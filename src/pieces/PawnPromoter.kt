package pieces

import utils.loadImage
import game.Game
import game.MouseEventType
import java.awt.*


class PawnPromoter(x: Int, y: Int, alliance: Alliance, private val game: Game) {
    private val queen = loadImage("${alliance.string}-Queen.png")
    private val rook = loadImage("${alliance.string}-Rook.png")
    private val bishop = loadImage("${alliance.string}-Bishop.png")
    private val knight = loadImage("${alliance.string}-Knight.png")

    private var visible = false;

    private val dir = if (alliance == Alliance.WHITE) 1 else -1

    private val queenRect =
        Rectangle(
            x * game.board.tileSize,
            (y + 0 * dir) * game.board.tileSize,
            game.board.tileSize,
            game.board.tileSize
        )
    private val bishopRect =
        Rectangle(
            x * game.board.tileSize,
            (y + 1 * dir) * game.board.tileSize,
            game.board.tileSize,
            game.board.tileSize
        )
    private val knightRect =
        Rectangle(
            x * game.board.tileSize,
            (y + 2 * dir) * game.board.tileSize,
            game.board.tileSize,
            game.board.tileSize
        )
    private val rookRect =
        Rectangle(
            x * game.board.tileSize,
            (y + 3 * dir) * game.board.tileSize,
            game.board.tileSize,
            game.board.tileSize
        )

    enum class Promotion {
        Queen,
        Rook,
        Bishop,
        Knight
    }

    public fun render(g: Graphics2D) {
        if (visible) {
            game.renderAbove {
                val opacity = 0.5
                g.color = Color(0, 0, 0, (255 * opacity).toInt())
                g.fillRect(0, 0, game.width, game.height)

                val ts = game.board.tileSize

                g.color = Color.lightGray
                g.fillRect(queenRect.x, queenRect.y, queenRect.width, queenRect.height)
                g.fillRect(bishopRect.x, bishopRect.y, bishopRect.width, bishopRect.height)
                g.fillRect(knightRect.x, knightRect.y, knightRect.width, knightRect.height)
                g.fillRect(rookRect.x, rookRect.y, rookRect.width, rookRect.height)

                g.color = Color.gray
                when {
                    queenRect.contains(game.mouse.point) -> {
                        g.fillRect(queenRect.x, queenRect.y, queenRect.width, queenRect.height)
                    }
                    bishopRect.contains(game.mouse.point) -> {
                        g.fillRect(bishopRect.x, bishopRect.y, bishopRect.width, bishopRect.height)
                    }
                    knightRect.contains(game.mouse.point) -> {
                        g.fillRect(knightRect.x, knightRect.y, knightRect.width, knightRect.height)
                    }
                    rookRect.contains(game.mouse.point) -> {
                        g.fillRect(rookRect.x, rookRect.y, rookRect.width, rookRect.height)
                    }
                }

                g.drawImage(queen, queenRect.x, queenRect.y, queenRect.width, queenRect.height, null)
                g.drawImage(bishop, bishopRect.x, bishopRect.y, bishopRect.width, bishopRect.height, null)
                g.drawImage(knight, knightRect.x, knightRect.y, knightRect.width, knightRect.height, null)
                g.drawImage(rook, rookRect.x, rookRect.y, rookRect.width, rookRect.height, null)
            }
        }
    }

    public fun choose(callback: (promotion: Promotion) -> Unit) {
        show()

        val listener = {
            if (visible) {
                var promotion: Promotion? = null
                when {
                    queenRect.contains(game.mouse.point) -> {
                        promotion = Promotion.Queen
                    }
                    bishopRect.contains(game.mouse.point) -> {
                        promotion = Promotion.Bishop
                    }
                    knightRect.contains(game.mouse.point) -> {
                        promotion = Promotion.Knight
                    }
                    rookRect.contains(game.mouse.point) -> {
                        promotion = Promotion.Rook
                    }
                }

                if (promotion != null) {
                    callback(promotion)
                    hide()

                    game.mouse.isDown = false
                    game.mouse.selected = null
                    game.cursor = Cursor(Cursor.DEFAULT_CURSOR)
                }
            }
        }

        game.mouse.addEventListener(MouseEventType.Pressed, listener, removeAfterCall = false)
    }

    private fun show() {
        visible = true
        game.overlayed = true
    }

    private fun hide() {
        visible = false
        game.overlayed = false
    }
}