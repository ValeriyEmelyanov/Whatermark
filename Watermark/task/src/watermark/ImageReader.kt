package watermark

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

const val NUMBER_OF_COLOR_COMPONENTS = 3
const val BIT_PER_PIXEL_24 = 24
const val BIT_PER_PIXEL_32 = 32

object ImageReader {

    fun readFromFile(messages: Messages): BufferedImage {
        println(messages.askInputName)
        val filename = readln().trim()

        val file = File(filename)
        if (!file.exists()) {
            println("The file $filename doesn't exist.")
            exitProcess(-1)
        }

        val image = ImageIO.read(file)

        if (image.colorModel.numComponents != NUMBER_OF_COLOR_COMPONENTS &&
            image.colorModel.numComponents != NUMBER_OF_COLOR_COMPONENTS + if (messages == Messages.WATERMARK) 1 else 0
        ) {
            println(messages.wrongImageColorComponentsNumber)
            exitProcess(-1)
        }
        if (image.colorModel.pixelSize != BIT_PER_PIXEL_24 && image.colorModel.pixelSize != BIT_PER_PIXEL_32) {
            println(messages.thisIsNot24Or32Bit)
            exitProcess(-1)
        }

        return image
    }

}