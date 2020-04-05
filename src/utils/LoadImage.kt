package utils

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

private val imageCache = mutableMapOf<String, BufferedImage>()

public fun loadImage(path: String): BufferedImage {
    return if(imageCache.containsKey(path)) {
        imageCache[path]!!
    } else {
        val stream = ClassLoader.getSystemResourceAsStream(path)
        val image = ImageIO.read(stream)

        imageCache[path] = image

        image
    }
}