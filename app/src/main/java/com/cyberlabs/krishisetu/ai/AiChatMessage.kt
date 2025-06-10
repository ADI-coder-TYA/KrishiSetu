package com.cyberlabs.krishisetu.ai

data class AiChatMessage(
    val text: String,
    val sender: Sender,
    var isPlaceholder: Boolean = false // For "thinking..." indicator
) {
    enum class Sender {
        USER,
        AI
    }
}