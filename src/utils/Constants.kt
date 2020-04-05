package utils

object Constants {
    const val BOARD_TILES_SIZE = 8
    const val BLACK_WIN_MESSAGE = "Checkmate! Black wins."
    const val WHITE_WIN_MESSAGE = "Checkmate! White wins."

    object PieceInitialPosition {
        const val WHITE_PAWNS_ROW = 6
        const val BLACK_PAWNS_ROW = 1
        const val BLACK_KING_ROW = 0
        const val WHITE_KING_ROW = 7
        const val FIRST_ROCK_COL = 0
        const val SECOND_ROCK_COL = 7
        const val FIRST_BISHOP_COL = 2
        const val SECOND_BISHOP_COL = 5
        const val FIRST_KNIGHT_COL = 3
        const val SECOND_KNIGHT_COL = 4
        const val KING_COL = 4
        const val QUEEN_COL = 4
    }
}