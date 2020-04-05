package pieces

import board.Board
import game.Game
import utils.Diagonal
import java.awt.Point
import java.awt.Rectangle
import kotlin.math.abs

class Bishop(x: Int, y: Int, alliance: Alliance, game: Game, board: Board) :
    Piece(x, y, alliance, game, board, "${alliance.string}-Bishop") {

    override fun isValidMove(move: Point): Boolean {
        val difference = getMoveDifference(move)
        val tilePiece = getTilePiece(move)

        val isDiagonal = abs(difference.x) == abs(difference.y)

        val pathEmpty = pathEmpty(move) && isDiagonal

        val isValidMove = isMoveInBounds(move) && pathEmpty &&
                (tilePiece == null || tilePiece.alliance != this.alliance)


        return isValidMove && !isInCheckAfterMove(move)
    }

    override fun getValidMoves(): Array<Point> {
        return Diagonal.generateAll(x, y).filter(::isValidMove).toTypedArray()
    }

    override fun getCoveredSquares(): Array<Point> {
        return Diagonal.generateAll(x, y).filter { pathEmpty(it, ignoreEnemyKing = true) }.toTypedArray()
    }

    private fun pathEmpty(move: Point, ignoreEnemyKing: Boolean = false): Boolean {
        for (tile in Diagonal.generateInBetween(x, y, move.x, move.y)) {
            val tilePiece = game.board.getPiece(tile.x, tile.y)

            if (
                tilePiece != null &&
                !(ignoreEnemyKing && (tilePiece is King) && tilePiece.alliance != this.alliance)
            ) {
                return false
            }
        }
        return true
    }
}