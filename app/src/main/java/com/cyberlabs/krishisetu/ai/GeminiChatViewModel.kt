package com.cyberlabs.krishisetu.ai

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberlabs.krishisetu.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.ai.client.generativeai.Chat

@HiltViewModel
class GeminiChatViewModel @Inject constructor() : ViewModel() {

    private val _messages = mutableStateListOf<AiChatMessage>()
    val messages: List<AiChatMessage> = _messages

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash", // You can specify 'gemini-pro-vision' for multimodal
        apiKey = BuildConfig.GEMINI_API_KEY // Access the API key from BuildConfig
    )

    private lateinit var chat: Chat

    init {
        // Initialize the chat session here
        startNewChat()

        // Initial AI greeting
        _messages.add(
            AiChatMessage(
                "Hello! I'm your KrishiSetu AI Assistant. How can I help you today?",
                AiChatMessage.Sender.AI
            )
        )
    }

    //Function to start a new chat session (useful if you add a "New Chat" button later)
    fun startNewChat() {
        chat = generativeModel.startChat(history = emptyList()) // Start with an empty history
        // Optionally clear existing messages if this is a "new chat" action
        // messages.clear() // Uncomment if you want to clear UI when starting new chat
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        _messages.add(AiChatMessage(userMessage, AiChatMessage.Sender.USER))
        // Scroll to the end (handled in UI via LazyColumn's state)

        // Add a "thinking..." placeholder
        _messages.add(AiChatMessage("...", AiChatMessage.Sender.AI, isPlaceholder = true))

        viewModelScope.launch {
            try {
                // Simulate typing delay for placeholder to appear
                delay(500)

                // Make the direct call to Gemini using the SDK
                val response = chat.sendMessage(userMessage)

                // Get the text from the response
                val aiResponse = response.text ?: "Sorry, I couldn't get a clear response."

                // Replace the placeholder with the actual response
                val lastMessageIndex = messages.indexOfLast { it.isPlaceholder }
                if (lastMessageIndex != -1) {
                    _messages[lastMessageIndex] = AiChatMessage(aiResponse, AiChatMessage.Sender.AI)
                } else {
                    // Fallback if placeholder somehow vanished
                    _messages.add(AiChatMessage(aiResponse, AiChatMessage.Sender.AI))
                }

            } catch (e: Exception) {
                val errorMessage =
                    "Failed to get AI response. Please try again. Error: ${e.localizedMessage}"
                val lastMessageIndex = messages.indexOfLast { it.isPlaceholder }
                if (lastMessageIndex != -1) {
                    _messages[lastMessageIndex] =
                        AiChatMessage(errorMessage, AiChatMessage.Sender.AI)
                } else {
                    _messages.add(AiChatMessage(errorMessage, AiChatMessage.Sender.AI))
                }
                e.printStackTrace()
            }
        }
    }
}
