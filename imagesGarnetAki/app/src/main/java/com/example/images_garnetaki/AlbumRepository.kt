package com.example.images_garnetaki

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.StrictMode
import android.provider.MediaStore
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.dto.common.id.UserId
import com.vk.sdk.api.photos.PhotosService
import com.vk.sdk.api.photos.dto.PhotosGetAlbumsResponseDto
import com.vk.sdk.api.photos.dto.PhotosPhotoDto
import com.vk.sdk.api.photos.dto.PhotosPhotoUploadDto
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume

class AlbumRepository {

    suspend fun getAlbums(userId: Long): MutableList<Album> {
        return suspendCancellableCoroutine { continuation ->
            VK.execute(PhotosService().photosGetAlbums(ownerId = UserId(userId), needSystem = true, needCovers = true),
            object: VKApiCallback<PhotosGetAlbumsResponseDto> {
                override fun success(result: PhotosGetAlbumsResponseDto) {
                    val albums: MutableList<Album> = emptyList<Album>().toMutableList()
                    for (i in result.items){
                        if (!i.thumbSrc.isNullOrEmpty())
                            albums.add(Album(i.title, i.size, i.id, i.thumbSrc!!))
                    }
                    continuation.resume(albums)
                }
                override fun fail(error: Exception) {
                    continuation.resume(emptyList<Album>().toMutableList())
                }
            })
        }
    }

    suspend fun uploadPhotos(
        uris: List<@JvmSuppressWildcards Uri>,
        albumId: Int,
        contentResolver: ContentResolver
    ): String {
        return suspendCancellableCoroutine { continuation ->
        VK.execute(PhotosService().photosGetUploadServer(albumId = albumId),
            object: VKApiCallback<PhotosPhotoUploadDto> {
                override fun fail(error: Exception) {
                    continuation.resume(error.message.toString())
                }

                override fun success(result: PhotosPhotoUploadDto) {
                    val client = OkHttpClient()

                    var requestBodyUnBuild = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)

                    var t = 1
                    uris.forEach { i ->
                        val filePath: String
                        if ("content" == i.scheme) {
                            val cursor: Cursor? = contentResolver.query(
                                i,
                                arrayOf<String>(MediaStore.Images.ImageColumns.DATA),
                                null,
                                null,
                                null
                            )
                            cursor?.moveToFirst()
                            filePath = cursor!!.getString(0)
                            cursor.close()
                        } else {
                            filePath = i.path!!
                        }
                        println(filePath)
                        requestBodyUnBuild = requestBodyUnBuild.addFormDataPart("file".plus(t.toString()),
                            filePath,
                            File(filePath).asRequestBody())
                        t++
                    }

                    val requestBody = requestBodyUnBuild.build()

                    val request = Request.Builder()
                        .url(result.uploadUrl)
                        .post(requestBody)
                        .build()

                    val policy = StrictMode.ThreadPolicy.Builder()
                        .permitAll().build()
                    StrictMode.setThreadPolicy(policy)

                    val responseData: String = client.newCall(request).execute()
                        .use { response ->
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            response.body!!.string()
                        }

                    println(responseData)
                    val jsonObject = JSONObject(responseData)

                    VK.execute(PhotosService().photosSave(albumId = jsonObject.getString("aid").toInt(),
                        server = jsonObject.getString("server").toInt(),
                        photosList = jsonObject.getString("photos_list"),
                        hash = jsonObject.getString("hash")),
                        object: VKApiCallback<List<PhotosPhotoDto>> {
                            override fun fail(error: Exception) {
                                continuation.resume(error.message.toString())
                            }

                            override fun success(result: List<PhotosPhotoDto>) {
                                continuation.resume("success")
                            }
                        })
                }
            })
            }
    }
}