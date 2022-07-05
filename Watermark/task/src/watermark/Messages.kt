package watermark

enum class Messages(
    val askInputName: String,
    val wrongImageColorComponentsNumber: String,
    val thisIsNot24Or32Bit: String
) {
    IMAGE(
        "Input the image filename:",
        "The number of image color components isn't 3.",
        "The image isn't 24 or 32-bit."
    ),
    WHATERMARK(
        "Input the watermark image filename:",
        "The number of watermark color components isn't 3.",
        "The watermark isn't 24 or 32-bit."
    )
}