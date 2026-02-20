package com.example.myapplication.utils

object LandmarkNormalizer {
    
    fun normalizeHandLandmarks(landmarks: List<FloatArray>): List<FloatArray> {
        if (landmarks.isEmpty()) return emptyList()
        
        val normalized = mutableListOf<FloatArray>()
        
        // Get wrist (reference point)
        val wrist = landmarks[0]
        
        for (landmark in landmarks) {
            val normalized_x = (landmark[0] - wrist[0])
            val normalized_y = (landmark[1] - wrist[1])
            val normalized_z = (landmark.getOrNull(2) ?: 0f) - wrist[2]
            
            normalized.add(floatArrayOf(normalized_x, normalized_y, normalized_z))
        }
        
        return normalized
    }

    fun smoothLandmarks(
        currentLandmarks: List<FloatArray>,
        previousLandmarks: List<FloatArray>?,
        alpha: Float = 0.7f
    ): List<FloatArray> {
        if (previousLandmarks == null) return currentLandmarks
        
        return currentLandmarks.mapIndexed { index, current ->
            val previous = previousLandmarks.getOrNull(index) ?: current
            floatArrayOf(
                current[0] * alpha + previous[0] * (1 - alpha),
                current[1] * alpha + previous[1] * (1 - alpha),
                current.getOrNull(2)?.let { 
                    it * alpha + (previous.getOrNull(2) ?: 0f) * (1 - alpha) 
                } ?: current.getOrNull(2) ?: 0f
            )
        }
    }

    fun validateLandmarks(landmarks: List<FloatArray>): Boolean {
        if (landmarks.size != 21) return false
        
        for (landmark in landmarks) {
            if (landmark.size < 2) return false
            if (landmark[0] < 0 || landmark[0] > 1) return false
            if (landmark[1] < 0 || landmark[1] > 1) return false
        }
        
        return true
    }
}
