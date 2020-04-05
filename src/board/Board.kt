package board

import game.Game
import game.Move
import pieces.*
import utils.Constants.BLACK_WIN_MESSAGE
import utils.Constants.BOARD_TILES_SIZE
import utils.Constants.PieceInitialPosition.FIRST_BISHOP_COL
import utils.Constants.PieceInitialPosition.FIRST_KNIGHT_COL
import utils.Constants.PieceInitialPosition.FIRST_ROCK_COL
import utils.Constants.PieceInitialPosition.KING_COL
import utils.Constants.PieceInitialPosition.QUEEN_COL
import utils.Constants.PieceInitialPosition.SECOND_BISHOP_COL
import utils.Constants.PieceInitialPosition.SECOND_KNIGHT_COL
import utils.Constants.PieceInitialPosition.SECOND_ROCK_COL
import utils.Constants.WHITE_WIN_MESSAGE
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import javax.swing.JOptionPane
import kotlin.system.exitProcess

class Board(private val game: Game, public val tileSize: Int) {
    val tiles: Array<Array<Tile>> = Array(BOARD_TILES_SIZE) { column ->
        Array(BOARD_TILES_SIZE) { row ->
            Tile(row, column)
        }
    }

    public lateinit var whiteKing: King
    public lateinit var blackKing: King

    private var whiteKingInCheck = false
    private var blackKingInCheck = false

    private var whiteKingMated = false
    private var blackKingMated = false

    init {
        placePieceSet(Alliance.BLACK)
        placePieceSet(Alliance.WHITE)
    }

    private fun placePieceSet(alliance: Alliance) {
        placePawns(alliance)
        placeBishops(alliance)
        placeKing(alliance)
        placeKnights(alliance)
        placeQueen(alliance)
        placeRocks(alliance)
    }

    private fun placePawns(alliance: Alliance) {
        val rowIndex = alliance.getPawnRowIndex()
        repeat(BOARD_TILES_SIZE) { x ->
            addPiece(Pawn(x, rowIndex, alliance, game, this))
        }
    }

    private fun placeRocks(alliance: Alliance) {
        val rowIndex = alliance.getKingRowIndex()
        addPiece(Rook(FIRST_ROCK_COL, rowIndex, alliance, game, this))
        addPiece(Rook(SECOND_ROCK_COL, rowIndex, alliance, game, this))
    }

    private fun placeBishops(alliance: Alliance) {
        val rowIndex = alliance.getKingRowIndex()
        addPiece(Bishop(FIRST_BISHOP_COL, rowIndex, alliance, game, this))
        addPiece(Bishop(SECOND_BISHOP_COL, rowIndex, alliance, game, this))
    }

    private fun placeKnights(alliance: Alliance) {
        val rowIndex = alliance.getKingRowIndex()
        addPiece(Knight(FIRST_KNIGHT_COL, rowIndex, alliance, game, this))
        addPiece(Knight(SECOND_KNIGHT_COL, rowIndex, alliance, game, this))
    }

    private fun placeKing(alliance: Alliance) {
        val rowIndex = alliance.getKingRowIndex()
        val king = King(KING_COL, rowIndex, alliance, game, this)
        when (alliance) {
            Alliance.WHITE -> whiteKing = king
            Alliance.BLACK -> blackKing = king
        }
        addPiece(king)
    }

    private fun placeQueen(alliance: Alliance) {
        val rowIndex = alliance.getKingRowIndex()
        addPiece(Queen(QUEEN_COL, rowIndex, alliance, game, this))
    }

    fun render(g: Graphics2D) {
        tiles.forEachIndexed { y, row ->
            row.forEachIndexed { x, tile ->
                g.color = tile.color
                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize)
            }
        }

        g.color = Color(255, 0, 0, 127)

        if (whiteKingInCheck)
            g.fillOval(whiteKing.x * tileSize, whiteKing.y * tileSize, tileSize, tileSize)

        if (blackKingInCheck)
            g.fillOval(blackKing.x * tileSize, blackKing.y * tileSize, tileSize, tileSize)
    }

    fun renderPieces(g: Graphics2D) {
        tiles.forEach {
            it.forEach { tile ->
                tile.piece?.let {
                    tile.piece!!.render(g)
                }
            }
        }
    }

    fun tick() {
        tiles.forEach {
            it.forEach { tile ->
                tile.piece?.let {
                    tile.piece!!.tick()
                }
            }
        }
    }

    public fun alertMoveAdded(move: Move) {
        whiteKingInCheck = whiteKing.inCheck()
        blackKingInCheck = blackKing.inCheck()

        whiteKingMated = whiteKing.isMated()
        blackKingMated = blackKing.isMated()

        val frame = game.window.frame

        if (whiteKingMated) {
            JOptionPane.showMessageDialog(frame, BLACK_WIN_MESSAGE)
            exitProcess(0)
        }

        if (blackKingMated) {
            JOptionPane.showMessageDialog(frame, WHITE_WIN_MESSAGE)
            exitProcess(0)
        }
    }

    private fun addPiece(piece: Piece) {
        tiles[piece.y][piece.x].piece = piece
    }

    public fun setPiece(point: Point, piece: Piece?) {
        setPiece(point.x, point.y, piece); }

    public fun setPiece(x: Int, y: Int, piece: Piece?) {
        tiles[y][x].piece = piece
        piece?.x = x
        piece?.y = y
    }

    public fun getPiece(point: Point): Piece? {
        return getPiece(point.x, point.y); }

    public fun getPiece(x: Int, y: Int): Piece? {
        return if (Rectangle(0, 0, BOARD_TILES_SIZE, BOARD_TILES_SIZE).contains(x, y)) tiles[y][x].piece else null
    }
}
