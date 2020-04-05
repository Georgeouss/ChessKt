package pieces

import utils.Constants.PieceInitialPosition.BLACK_KING_ROW
import utils.Constants.PieceInitialPosition.BLACK_PAWNS_ROW
import utils.Constants.PieceInitialPosition.WHITE_KING_ROW
import utils.Constants.PieceInitialPosition.WHITE_PAWNS_ROW

enum class Alliance(val string: String) {
    WHITE("White"),
    BLACK("Black");

    fun getPawnRowIndex(): Int {
        return when (this) {
            WHITE -> WHITE_PAWNS_ROW
            BLACK -> BLACK_PAWNS_ROW
        }
    }

    fun getKingRowIndex(): Int {
        return when (this) {
                WHITE -> WHITE_KING_ROW
                BLACK -> BLACK_KING_ROW
            }
    }
}