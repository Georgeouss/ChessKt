package pieces

import board.Board
import game.Game
import java.awt.Point
import java.awt.Rectangle
import kotlin.math.abs

class King(x: Int, y: Int, alliance: Alliance, game: Game, board: Board) :
    Piece(x, y, alliance, game, board, "${alliance.string}-King") {

    private var hasMoved = false

    override fun isValidMove(move: Point): Boolean {
        val difference = getMoveDifference(move)
        val tilePiece = getTilePiece(move)
        val checked = inCheck(move)

        return isMoveInBounds(move) && !checked && (tilePiece == null || tilePiece.alliance != this.alliance) &&
                (abs(difference.x) + abs(difference.y) == 1 || (abs(difference.x) == abs(difference.y) && abs(difference.y) == 1))
                || isValidCastleMove(move)
    }

    private fun isValidCastleMove(move: Point): Boolean {
        val difference = getMoveDifference(move)

        return if (!inCheck() && abs(difference.x) == 2 && difference.y == 0) {
            val dir = difference.x / abs(difference.x)
            val long = dir == -1
            val length = if (long) 3 else 2

            val path = if (long) dir downTo length * dir else dir..(length * dir)
            val pathClear = path.toList().all {
                game.board.getPiece(x + it, y) == null
                        && !inCheck(x + it, y)
            }
            val cornerPiece = game.board.getPiece(x + (length * dir) + dir, y)
            val rookInCorner = cornerPiece != null && (cornerPiece is Rook)

            !hasMoved && pathClear && rookInCorner
        } else {
            false
        }
    }

    private fun inCheck(move: Point): Boolean {
        for (row in game.board.tiles) {
            for (tile in row) {
                val piece = tile.piece

                if (piece != null && piece.alliance != this.alliance) {
                    val checked = piece.getCoveredSquares().any { it.x == move.x && it.y == move.y }

                    if (checked) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun inCheck(x: Int, y: Int): Boolean {
        return inCheck(Point(x, y))
    }

    public fun inCheck(): Boolean {
        return inCheck(Point(this.x, this.y))
    }

    fun isMated(): Boolean {
        return this.inCheck() && !game.board.tiles.reduce { a, b -> a.plus(b) }.any {
            it.piece != null && it.piece!!.alliance == this.alliance && it.piece!!.getValidMoves().isNotEmpty()
        }
    }

    override fun move(move: Point) {
        if (isValidCastleMove(move)) {
            this.castle(move, validated = true)
            hasMoved = true
        } else if (isValidMove(move)) {
            hasMoved = true
            super.move(move)
        }
    }

    @Suppress("SameParameterValue")
    private fun castle(move: Point, validated: Boolean = true) {
        if (validated || isValidCastleMove(move)) {
            val diff = Point(move.x - x, move.y - y)
            val dir = diff.x / abs(diff.x)
            val long = dir == -1
            val length = if (long) 4 else 3
            val rook = game.board.getPiece(x + length * dir, y)!!

            super.move(move)
            game.board.setPiece(rook.x, rook.y, null)
            game.board.setPiece(move.x + (dir * -1), move.y, rook)
        }
    }

    override fun getValidMoves(): Array<Point> {
        val castingMoves = arrayOf(Point(x + 2, y + 0), Point(x - 2, y + 0))
        return getCoveredSquares().plus(castingMoves)
            .filter(::isValidMove).toTypedArray()
    }

    override fun getCoveredSquares(): Array<Point> {
        return arrayOf(
            Point(x - 1, y - 1),
            Point(x + 0, y - 1),
            Point(x + 1, y - 1),
            Point(x - 1, y + 0),
            Point(x + 1, y + 0),
            Point(x - 1, y + 1),
            Point(x + 0, y + 1),
            Point(x + 1, y + 1)
        )
    }
}