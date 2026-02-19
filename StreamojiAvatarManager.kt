package com.example.myapplication.avatar

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.api.StreamojiService
import kotlinx.coroutines.*
import java.io.File

class StreamojiAvatarManager(
    private val context: Context,
    private val service: StreamojiService
) {
    companion object {
        private const val TAG = "StreamojiAvatarManager"
        private const val AVATARS_CACHE_DIR = "streamoji_avatars"
    }

    private val _avatarStateLD = MutableLiveData<AvatarState>()
    val avatarState: LiveData<AvatarState> = _avatarStateLD

    private val _loadingLD = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loadingLD

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    suspend fun initializeAvatar(config: AvatarConfig): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            _loadingLD.postValue(true)
            Log.d(TAG, "Initializing avatar: ${config.name}")

            val cachedPath = getCachedAvatarPath(config.id)
            if (cachedPath.exists()) {
                Log.d(TAG, "Avatar found in cache")
                _loadingLD.postValue(false)
                _avatarStateLD.postValue(AvatarState.Success(cachedPath.absolutePath))
                return@withContext Result.success(cachedPath.absolutePath)
            }

            val response = service.createAvatar(config)
            if (response.isSuccessful) {
                val glbUrl = response.body()?.modelUrl
                    ?: return@withContext Result.failure(Exception("No model URL"))
                
                val localPath = downloadAndCacheAvatar(glbUrl, config.id)
                _avatarStateLD.postValue(AvatarState.Success(localPath))
                _loadingLD.postValue(false)
                Result.success(localPath)
            } else {
                val error = Exception("API Error: ${response.code()}")
                _avatarStateLD.postValue(AvatarState.Error(error))
                _loadingLD.postValue(false)
                Result.failure(error)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error", e)
            _avatarStateLD.postValue(AvatarState.Error(e))
            _loadingLD.postValue(false)
            Result.failure(e)
        }
    }

    private suspend fun downloadAndCacheAvatar(
        glbUrl: String,
        avatarId: String
    ): String = withContext(Dispatchers.IO) {
        val cacheDir = File(context.cacheDir, AVATARS_CACHE_DIR)
        if (!cacheDir.exists()) cacheDir.mkdirs()

        val cachedFile = File(cacheDir, "$avatarId.glb")
        val response = service.downloadFile(glbUrl)
        
        if (response.isSuccessful) {
            response.body()?.byteStream()?.use { input ->
                cachedFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } else {
            throw Exception("Download failed: ${response.code()}")
        }
        return@withContext cachedFile.absolutePath
    }

    private fun getCachedAvatarPath(avatarId: String): File {
        return File(context.cacheDir, "$AVATARS_CACHE_DIR/$avatarId.glb")
    }

    suspend fun applySignExpression(signLabel: String, intensity: Float = 1.0f): Result<Unit> {
        return try {
            val expressionMap = mapSignToExpression(signLabel)
            _avatarStateLD.postValue(AvatarState.ExpressionApplied(signLabel, expressionMap, intensity))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapSignToExpression(signLabel: String): Map<String, Float> {
        return when (signLabel.lowercase()) {
            "hello" -> mapOf("mouth_smile" to 0.8f, "left_hand_open" to 1.0f)
            "thank_you" -> mapOf("mouth_smile" to 0.6f, "left_hand_wave" to 0.8f)
            "goodbye" -> mapOf("mouth_smile" to 0.5f, "left_hand_wave" to 0.9f)
            "yes" -> mapOf("head_nod" to 0.9f, "mouth_smile" to 0.5f)
            "no" -> mapOf("head_shake" to 0.9f, "mouth_neutral" to 1.0f)
            else -> mapOf()
        }
    }

    fun cleanup() {
        coroutineScope.cancel()
        Log.d(TAG, "Cleaned up")
    }

    fun clearCache() {
        val cacheDir = File(context.cacheDir, AVATARS_CACHE_DIR)
        if (cacheDir.exists()) cacheDir.deleteRecursively()
    }
}

data class AvatarConfig(
    val id: String,
    val name: String,
    val gender: String = "neutral",
    val skinTone: String = "light",
    val outfitColor: String = "#0066CC"
)

sealed class AvatarState {
    object Loading : AvatarState()
    data class Success(val modelPath: String) : AvatarState()
    data class Error(val exception: Exception) : AvatarState()
    data class ExpressionApplied(
        val signLabel: String,
        val blendshapes: Map<String, Float>,
        val intensity: Float
    ) : AvatarState()
}
