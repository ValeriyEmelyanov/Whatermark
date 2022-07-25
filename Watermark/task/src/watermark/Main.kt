package watermark

import java.awt.Color
import java.awt.Transparency
import java.awt.image.BufferedImage
import kotlin.system.exitProcess

fun main() {
    val image = ImageReader.readFromFile(Messages.IMAGE)
    val watermark = ImageReader.readFromFile(Messages.WATERMARK)
    checkImageSizes(image, watermark)

    val useAlpha = checkUsingAlpha(watermark)
    val transColor = getTransColor(watermark)
    val watermarkTranspPercentage = getWatermarkTranspPercentage()

    val positionMethod = getPositionMethod()
    val position = getPosition(positionMethod, image, watermark)

    val outputFilename = getOutputFilename()

    val resultImage = ImageBlender.Builder()
        .image(image)
        .watermark(watermark)
        .weight(watermarkTranspPercentage)
        .useAlpha(useAlpha)
        .transColor(transColor)
        .positionMethod(positionMethod)
        .position(position)
        .build()
        .blendImages()

    ImageSaver.save(resultImage, outputFilename)
}

fun checkImageSizes(image: BufferedImage, watermark: BufferedImage) {
    if (watermark.width > image.width || watermark.height > image.height) {
        println("The watermark's dimensions are larger.")
        exitProcess(-1)
    }
}

fun getPositionMethod(): PositionMethod {
    println("Choose the position method (single, grid):")
    val answer = readln()
    try {
        return PositionMethod.valueOf(answer.uppercase())
    } catch (e: Exception) {
        println("The position method input is invalid.")
        exitProcess(-1)
    }
}

fun getPosition(positionMethod: PositionMethod, image: BufferedImage, watermark: BufferedImage): Pair<Int, Int> {
    if (positionMethod == PositionMethod.GRID) return Pair(0, 0)

    val diffX = image.width - watermark.width
    val diffY = image.height - watermark.height
    println("Input the watermark position ([x 0-$diffX] [y 0-$diffY]):")
    try {
        val (x, y) = readln().split(' ').map { n -> n.toInt() }

        if (x < 0 || x > diffX || y < 0 || y > diffY) {
            println("The position input is out of range.")
            exitProcess(-1)
        }
        return Pair(x, y)
    } catch (e: Exception) {
        println("The position input is invalid.")
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
