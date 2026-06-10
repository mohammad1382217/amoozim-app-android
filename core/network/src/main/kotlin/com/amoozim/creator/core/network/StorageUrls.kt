package com.amoozim.creator.core.network

import java.net.URLEncoder

/**
 * Builds media URLs on the storage-preview host, mirroring `resolveStorageMediaRef`
 * on the web. A `media_token` is exchanged for an authenticated file URL; an already
 * absolute URL is returned unchanged. Image/file requests still require the bearer
 * token (sent by the shared authed OkHttp client / Coil loader).
 */
object StorageUrls {
    private val previewBase: String = BuildConfig.STORAGE_PREVIEW_BASE.trimEnd('/')

    fun mediaFile(token: String): String =
        if (token.startsWith("http", ignoreCase = true)) {
            token
        } else {
            "$previewBase/api/media/file?token=${URLEncoder.encode(token, "UTF-8")}"
        }

    /** Joins a relative path (e.g. an HLS playlist path) onto the preview host. */
    fun fromPreview(relativePath: String): String =
        if (relativePath.startsWith("http", ignoreCase = true)) relativePath
        else previewBase + if (relativePath.startsWith("/")) relativePath else "/$relativePath"
}
