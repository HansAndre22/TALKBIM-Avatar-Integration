package com.example.myapplication.detection

object SignToAnimationMapper {
    
    fun mapSignToAnimation(signLabel: String): AnimationConfig {
        return when (signLabel.lowercase()) {
            "hello" -> AnimationConfig("hello", mapOf("smile" to 0.8f))
            "thank_you" -> AnimationConfig("thank_you", mapOf("smile" to 0.6f))
            "goodbye" -> AnimationConfig("goodbye", mapOf("wave" to 0.9f))
            "yes" -> AnimationConfig("yes", mapOf("nod" to 0.9f))
            "no" -> AnimationConfig("no", mapOf("shake" to 0.9f))
            "please" -> AnimationConfig("please", mapOf("hands_together" to 0.7f))
            "sorry" -> AnimationConfig("sorry", mapOf("sad" to 0.6f))
            else -> AnimationConfig("default", emptyMap())
        }
    }

    fun getAllSignMappings(): Map<String, AnimationConfig> {
        return mapOf(
            "hello" to AnimationConfig("hello", mapOf("smile" to 0.8f)),
            "thank_you" to AnimationConfig("thank_you", mapOf("smile" to 0.6f)),
            "goodbye" to AnimationConfig("goodbye", mapOf("wave" to 0.9f)),
            "yes" to AnimationConfig("yes", mapOf("nod" to 0.9f)),
            "no" to AnimationConfig("no", mapOf("shake" to 0.9f)),
            "please" to AnimationConfig("please", mapOf("hands_together" to 0.7f)),
            "sorry" to AnimationConfig("sorry", mapOf("sad" to 0.6f)),
            "beautiful" to AnimationConfig("beautiful", mapOf("smile" to 0.7f)),
            "congratulations" to AnimationConfig("congratulations", mapOf("clap" to 0.8f))
        )
    }
}

data class AnimationConfig(
    val name: String,
    val blendshapes: Map<String, Float>
)
