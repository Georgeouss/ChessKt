package utils

import utils.Constants.BOARD_TILES_SIZE
import java.awt.Point
import java.awt.Rectangle
import kotlin.math.abs
import kotlin.math.max

interface Direction {
    fun generateAll(x: Int, y: Int): Array<Point>
    fun generateInBetween(x: Int, y: Int, dx: Int, dy: Int): Array<Point>
}

enum class Directions(val obj: Direction) {
    Diagonal(utils.Diagonal),
    Vertical(utils.Vertical),
    Horizontal(utils.Horizontal),
}

object Diagonal: Direction {
    override fun generateAll(x: Int, y: Int): Array<Point> {
        val bounds = Rectangle(0, 0, BOARD_TILES_SIZE, BOARD_TILES_SIZE)
        val point = Point(x, y)
        val directions = arrayOf(arrayOf(1, 1), arrayOf(1, -1), arrayOf(-1, 1), arrayOf(-1, -1))
        val list = ArrayList<Point>()

        for (dir in directions) {
            point.x = x + dir[0]
            point.y = y + dir[1]

            while (bounds.contains(point)) {
                list.add(Point(point.x, point.y))
                point.x += dir[0]
                point.y += dir[1]
            }
        }

        return list.toTypedArray()
    }

    override fun generateInBetween(x: Int, y: Int, dx: Int, dy: Int): Array<Point> {
        val bounds = Rectangle(0, 0, BOARD_TILES_SIZE, BOARD_TILES_SIZE)
        val diff = Point(dx - x, dy - y)
        val dir = Point(diff.x / max(1, abs(diff.x)), diff.y / max(1, abs(diff.y)))
        val point = Point(x + dir.x, y + dir.y)

        val list = ArrayList<Point>()

        while (bounds.contains(point) && !(point.x == dx && point.y == dy)) {
            list.add(Point(point.x, point.y))
            point.x += dir.x
            point.y += dir.y
        }

        return list.toTypedArray()
    }
}

object Vertical: Direction {
    override fun generateAll(x: Int, y: Int): Array<Point> {
        val list = ArrayList<Point>()
        for (i in 0..BOARD_TILES_SIZE) {
            if (i != y)
                list.add(Point(x, i))
        }
        return list.toTypedArray()
    }

    override fun generateInBetween(x: Int, y: Int, dx: Int, dy: Int): Array<Point> {
        val bounds = Rectangle(0, 0, BOARD_TILES_SIZE, BOARD_TILES_SIZE)
        val list = ArrayList<Point>()
        val dir = if(y < dy) 1 else -1

        val point = Point(x, y + dir)

        while(bounds.contains(point) && !(point.x == dx && point.y == dy)) {
            list.add(Point(point.x, point.y))
            point.y += dir
        }

        return list.toTypedArray()
    }
}

object Horizontal: Direction {
    override fun generateInBetween(x: Int, y: Int, dx: Int, dy: Int): Array<Point> {
        val bounds = Rectangle(0, 0, BOARD_TILES_SIZE, BOARD_TILES_SIZE)
        val list = ArrayList<Point>()
        val dir = if(x < dx) 1 else -1

        val point = Point(x + dir, y)

        while(bounds.contains(point) && !(point.x == dx && point.y == dy)) {
            list.add(Point(point.x, point.y))
            point.x += dir
        }

        return list.toTypedArray()
    }

    override fun generateAll(x: Int, y: Int): Array<Point> {
        val list = ArrayList<Point>()
        for (i in 0..BOARD_TILES_SIZE) {
            if (i != x)
                list.add(Point(i, y))
        }
        return list.toTypedArray()
    }
}

