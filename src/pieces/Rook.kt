package pieces

import board.Board
import game.Game
import utils.DirectionMovable
import utils.Directions
import utils.Horizontal
import utils.Vertical
import java.awt.Point
import kotlin.math.abs

class Rook(x: Int, y: Int, alliance: Alliance, game: Game, board: Board) :
    Piece(x, y, alliance, game, board, "${alliance.string}-Rook") {

    override fun isValidMove(pointToMove: Point): Boolean {
        val diff = Point(pointToMove.x - x, pointToMove.y - y)
        val tilePiece = game.board.getPiece(pointToMove.x, pointToMove.y)

        val isHorizontal = diff.y == 0 && abs(diff.x) > 0
        val isVertical = diff.x == 0 && abs(diff.y) > 0

        val direction = when {
            isHorizontal -> Directions.Horizontal
            isVertical -> Directions.Vertical
            else -> null
        }

        val isValid = isMoveInBounds(pointToMove) && (isHorizontal || isVertical) &&
                isPathEmpty(pointToMove, direction!!.obj) && (tilePiece == null || tilePiece.alliance != this.alliance)

        return isValid && isInCheckAfterMove(pointToMove)
    }

    private fun isPathEmpty(move: Point, directionMovable: DirectionMovable): Boolean {
        return directionMovable.isPathEmpty(Point(x, y), move, game)
    }

    override fun getValidMoves(): Array<Point> {
        val horizontal = Horizontal.generateAll(x, y).filter(::isValidMove).toTypedArray()
        val vertical = Vertical.generateAll(x, y).filter(::isValidMove).toTypedArray()

        return arrayOf(*horizontal, *vertical)
    }

    override fun getCoveredSquares(): Array<Point> {
        val horizontal = Horizontal.getCoveredSquares(Point(x, y), game, alliance)
        val vertical = Vertical.getCoveredSquares(Point(x, y), game, alliance)

        return arrayOf(*horizontal, *vertical)
    }
}