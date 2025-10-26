package com.example.foodsure.data

import android.content.Context
import android.util.Log
import com.example.foodsure.data.ModelRepository.RespondStatus
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.prepareGet
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.copyAndClose
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.flow.Flow
import kotlinx.io.buffered
import kotlinx.serialization.Serializable
import java.io.File

class RoomRepository(
    private val database: AppDatabase,
    context: Context
) : ModelRepository {
    private val TAG: String = "RoomRepository"
    private val modelDao = database.foodModelDao()

    /** FoodItem operations */
    override suspend fun insertItem(item: FoodItem, tags: List<String>) {
        modelDao.insertFoodItemWithTags(item, tags)
    }

    override suspend fun updateItem(item: FoodItem, tags: List<String>) {
        modelDao.updateFoodItemWithTags(item, tags)
    }

    override suspend fun getItem(id: Long): FoodItemWithTags? {
        return modelDao.getFoodItemById(id)
    }

    override suspend fun deleteItem(foodItem: FoodItem) {
        modelDao.deleteFoodItem(foodItem)
    }

    override suspend fun deleteItem(foodItemId: Long) {
        modelDao.deleteFoodItemWithTags(foodItemId)
    }

    override fun searchItem(query: String): Flow<List<FoodItemWithTags>> {
        return modelDao.searchFoodItemByAllStringData(query)
    }

    /** FoodTags operations */
    override suspend fun insertTag(tag: FoodTag) {
        modelDao.insertFoodTag(tag)
    }

    override suspend fun updateTag(tag: FoodTag) {
        modelDao.updateFoodTag(tag)
    }

    override fun getAllTagsName(): Flow<List<String>> {
        return modelDao.getAllFoodTagName()
    }

    override suspend fun deleteTag(tag: FoodTag) {
        modelDao.deleteFoodTag(tag)
    }

    override suspend fun getTagByName(name: String): FoodTag? {
        return modelDao.getFoodTagByName(name)
    }

    override fun searchTag(query: String): Flow<List<FoodTag>> {
        return modelDao.searchFoodTag(query)
    }

    /**
     * RoomBackup operations
     */

    private val endpoint = "http://192.168.0.240:8000/"
    val backupFile = File("${context.filesDir}/databasebackup/FoodModelDB.sqlite3")

    @Serializable
    data class RespondBody(
        val id: Int,
        val original_filename: String,
        val size_bytes: Long,
        val upload_time: String,
        val description: String,
        val storage_path: String
    )

    private fun getHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json() // Example: Register JSON content transformation
                // Add more transformations as needed for other content types
            }
        }
    }

    override suspend fun uploadBackup(backup: RoomBackup): RespondStatus {
        var result = RespondStatus.FAIL_ON_BACKUP
        backup.database(database)
            .enableLogDebug(true)
            .backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_FILE)
            .backupLocationCustomFile(backupFile)
            .apply {
                onCompleteListener { success, message, exitCode ->
                    Log.d(TAG, "success: $success, message: $message, exitCode: $exitCode")
                    if (success) {
                        result = RespondStatus.SUCCESS
                    }
                }
            }
            .backup()
        if (result == RespondStatus.FAIL_ON_BACKUP) return result

        val httpClient by lazy { getHttpClient() }
        val respond = httpClient.post(endpoint) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("description", "FoodModelDB")
                        append(
                            "file",
                            InputProvider { backupFile.inputStream().asInput().buffered() },
                            Headers.build {
                                append(HttpHeaders.ContentType, "application/octet-stream")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"FoodModelDB.sqlite3\""
                                )
                            }
                        )
                    }
                )
            )
            url {
                appendPathSegments("/backups/")
            }
            onUpload { bytesSentTotal, contentLength ->
                println("Sent $bytesSentTotal bytes from $contentLength")
            }
        }
        val body: String = respond.body()
        Log.i(TAG, "uploadBackup: $respond, body:$body")
        if (respond.status == io.ktor.http.HttpStatusCode.Created) {
            if (backupFile.delete()) return RespondStatus.SUCCESS
            else return RespondStatus.FAIL_ON_DELETE
        } else {
            return RespondStatus.FAIL_ON_UPLOAD
        }
    }

    override suspend fun listBackup() {
        TODO("Not yet implemented")
    }

    override suspend fun downloadBackup(backup: RoomBackup): RespondStatus {
        val httpClient by lazy { getHttpClient() }
        var respond = httpClient.get(endpoint) {
            url {
                appendPathSegments("backups/latest")
            }
            onUpload { bytesSentTotal, contentLength ->
                println("Sent $bytesSentTotal bytes from $contentLength")
            }
        }
        val respondBody: RespondBody = respond.body()

        httpClient.prepareGet("${endpoint}backups/${respondBody.id}/download")
            .execute { httpResponse ->
                val channel: ByteReadChannel = httpResponse.body()
                channel.copyAndClose(backupFile.writeChannel())
                Log.i(TAG, "A file saved to ${backupFile.path}")
                respond = httpResponse
            }

        var result = RespondStatus.FAIL_ON_RESTORE
        if (respond.status != io.ktor.http.HttpStatusCode.OK) {
            Log.i(TAG, "download fail: ${respond.status}")
            return RespondStatus.FAIL_ON_DOWNLOAD
        } else {
            backup.database(database)
                .enableLogDebug(true)
                .backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_FILE)
                .backupLocationCustomFile(backupFile)
                .apply {
                    onCompleteListener { success, message, exitCode ->
                        Log.d(TAG, "success: $success, message: $message, exitCode: $exitCode")
                        if (success) {
                            result = RespondStatus.SUCCESS
                        }
                    }
                }
                .restore()
        }
        return result
    }

    override suspend fun deleteBackup(): RespondStatus {
        TODO("Not yet implemented")
    }
}