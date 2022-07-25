package watermark

import java.awt.Color
import java.awt.image.BufferedImage

class ImageBlender(
    val image: BufferedImage,
    val watermark: BufferedImage,
    val weight: Int,
    val useAlpha: Boolean,
    val transColor: Pair<Boolean, Color>,
    val positionMethod: PositionMethod,
    val position: Pair<Int, Int>
) {

    fun blendImages(): BufferedImage {
        val result = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

        when (positionMethod) {
            PositionMethod.SINGLE -> blendImagesBySingle(result, position.first, position.second)
            PositionMethod.GRID -> blendImagesByGrid(result)
        }

        return result
    }

    private fun blendImagesBySingle(result: BufferedImage, posX: Int, posY: Int) {

        val limitX = posX + watermark.width
        val limitY = posY + watermark.height

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                if (x >= posX && x < limitX && y >= posY && y < limitY) continue
                setColor(result, x, y)
            }
        }

        blendImagesInPosition(result, posX, posY)

    }

    private fun setColor(result: BufferedImage, x: Int, y: Int) {
        val i = Color(image.getRGB(x, y))
        result.setRGB(x, y, i.rgb)
    }

    private fun blendImagesInPosition(result: BufferedImage, posX: Int, posY: Int): BufferedImage {

        for (x in 0 until watermark.width) {
            val imgX = posX + x;
            if (imgX >= image.width) break

            for (y in 0 until watermark.height) {
                val imgY = posY + y
                if (imgY >= image.height) break

                val i = Color(image.getRGB(imgX, imgY))
                val w = if (useAlpha) Color(watermark.getRGB(x, y), true) else Color(watermark.getRGB(x, y))

                val color = if (useAlpha && w.alpha == 0 ||
                    transColor.first && transColor.second == w
                ) i else Color(
                    mixColors(w.red, i.red, weight),
                    mixColors(w.green, i.green, weight),
                    mixColors(w.blue, i.blue, weight)
                )
                result.setRGB(imgX, imgY, color.rgb)
            }
        }

        return result
    }

    fun mixColors(wColor: Int, iColor: Int, weight: Int): Int =
        (weight * wColor + (100 - weight) * iColor) / 100

    private fun blendImagesByGrid(result: BufferedImage) {

        for (x in 0 until image.width step watermark.width) {
            for (y in 0 until image.height step watermark.height) {
                blendImagesInPosition(result, x, y)
            }
        }
    }

    class Builder {
        var image: BufferedImage? = null
        var watermark: BufferedImage? = null
        var weight: Int = 0
        var useAlpha: Boolean = true
        var transColor: Pair<Boolean, Color> = Pair(false, Color.WHITE)
        var positionMethod: PositionMethod = PositionMethod.SINGLE
        var position: Pair<Int, Int> = Pair(0, 0)

        fun image(image: BufferedImage) = apply { this.image = image }
        fun watermark(watermark: BufferedImage) = apply { this.watermark = watermark }
        fun weight(weight: Int) = apply { this.weight = weight }
        fun useAlpha(useAlpha: Boolean) = apply { this.useAlpha = useAlpha }
        fun transColor(transColor: Pair<Boolean, Color>) = apply { this.transColor = transColor }
        fun positionMethod(positionMethod: PositionMethod) = apply { this.positionMethod = positionMethod }
        fun position(position: Pair<Int, Int>) = apply { this.position = position }

        fun build() = ImageBlender(
            image!!,
            watermark!!,
            weight,
            useAlpha,
            transColor,
            positionMethod,
            position
        )
    }
}