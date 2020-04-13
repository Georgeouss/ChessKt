package pieces

import board.Board
import game.Game
import utils.*
import java.awt.Point
import kotlin.math.abs

class Queen(x: Int, y: Int, alliance: Alliance, game: Game, board: Board) :
    Piece(x, y, alliance, game, board, "${alliance.string}-Queen") {

    override fun getValidMoves(): Array<Point> {
        val diagonal = Diagonal.generateAll(x, y).filter(::isValidMove).toTypedArray()
        val horizontal = Horizontal.generateAll(x, y).filter(::isValidMove).toTypedArray()
        val vertical = Vertical.generateAll(x, y).filter(::isValidMove).toTypedArray()

        return arrayOf(*diagonal, *horizontal, *vertical)
    }

    override fun isValidMove(pointToMove: Point): Boolean {
        val diff = getMoveDifference(pointToMove)
        val tilePiece = getTilePiece(pointToMove)

        val isDiagonal = abs(diff.x) == abs(diff.y)
        val isHorizontal = diff.y == 0 && abs(diff.x) > 0
        val isVertical = diff.x == 0 && abs(diff.y) > 0

        val direction = when {
            isDiagonal -> Directions.Diagonal
            isHorizontal -> Directions.Horizontal
            isVertical -> Directions.Vertical
            else -> null
        }

        val isValid = isMoveInBounds(pointToMove) && (isDiagonal || isHorizontal || isVertical) &&
                pathEmpty(pointToMove, direction!!.obj) && (tilePiece == null || tilePiece.alliance != this.alliance)

        return isValid && isInCheckAfterMove(pointToMove)
    }

    override fun getCoveredSquares(): Array<Point> {
        val diagonal = Diagonal.getCoveredSquares(Point(x, y), game, alliance)
        val horizontal = Horizontal.getCoveredSquares(Point(x, y), game, alliance)
        val vertical = Vertical.getCoveredSquares(Point(x, y), game, alliance)

        return arrayOf(*diagonal, *horizontal, *vertical)
    }

    private fun pathEmpty(move: Point, directionMovable: DirectionMovable): Boolean {
        return directionMovable.isPathEmpty(Point(x, y), move, game)
    }
}