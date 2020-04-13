package pieces

import board.Board
import game.Game
import utils.Diagonal
import java.awt.Point
import kotlin.math.abs

class Bishop(x: Int, y: Int, alliance: Alliance, game: Game, board: Board) :
    Piece(x, y, alliance, game, board, "${alliance.string}-Bishop"), StraightLineMovable {

    override fun getValidMoves(): Array<Point> {
        return Diagonal.generateAll(x, y)
            .filter(::isValidMove)
            .toTypedArray()
    }

    override fun isValidMove(pointToMove: Point): Boolean {
        val difference = getMoveDifference(pointToMove)
        val tilePiece = getTilePiece(pointToMove)

        val isDiagonal = abs(difference.x) == abs(difference.y)

        val pathEmpty = isPathEmpty(pointToMove) && isDiagonal

        val isValidMove = isMoveInBounds(pointToMove) && pathEmpty &&
                (tilePiece == null || tilePiece.alliance != this.alliance)


        return isValidMove && !isInCheckAfterMove(pointToMove)
    }

    override fun getCoveredSquares(): Array<Point> {
        return Diagonal.getCoveredSquares(Point(x, y), game, alliance)
    }

    private fun isPathEmpty(move: Point): Boolean {
        return Diagonal.isPathEmpty(Point(x, y), move, game)
    }
}