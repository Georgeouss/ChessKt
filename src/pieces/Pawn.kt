package pieces

import board.Board
import game.Game
import game.Move
import utils.Constants.PieceInitialPosition.BLACK_KING_ROW
import utils.Constants.PieceInitialPosition.WHITE_KING_ROW
import java.awt.Cursor
import java.awt.Graphics2D
import java.awt.Point
import kotlin.math.abs

class Pawn(x: Int, y: Int, alliance: Alliance, game: Game, board: Board) :
    Piece(x, y, alliance, game, board, "${alliance.string}-Pawn") {

    private var promoter: PawnPromoter? = null

    override fun render(g: Graphics2D) {
        if (promoter != null) {
            promoter!!.render(g)
        } else {
            super.render(g)
        }
    }

    // Overridden because of position specific rules
    override fun makeMove(destination: Point) {
        val backrank = if (alliance == Alliance.WHITE) BLACK_KING_ROW else WHITE_KING_ROW
        val isValid = this.isValidMove(destination)

        if (isValid && destination.y == backrank) {
            game.cursor = Cursor(Cursor.DEFAULT_CURSOR)

            this.promoter = PawnPromoter(destination.x, destination.y, alliance, game)

            promoter!!.choose { promotion: PawnPromoter.Promotion ->
                val piece = pieceFrom(promotion, destination.x, destination.y)

                game.board.setPiece(x, y, null)

                game.mouse.isDown = false
                game.mouse.selected = null

                game.board.setPiece(destination, piece)

                game.addMove(Move(x, y, destination.x, destination.y, piece))
            }
        } else if (isValid && isValidEnPassant(destination)) {
            this.enPassant(destination)
        } else if (isValid) {
            super.makeMove(destination)
        }
    }

    override fun getValidMoves(): Array<Point> {
        val dir = if (alliance == Alliance.WHITE) -1 else 1

        return arrayOf(
            Point(x, y + 1 * dir),
            Point(x, y + 2 * dir),
            Point(x + 1, y + 1 * dir),
            Point(x - 1, y + 1 * dir)
        ).filter(::isValidMove).toTypedArray()
    }

    override fun isValidMove(pointToMove: Point): Boolean {
        val dir = if (alliance == Alliance.WHITE) -1 else 1
        val backrank = alliance.getPawnRowIndex()
        val difference = getMoveDifference(pointToMove)
        val tilePiece = getTilePiece(pointToMove)
        val isCapture = (difference.x == 1 || difference.x == -1) && difference.y == dir

        val enPassant = isValidEnPassant(pointToMove)

        val isValidMove =
            (difference.x == 0 && tilePiece == null && (difference.y == dir || (difference.y == 2 * dir && y == backrank)))
                    || isCapture && (enPassant || (tilePiece != null && tilePiece.alliance != this.alliance))

        return isValidMove && isInCheckAfterMove(pointToMove)
    }

    override fun getCoveredSquares(): Array<Point> {
        val dir = if (alliance == Alliance.WHITE) -1 else 1
        return arrayOf(
            Point(x + 1, y + dir),
            Point(x - 1, y + dir)
        )
    }

    private fun isValidEnPassant(move: Point): Boolean {
        if (game.moves.size == 0) return false

        val diff = Point(move.x - x, move.y - y)
        val dir = if (alliance == Alliance.WHITE) -1 else 1

        if (abs(diff.x) == 1 && diff.y == dir) {
            val capturee = game.board.getPiece(x + diff.x, y)
            val lastMove = game.moves.last()
            val lastMoveDiff = Point(lastMove.dx - lastMove.x, lastMove.dy - lastMove.y)

            if (
                capturee != null && capturee is Pawn && capturee.alliance != this.alliance
                && lastMove.piece == capturee && abs(lastMoveDiff.y) == 2
            ) {
                return true
            }
        }
        return false
    }


    private fun enPassant(move: Point) {
        val diff = Point(move.x - x, move.y - y)
        val captureePosition = Point(x + diff.x, y)

        super.makeMove(move)

        game.board.setPiece(captureePosition, null)
    }

    private fun pieceFrom(promotion: PawnPromoter.Promotion, x: Int, y: Int): Piece {
        return when (promotion) {
            PawnPromoter.Promotion.Queen -> {
                Queen(x, y, alliance, game, game.board)
            }
            PawnPromoter.Promotion.Bishop -> {
                Bishop(x, y, alliance, game, game.board)
            }
            PawnPromoter.Promotion.Rook -> {
                Rook(x, y, alliance, game, game.board)
            }
            PawnPromoter.Promotion.Knight -> {
                Knight(x, y, alliance, game, game.board)
            }
        }
    }

}