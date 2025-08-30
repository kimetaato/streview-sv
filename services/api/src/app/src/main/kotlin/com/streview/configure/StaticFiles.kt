package com.streview.configure

import com.streview.application.services.ImageStorageConfig
import com.streview.application.services.ImageType
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Application.configureStaticFiles() {
    val config by inject<ImageStorageConfig>()

    routing {
        ImageType.entries.forEach { imageType ->
            staticFiles(
                "/static/images/${imageType.value}",
                File("${config.baseDirectory}/${imageType.value}")
            )
        }
    }
}
