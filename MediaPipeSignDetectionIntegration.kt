package com.example.myapplication.detection

import android.util.Log
import com.example.myapplication.ui.avatar.AvatarRenderFragment

class MediaPipeSignDetectionIntegration(
    private val avatarFragment: AvatarRenderFragment
) {
    companion object {
        private const val TAG = "MediaPipeIntegration"
    }

    fun onHandLandmarksDetected(
        leftHandLandmarks: List<FloatArray>?,
        rightHandLandmarks: List<FloatArray>?
    ) {
        avatarFragment.updateAvatarHandsFromLandmarks(
            leftHandLandmarks,
            rightHandLandmarks
        )
    }

    fun onSignRecognized(
        signLabel: String,
        confidence: Float,
        leftHandLandmarks: List<FloatArray>?,
        rightHandLandmarks: List<FloatArray>?
    ) {
        Log.d(TAG, "Sign recognized: $signLabel (confidence: $confidence)")
        
        if (confidence > 0.7f) {
            avatarFragment.animateSignRecognition(signLabel)
            avatarFragment.updateAvatarHandsFromLandmarks(
                leftHandLandmarks,
                rightHandLandmarks
            )
        }
    }

    fun getAnimationTriggerForSign(signLabel: String): String {
        return when (signLabel.lowercase()) {
            "hello" -> "SIGN_HELLO"
            "thank_you", "thanks" -> "SIGN_THANK_YOU"
            "goodbye", "bye" -> "SIGN_GOODBYE"
            "yes" -> "SIGN_YES"
            "no" -> "SIGN_NO"
            "please" -> "SIGN_PLEASE"
            "sorry" -> "SIGN_SORRY"
            else -> "SIGN_DEFAULT"
        }
    }
}
