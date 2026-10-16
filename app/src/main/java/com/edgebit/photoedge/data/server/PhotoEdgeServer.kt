package com.edgebit.photoedge.data.server

import android.content.Context
import com.edgebit.photoedge.data.model.FrameSettings
import com.edgebit.photoedge.data.repository.MediaRepository
import com.edgebit.photoedge.domain.util.MediaFileUtils
import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream

class PhotoEdgeServer(
    private val context: Context,
    private val mediaRepository: MediaRepository,
    private val getSettings: suspend () -> FrameSettings,
    private val onMediaUploaded: () -> Unit,
    port: Int = 8080
) : NanoHTTPD(port) {

    companion object {
        const val MAX_FILE_SIZE_BYTES = 512L * 1024 * 1024 // 512 MB
    }

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        val method = session.method

        return when {
            method == Method.GET && (uri == "/" || uri == "/index.html") -> {
                serveIndexHtml()
            }
            method == Method.GET && uri == "/api/status" -> {
                jsonResponse(Response.Status.OK, JSONObject().put("status", "ok").put("name", "PhotoEdge"))
            }
            method == Method.POST && uri == "/api/upload" -> {
                handleFileUpload(session)
            }
            else -> {
                newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "404 Not Found")
            }
        }
    }

    private fun serveIndexHtml(): Response {
        return try {
            val html = context.assets.open("web/index.html").bufferedReader().use { it.readText() }
            newFixedLengthResponse(Response.Status.OK, "text/html; charset=UTF-8", html)
        } catch (e: Exception) {
            newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error loading web page")
        }
    }

    private fun handleFileUpload(session: IHTTPSession): Response {
        return try {
            val files = HashMap<String, String>()
            session.parseBody(files)
            val params = session.parameters

            val currentSettings = kotlinx.coroutines.runBlocking { getSettings() }
            val enteredPin = params["pin"]?.firstOrNull() ?: ""

            if (enteredPin != currentSettings.serverPinCode) {
                return jsonResponse(
                    Response.Status.UNAUTHORIZED,
                    JSONObject().put("error", "Invalid PIN code")
                )
            }

            var savedAny = false
            for ((key, tempFilePath) in files) {
                if (key == "pin" || key == "postData") continue

                val tempFile = File(tempFilePath)
                if (!tempFile.exists() || tempFile.length() == 0L) continue

                if (tempFile.length() > MAX_FILE_SIZE_BYTES) {
                    return jsonResponse(
                        Response.Status.PAYLOAD_TOO_LARGE,
                        JSONObject().put("error", "File too large (max 512MB)")
                    )
                }

                val originalName = params[key]?.firstOrNull() ?: "upload_${System.currentTimeMillis()}.jpg"

                if (!MediaFileUtils.isSupportedMediaFile(originalName)) {
                    return jsonResponse(
                        Response.Status.UNSUPPORTED_MEDIA_TYPE,
                        JSONObject().put("error", "Unsupported file type")
                    )
                }

                val mimeType = MediaFileUtils.detectMimeType(originalName)

                kotlinx.coroutines.runBlocking {
                    FileInputStream(tempFile).use { input ->
                        mediaRepository.addMedia(originalName, input, mimeType)
                    }
                }
                savedAny = true
            }

            if (savedAny) {
                onMediaUploaded()
                jsonResponse(
                    Response.Status.OK,
                    JSONObject().put("success", true).put("message", "File uploaded successfully")
                )
            } else {
                jsonResponse(
                    Response.Status.BAD_REQUEST,
                    JSONObject().put("error", "No valid file provided")
                )
            }
        } catch (e: Exception) {
            jsonResponse(
                Response.Status.INTERNAL_ERROR,
                JSONObject().put("error", "Upload failed")
            )
        }
    }

    private fun jsonResponse(status: Response.Status, json: JSONObject): Response {
        return newFixedLengthResponse(status, "application/json", json.toString())
    }
}
