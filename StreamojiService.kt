package com.example.myapplication.api

import com.example.myapplication.avatar.AvatarConfig
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface StreamojiService {
    companion object {
        const val BASE_URL = "https://api.avatars.streamoji.com/"
    }

    @POST("api/v1/avatars/create")
    @Headers("Content-Type: application/json")
    suspend fun createAvatar(@Body config: AvatarConfig): Response<AvatarResponse>

    @GET
    suspend fun downloadFile(@Url url: String): Response<ResponseBody>

    @PUT("api/v1/avatars/{avatarId}")
    suspend fun updateAvatar(
        @Path("avatarId") avatarId: String,
        @Body updates: AvatarUpdateRequest
    ): Response<AvatarResponse>

    @GET("api/v1/avatars/{avatarId}")
    suspend fun getAvatarDetails(
        @Path("avatarId") avatarId: String
    ): Response<AvatarResponse>
}

data class AvatarResponse(
    val id: String,
    val name: String,
    val modelUrl: String,
    val thumbnailUrl: String? = null,
    val createdAt: String,
    val status: String
)

data class AvatarUpdateRequest(
    val blendshapes: Map<String, Float>? = null,
    val pose: PoseUpdate? = null
)

data class PoseUpdate(
    val leftHandRotation: FloatArray? = null,
    val rightHandRotation: FloatArray? = null,
    val bodyRotation: FloatArray? = null
)
