package pieces

import board.Board
import game.Game
import game.MouseEventType
import game.Move
import utils.Constants.BOARD_TILES_SIZE
import utils.loadImage
import java.awt.*
import kotlin.math.roundToInt

abstract class Piece(
    public var x: Int,
    public var y: Int,
    public val alliance: Alliance,
    protected val game: Game,
    board: Board,
    imageName: String
) {
    private var eventListenerAdded = false
    private var renderX = x * board.tileSize;
    private var renderY = y * board.tileSize;

    private var validMovesCache: Array<Point>? = null

    private val renderTile: Point
        get() = run {
            val x = (renderX.toFloat() / game.board.tileSize).roundToInt()
            val y = (renderY.toFloat() / game.board.tileSize).roundToInt()

            return Point(x, y)
        }

    private var image = loadImage("$imageName.png")

    protected abstract fun isValidMove(pointToMove: Point): Boolean

    public abstract fun getValidMoves(): Array<Point>
    public abstract fun getCoveredSquares(): Array<Point>

    protected open fun testMove(move: Point): () -> Unit {
        val prev = Point(this.x, this.y)
        val tilePiece = game.board.getPiece(move)

        game.board.setPiece(prev, null)
        game.board.setPiece(move, this)

        return {
            game.board.setPiece(prev, this)
            game.board.setPiece(move, tilePiece)
        }
    }

    public open fun render(g: Graphics2D) {
        val alpha = 0.65
        val highlightColor = Color(192, 192, 192, (255 * alpha).roundToInt())
        val ts = game.board.tileSize

        if (game.turn == this.alliance && game.mouse.selected == this && game.mouse.isDown) {
            game.renderAbove {
                g.drawImage(image, renderX, renderY, ts, ts, null)
            }
            game.renderBelow { bg: Graphics2D ->
                val oldStroke = bg.stroke
                val stroke = 6f

                bg.color = highlightColor
                bg.stroke = BasicStroke(stroke)

                val moves = getValidMovesCached()

                moves.forEach {
                    bg.drawRect(
                        ((it.x * ts) + stroke / 2).toInt(),
                        ((it.y * ts) + stroke / 2).toInt(),
                        (ts - stroke).toInt(),
                        (ts - stroke).toInt()
                    )
                }
                bg.stroke = oldStroke

                if (moves.any { it.x == renderTile.x && it.y == renderTile.y }) {
                    bg.color = highlightColor
                    bg.fillRect(renderTile.x * ts, renderTile.y * ts, ts, ts)
                }
            }

            g.color = highlightColor
            g.fillRect(x * ts, y * ts, ts, ts)

            g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)
            g.drawImage(image, x * ts, y * ts, ts, ts, null)
            g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)
        } else {
            g.drawImage(image, renderX, renderY, ts, ts, null)
        }
    }

    private fun getValidMovesCached(): Array<Point> {
        return if (validMovesCache == null) {
            validMovesCache = getValidMoves()
            validMovesCache!!
        } else {
            validMovesCache!!
        }
    }

    public fun tick() {
        val ts = game.board.tileSize
        val bounds = Rectangle(x * ts, y * ts, ts, ts)

        if (!game.overlayed && bounds.contains(game.mouse.point) && game.mouse.isDown && game.mouse.selected == null) {
            game.mouse.selected = this
            validMovesCache = getValidMoves()

            if (!eventListenerAdded) {
                game.mouse.addEventListener(MouseEventType.Released, {
                    validMovesCache = null
                    if (game.mouse.selected == this) {
                        eventListenerAdded = false

                        game.mouse.isDown = false
                        game.mouse.selected = null

                        if (game.turn == this.alliance)
                            makeMove(Point(renderTile.x, renderTile.y))

                        renderX = x * ts
                        renderY = y * ts
                    } else {
                        game.mouse.selected = null
                    }
                })
                eventListenerAdded = true
            }
        }

        if (game.mouse.selected == this) {
            renderX = game.mouse.x - ts / 2
            renderY = game.mouse.y - ts / 2
        } else {
            renderX = x * ts
            renderY = y * ts
        }
    }

    protected open fun makeMove(destination: Point) {
        if (isValidMove(destination)) {
            val prev = Point(this.x, this.y)

            game.board.setPiece(prev, null)
            game.board.setPiece(destination, this)

            game.addMove(Move(prev.x, prev.y, destination.x, destination.y, this))
        }
    }

    protected fun isMoveInBounds(point: Point): Boolean {
        return Rectangle(0, 0, BOARD_TILES_SIZE, BOARD_TILES_SIZE)
            .contains(point.x, point.y)
    }

    protected fun getMoveDifference(point: Point): Point {
        return Point(point.x - x, point.y - y)
    }

    protected fun getTilePiece(point: Point) = game.board.getPiece(point.x, point.y)

    protected fun isInCheckAfterMove(point: Point): Boolean {
        val king = if (alliance == Alliance.WHITE) game.board.whiteKing else game.board.blackKing
        val takeBack = this.testMove(point)
        val result = king.inCheck()
        takeBack()
        return result
    }
}