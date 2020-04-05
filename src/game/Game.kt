package game

import board.Board
import pieces.Alliance
import java.awt.*

// TODO: Check for checkmate
class Game(title: String) : Canvas(), Runnable {
    @Suppress("unused")
    public val window = Window(640, 640, title, this)

    private var fps = 60.0
    private var running = true

    private lateinit var thread: Thread
    public lateinit var board: Board
    public lateinit var mouse: Mouse

    private val above = ArrayList<(g: Graphics2D) -> Unit>()
    private val below = ArrayList<(g: Graphics2D) -> Unit>()
    private val linux = System.getProperty("os.name").indexOf("nux") > 0
    public var overlayed = false

    public val moves = ArrayList<Move>()
    public var turn = Alliance.WHITE

    public fun start(window: Window) {
        board = Board(this, window.width / 8)

        this.mouse = Mouse(this)
        addMouseListener(mouse)

        thread = Thread(this)
        thread.start()

        running = true
    }

    private fun render() {
        if (linux)
            Toolkit.getDefaultToolkit().sync()

        val bs = this.bufferStrategy
        if (bs == null) {
            this.createBufferStrategy(3); return
        }

        val g = bs.drawGraphics as Graphics2D

        board.render(g)

        below?.let {
            below.forEach {
                it(g)
            }
            below.clear()
        }

        board.renderPieces(g)

        above?.let {
            above.forEach {
                it(g)
            }
            above.clear()
        }

        g.color = Color.green
        g.font = Font("SansSerif", Font.PLAIN, 12)
        val str = "$fps FPS"
        g.drawString(str, width - str.length * 7, 12)

        g.dispose()
        bs.show()
    }

    private fun tick() {
        board.tick()
    }

    public fun addMove(move: Move) {
        this.moves.add(move)

        turn = when(turn) {
            Alliance.WHITE -> Alliance.BLACK
            Alliance.BLACK -> Alliance.WHITE
        }

        board.alertMoveAdded(move)
    }

    public fun renderBelow(func: (g: Graphics2D) -> Unit) {
        below.add(func)
    }

    public fun renderAbove(func: (g: Graphics2D) -> Unit) {
        above.add(func)
    }

    @Synchronized
    fun stop() {
        try {
            thread.join()
            running = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun run() {
        requestFocus()

        val ns = 1000000000 / fps
        var lastTime: Long = System.nanoTime()
        var delta = 0.0
        var timer: Long = System.currentTimeMillis()
        var frames = 0

        // Spillets hoved-loop
        while (running) {
            val now: Long = System.nanoTime()
            delta += (now - lastTime) / ns
            lastTime = now

            while (delta >= 1) {
                tick()
                delta--
            }

            if (running)
                render()

            frames++

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000
                fps = frames.toDouble()

                frames = 0
            }
        }
    }
}
