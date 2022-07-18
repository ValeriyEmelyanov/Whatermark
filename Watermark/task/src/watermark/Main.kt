package watermark

import java.awt.Color
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

const val NUMBER_OF_COLOR_COMPONENTS = 3
const val BIT_PER_PIXEL_24 = 24
const val BIT_PER_PIXEL_32 = 32

fun main() {
    val image = getImage(Messages.IMAGE)
    val watermark = getImage(Messages.WATERMARK)
    checkImageSizes(image, watermark)

    val useAlpha = checkUsingAlpha(watermark)
    val transColor = getTransColor(watermark)
    val watermarkTranspPercentage = getWatermarkTranspPercentage()

    val outputFilename = getOutputFilename()

    val resultImage = blendImages(image, watermark, watermarkTranspPercentage, useAlpha, transColor)

    saveImage(resultImage, outputFilename)
}

fun getImage(messages: Messages): BufferedImage {
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

fun checkImageSizes(image1: BufferedImage, image2: BufferedImage) {
    if (image1.width != image2.width || image1.height != image2.height) {
        println("The image and watermark dimensions are different.")
        exitProcess(-1)
    }
}

fun checkUsingAlpha(watermark: BufferedImage): Boolean {
    if (watermark.transparency == Transparency.TRANSLUCENT) {
        println("Do you want to use the watermark's Alpha channel?")
        val answer = readln()
        return answer.lowercase() == "yes"
    }
    return false
}

fun getTransColor(watermark: BufferedImage): Pair<Boolean, Color> {
    if (watermark.transparency == Transparency.TRANSLUCENT) return Pair(false, Color.WHITE)

    println("Do you want to set a transparency color?")
    val answer = readln()
    if (answer.lowercase() != "yes") return Pair(false, Color.WHITE)

    println("Input a transparency color ([Red] [Green] [Blue]):")
    try {
        val rgb = readln().split(" ").map { s -> s.toInt() }
        if (rgb.size != 3) throw IllegalArgumentException()
        return Pair((true), Color(rgb[0], rgb[1], rgb[2]))
    } catch (e: Exception) {
        println("The transparency color input is invalid.")
        exitProcess(-1)
    }
}

fun getWatermarkTranspPercentage(): Int {
    println("Input the watermark transparency percentage (Integer 0-100):")
    val sPercentage = readln()
    if (!sPercentage.matches(Regex("""\d+"""))) {
        println("The transparency percentage isn't an integer number.")
        exitProcess(-1)
    }

    val percentage = sPercentage.toInt()
    if (!(percentage in 0..100)) {
        println("The transparency percentage is out of range.")
        exitProcess(-1)
    }

    return percentage
}

fun getOutputFilename(): String {
    println("Input the output image filename (jpg or png extension):")
    val filename = readln()

    if (!filename.endsWith(".jpg") && !filename.endsWith(".png")) {
        println("The output file extension isn't \"jpg\" or \"png\".")
        exitProcess(-1)
    }

    return filename
}

fun blendImages(
    image: BufferedImage,
    watermark: BufferedImage,
    weight: Int,
    useAlpha: Boolean,
    transColor: Pair<Boolean, Color>
): BufferedImage {
    val result = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val i = Color(image.getRGB(x, y))
            val w = if (useAlpha) Color(watermark.getRGB(x, y), true) else Color(watermark.getRGB(x, y))

            val color = if (useAlpha && w.alpha == 0 ||
                transColor.first && transColor.second == w
            ) i else Color(
                mixColors(w.red, i.red, weight),
                mixColors(w.green, i.green, weight),
                mixColors(w.blue, i.blue, weight)
            )
            result.setRGB(x, y, color.rgb)
        }
    }

    return result
}

fun mixColors(wColor: Int, iColor: Int, weight: Int): Int =
    (weight * wColor + (100 - weight) * iColor) / 100

fun saveImage(resultImage: BufferedImage, outputFilename: String) {
    try {
        val outputFile = File(outputFilename)
        ImageIO.write(resultImage, outputFile.extension, outputFile)
        println("The watermarked image $outputFilename has been created.")
    } catch (e: Exception) {
        println("Can't write output file!")
        exitProcess(-1)
    }
}