package watermark

import java.awt.Transparency
import java.io.File
import javax.imageio.ImageIO

fun main() {
    println("Input the image filename:")
    val filename = readln().trim()

    val file = File(filename)
    if (!file.exists()) {
        println("The file $filename doesn't exist.")
        return
    }

    val image = ImageIO.read(file)
    println("Image file: $filename")
    println("Width: ${image.width}")
    println("Height: ${image.height}")
    println("Number of components: ${image.colorModel.numComponents}")
    println("Number of color components: ${image.colorModel.numColorComponents}")
    println("Bits per pixel: ${image.colorModel.pixelSize}")
    println("Transparency: ${getTransparency(image.transparency)}")
}

fun getTransparency(num: Int): String =
    when (num) {
        Transparency.OPAQUE -> "OPAQUE"
        Transparency.BITMASK -> "BITMASK"
        Transparency.TRANSLUCENT -> "TRANSLUCENT"
        else -> ""
    }