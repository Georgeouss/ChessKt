package pieces

import board.Board
import game.Game
import java.awt.Point
import kotlin.math.abs

class King(x: Int, y: Int, alliance: Alliance, game: Game, board: Board) :
    Piece(x, y, alliance, game, board, "${alliance.string}-King") {

    private var hasMoved = false

    fun isMated(): Boolean {
        return this.inCheck() && !game.board.tiles.reduce { a, b -> a.plus(b) }.any {
            it.piece != null && it.piece!!.alliance == this.alliance && it.piece!!.getValidMoves().isNotEmpty()
        }
    }

    override fun getValidMoves(): Array<Point> {
        val castingMoves = arrayOf(Point(x + 2, y + 0), Point(x - 2, y + 0))
        return getCoveredSquares().plus(castingMoves)
            .filter(::isValidMove).toTypedArray()
    }

    public fun inCheck(): Boolean {
        return inCheck(Point(this.x, this.y))
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

    override fun makeMove(destination: Point) {
        if (isValidCastleMove(destination)) {
            this.castle(destination)
            hasMoved = true
        } else if (isValidMove(destination)) {
            hasMoved = true
            super.makeMove(destination)
        }
    }

    override fun isValidMove(pointToMove: Point): Boolean {
        val difference = getMoveDifference(pointToMove)
        val tilePiece = getTilePiece(pointToMove)
        val checked = inCheck(pointToMove)

        return isMoveInBounds(pointToMove) && !checked && (tilePiece == null || tilePiece.alliance != this.alliance) &&
                (abs(difference.x) + abs(difference.y) == 1 || (abs(difference.x) == abs(difference.y) && abs(difference.y) == 1))
                || isValidCastleMove(pointToMove)
    }

    private fun castle(move: Point) {
        if (isValidCastleMove(move)) {
            val diff = Point(move.x - x, move.y - y)
            val dir = diff.x / abs(diff.x)
            val long = dir == -1
            val length = if (long) 4 else 3
            val rook = game.board.getPiece(x + length * dir, y)!!

            super.makeMove(move)
            game.board.setPiece(rook.x, rook.y, null)
            game.board.setPiece(move.x + (dir * -1), move.y, rook)
        }
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
                        && !inCheck(Point(x + it, y))
            }
            val cornerPiece = game.board.getPiece(x + (length * dir) + dir, y)
            val rookInCorner = cornerPiece != null && (cornerPiece is Rook)

            !hasMoved && pathClear && rookInCorner
        } else {
            false
        }
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