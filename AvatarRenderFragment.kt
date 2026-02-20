package com.example.myapplication.ui.avatar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.avatar.AvatarConfig
import com.example.myapplication.avatar.AvatarState
import com.example.myapplication.avatar.StreamojiAvatarManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class AvatarRenderFragment : Fragment() {
    companion object {
        private const val TAG = "AvatarRenderFragment"
    }

    @Inject
    lateinit var avatarManager: StreamojiAvatarManager

    private lateinit var progressBar: ProgressBar
    private var avatarDisplayView: AvatarGLRenderer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_avatar_render, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.avatarProgressBar)
        avatarDisplayView = view.findViewById(R.id.avatarGLRenderer)

        avatarManager.avatarState.observe(viewLifecycleOwner) { state ->
            handleAvatarStateChange(state)
        }

        avatarManager.loading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        initializeAvatar()
    }

    private fun initializeAvatar() {
        val avatarConfig = AvatarConfig(
            id = "bim_avatar_1",
            name = "BIM Avatar",
            gender = "neutral",
            skinTone = "medium",
            outfitColor = "#00AA44"
        )

        viewLifecycleOwner.lifecycleScope.launch {
            avatarManager.initializeAvatar(avatarConfig)
        }
    }

    private fun handleAvatarStateChange(state: AvatarState) {
        when (state) {
            is AvatarState.Loading -> {
                Log.d(TAG, "Loading avatar...")
                progressBar.visibility = View.VISIBLE
            }
            is AvatarState.Success -> {
                Log.d(TAG, "Avatar loaded: ${state.modelPath}")
                progressBar.visibility = View.GONE
                loadAvatarModelIntoView(state.modelPath)
            }
            is AvatarState.Error -> {
                Log.e(TAG, "Avatar error", state.exception)
                progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Failed to load avatar",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is AvatarState.ExpressionApplied -> {
                Log.d(TAG, "Expression: ${state.signLabel}")
                applyExpressionToAvatar(state.blendshapes, state.intensity)
            }
        }
    }

    private fun loadAvatarModelIntoView(modelPath: String) {
        avatarDisplayView?.loadModel(modelPath)
    }

    private fun applyExpressionToAvatar(
        blendshapes: Map<String, Float>,
        intensity: Float
    ) {
        avatarDisplayView?.setBlendshapes(blendshapes, intensity)
    }

    fun animateSignRecognition(signLabel: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            avatarManager.applySignExpression(signLabel, intensity = 1.0f)
        }
    }

    fun updateAvatarHandsFromLandmarks(
        leftHandLandmarks: List<FloatArray>?,
        rightHandLandmarks: List<FloatArray>?
    ) {
        if (leftHandLandmarks != null) {
            avatarDisplayView?.updateHandSkeleton("left", leftHandLandmarks)
        }
        if (rightHandLandmarks != null) {
            avatarDisplayView?.updateHandSkeleton("right", rightHandLandmarks)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        avatarManager.cleanup()
    }
}
