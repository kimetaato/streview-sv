package com.streview.infrastructure.storages.images

import com.luciad.imageio.webp.CompressionType
import com.luciad.imageio.webp.WebPWriteParam
import com.streview.application.services.ImageStorageConfig
import com.streview.application.services.ImageStorageService
import com.streview.application.services.ImageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Source
import kotlinx.io.asInputStream
import kotlinx.io.asOutputStream
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.imgscalr.Scalr
import java.awt.image.BufferedImage
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter

class ImageStorageServiceImpl(private val config: ImageStorageConfig) : ImageStorageService {

    companion object {
        private const val MAX_IMAGE_WIDTH = 1080
        private const val WEBP_FORMAT = "webp"
        private const val WEBP_EXTENSION = ".webp"
    }

    override suspend fun save(
        image: Source,
        fileName: String,
        imageType: ImageType,
    ) {
        withContext(Dispatchers.IO) {
            val outputPath = prepareOutputPath(fileName, imageType)
            val writer = createWebPWriter()

            try {
                processAndSaveImage(image, outputPath, writer)
            } finally {
                writer.dispose()
            }
        }
    }

    /**
     * 出力パスを準備し、必要に応じてディレクトリを作成する
     */
    private fun prepareOutputPath(fileName: String, imageType: ImageType): Path {
        val baseDir = config.directories[imageType.value]
            ?: throw IllegalStateException("Directory for type '${imageType.value}' is not configured.")

        val outputDir = Path(baseDir)
        val outputPath = Path(outputDir, "$fileName$WEBP_EXTENSION")

        SystemFileSystem.createDirectories(outputDir)
        return outputPath
    }

    /**
     * WebP用のImageWriterを作成する
     */
    private fun createWebPWriter(): ImageWriter {
        val writers = ImageIO.getImageWritersByFormatName(WEBP_FORMAT)
        when {
            !writers.hasNext() -> {
                throw IllegalStateException("WebP ImageWriter not found. Please ensure the WebP Image I/O plugin is on the classpath.")
            }
            else -> return writers.next()
        }
    }

    /**
     * 画像を処理して保存する
     */
    private fun processAndSaveImage(image: Source, outputPath: Path, writer: ImageWriter) {
        SystemFileSystem.sink(outputPath).buffered().use { fileSink ->
            image.buffered().asInputStream().use { inputStream ->
                val resizedImage = loadAndResizeImage(inputStream)
                saveImageAsWebP(resizedImage, fileSink, writer)
                resizedImage.flush()
            }
        }
    }

    /**
     * 画像を読み込み、リサイズする
     */
    private fun loadAndResizeImage(inputStream: java.io.InputStream): BufferedImage {
        val originalImage = ImageIO.read(inputStream)
            ?: throw IllegalArgumentException("Unsupported image format or corrupted data.")

        return try {
            if (originalImage.width <= MAX_IMAGE_WIDTH) {
                originalImage
            } else {
                val resizedImage = Scalr.resize(
                    originalImage,
                    Scalr.Method.ULTRA_QUALITY,
                    MAX_IMAGE_WIDTH,
                    Scalr.OP_ANTIALIAS
                )
                originalImage.flush()
                resizedImage
            }
        } catch (e: Exception) {
            originalImage.flush()
            throw e
        }
    }

    /**
     * 画像をWebP形式で保存する
     */
    private fun saveImageAsWebP(
        image: BufferedImage,
        fileSink: kotlinx.io.Sink,
        writer: ImageWriter
    ) {
        ImageIO.createImageOutputStream(fileSink.asOutputStream()).use { imageOutputStream ->
            writer.output = imageOutputStream

            val params = configureWebPParams(writer)
            writer.write(null, IIOImage(image, null, null), params)
        }
    }

    /**
     * WebP書き込みパラメータを設定する
     */
    private fun configureWebPParams(writer: ImageWriter): ImageWriteParam {
        val params = writer.defaultWriteParam
        if (params is WebPWriteParam) {
            params.compressionMode = ImageWriteParam.MODE_EXPLICIT
            params.compressionType = CompressionType.Lossless
        }
        return params
    }
}