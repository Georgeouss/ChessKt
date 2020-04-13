package utils

import game.Game
import pieces.Alliance
import pieces.King
import java.awt.Point

fun Game.isEnemyKingPosition(tile: Point, alliance: Alliance): Boolean {
    this.board.getPiece(tile.x, tile.y).also {
        return (it is King) && it.alliance != alliance
    }
}

fun Array<Point>.isPathEmpty(game: Game): Boolean {
    return this.mapNotNull { tile -> game.board.getPiece(tile.x, tile.y) }
        .isEmpty()
}