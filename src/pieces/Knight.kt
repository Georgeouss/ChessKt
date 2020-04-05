package pieces

import board.Board
import game.Game
import java.awt.Point
import java.awt.Rectangle
import kotlin.math.abs

class Knight(x: Int, y: Int, alliance: Alliance, game: Game, board: Board) :
    Piece(x, y, alliance, game, board, "${alliance.string}-Knight") {

    override fun isValidMove(move: Point): Boolean {
        val difference = Point(move.x - x, move.y - y)
        val tilePiece = game.board.getPiece(move.x, move.y)

        val isValidMove =  isMoveInBounds(move) && (tilePiece == null || tilePiece.alliance != this.alliance)
                && ((abs(difference.x) == 1 && abs(difference.y) == 2) || (abs(difference.y) == 1 && abs(difference.x) == 2))

        return isValidMove && !isInCheckAfterMove(move)
    }

    override fun getValidMoves(): Array<Point> {
        return getCoveredSquares().filter(::isValidMove).toTypedArray()
    }

    override fun getCoveredSquares(): Array<Point> {
        return arrayOf(
            Point(x + 1, y - 2),
            Point(x + 2, y - 1),
            Point(x + 2, y + 1),
            Point(x + 1, y + 2),
            Point(x - 1, y + 2),
            Point(x - 2, y - 1),
            Point(x - 2, y + 1),
            Point(x - 1, y - 2)
        )
    }
}