package com.streview.infrastructure.storages.images

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
import javax.imageio.ImageIO

class ImageStorageServiceImpl(private val config: ImageStorageConfig) : ImageStorageService {
    /**
     * 画像をリサイズし、WebP形式で指定された場所に保存する.
     *
     * @param image 入力画像のSource. この関数内で一度だけ消費される.
     * @param fileName 保存するファイル名.
     * @param imageType 画像の用途を示すタイプ. これにより保存先ディレクトリが決まる.
     * @return 保存に成功した場合、保存されたファイルのPathオブジェクト.
     * @throws IllegalStateException 設定が不適切な場合.
     * @throws Exception 画像処理やI/Oでエラーが発生した場合.
     */
    override suspend fun save(
        image: Source,
        fileName: String,
        imageType: ImageType
    ) {
        // I/O処理や画像処理はCPU負荷が高いため、IOディスパッチャで実行する
        withContext(Dispatchers.IO) {
            // 1. 設定から保存パスを構築する
            val baseDir = config.directories[imageType.value]
                ?: throw IllegalStateException("Directory for type '${imageType.value}' is not configured.")
            val outputDir = Path(baseDir)
            val outputFile = Path(outputDir, "$fileName.webp")

            // 2. 保存先ディレクトリが存在しない場合は作成する (早期失敗)
            // ファイル書き込みの前に実行することで、パーミッションエラーなどを先に検知できる
            SystemFileSystem.createDirectories(outputDir)

            // 3. 入力Sourceを読み込み、処理し、直接出力Sinkに書き込む
            try {
                // 出力先ファイルを書き込みモードで開く (Sink)
                SystemFileSystem.sink(outputFile).buffered().use { fileSink ->
                    // 入力Sourceを読み込みモードで開く
                    image.buffered().asInputStream().use { inputStream ->

                        // デコード: InputStreamからBufferedImageへ
                        val originalImage: BufferedImage = ImageIO.read(inputStream)
                            ?: throw Exception("Unsupported image format or corrupted data.")

                        // 加工: imgscalrでリサイズ
                        val resizedImage = Scalr.resize(
                            originalImage,
                            Scalr.Method.QUALITY,
                            400, // targetWidth
                            Scalr.OP_ANTIALIAS
                        )
                        originalImage.flush() // 元画像のリソースを解放

                        // エンコード: リサイズ後の画像をWebP形式で直接ファイルSinkに書き込む
                        // SinkをOutputStreamとして扱うことで、中間ByteArrayを不要にする
                        val writeSuccess = ImageIO.write(resizedImage, "webp", fileSink.asOutputStream())
                        if (!writeSuccess) {
                            throw Exception("Failed to write image in WebP format.")
                        }

                        resizedImage.flush() // リサイズ後画像のリソースを解放
                    }
                }
            } catch (e: Exception) {
                // エラーが発生した場合は、中途半端に作られたファイルを削除するクリーンアップ処理を入れるとより堅牢になる
                // Files.deleteIfExists(outputFile.toNioPath())
                throw e // エラーを再スローして呼び出し元に伝える
            }
        }
    }
}
