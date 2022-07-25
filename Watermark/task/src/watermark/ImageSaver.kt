package watermark

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

object ImageSaver {

    fun save(resultImage: BufferedImage, outputFilename: String) {
        try {
            val outputFile = File(outputFilename)
            ImageIO.write(resultImage, outputFile.extension, outputFile)
            println("The watermarked image $outputFilename has been created.")
        } catch (e: Exception) {
            println("Can't write output file!")
            exitProcess(-1)
        }
    }

}