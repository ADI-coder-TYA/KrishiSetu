package com.cyberlabs.krishisetu.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.amplifyframework.api.graphql.GraphQLOperation
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.graphql.model.ModelSubscription
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.temporal.Temporal
import com.amplifyframework.datastore.generated.model.Message
import com.amplifyframework.datastore.generated.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val currentUserId: String = savedStateHandle["currentUserId"]!!
    val chatPartnerId: String = savedStateHandle["chatPartnerId"]!!

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    var text by mutableStateOf("")

    private var subscriptionOperation: GraphQLOperation<*>? = null

    init {
        fetchInitialMessages()
        subscribeToNewMessages()
    }

    private fun fetchInitialMessages() {
        val predicate = Message.SENDER.eq(currentUserId)
            .and(Message.RECEIVER.eq(chatPartnerId))
            .or(Message.SENDER.eq(chatPartnerId).and(Message.RECEIVER.eq(currentUserId)))

        val request = ModelQuery.list(Message::class.java, predicate)

        Amplify.API.query(
            request,
            { response ->
                if (response.hasData()) {
                    _messages.value = response.data.items.sortedBy { it.createdAt }
                    Log.i(
                        "ChatViewModel",
                        "Successfully fetched ${response.data.items.count()} messages."
                    )
                }
            },
            { error -> Log.e("ChatViewModel", "Failed to fetch messages", error) }
        )
    }

    private fun subscribeToNewMessages() {
        val request = ModelSubscription.onCreate(Message::class.java)

        subscriptionOperation = Amplify.API.subscribe(
            request,
            { _ -> Log.i("ChatViewModel", "Subscription established") },
            { newMsgEvent ->
                val newMessage = newMsgEvent.data
                val isMyMessage =
                    newMessage.sender.id == currentUserId && newMessage.receiver.id == chatPartnerId
                val isPartnerMessage =
                    newMessage.sender.id == chatPartnerId && newMessage.receiver.id == currentUserId

                if (isMyMessage || isPartnerMessage) {
                    _messages.value = (_messages.value + newMessage).sortedBy { it.createdAt }
                    Log.i("ChatViewModel", "New message received: ${newMessage.content}")
                }
            },
            { error -> Log.e("ChatViewModel", "Subscription failed", error) },
            { Log.i("ChatViewModel", "Subscription completed") }
        )
    }

    fun markMessageRead(message: Message) {
        val updatedMessage = message.copyOfBuilder()
            .isRead(true)
            .build()

        Amplify.API.mutate(
            ModelMutation.update(updatedMessage),
            { response -> Log.i("ChatViewModel", "Marked read: ${response.data.id}") },
            { error -> Log.e("ChatViewModel", "Failed to mark read", error) }
        )
    }

    /**
     * Sends a message by first querying for the sender and receiver User objects,
     * then creating and sending the message. This mirrors the original DataStore logic.
     */
    fun sendMessage(messageText: String) {
        val messageToSend = messageText
        text = "" // Clear input field immediately for better UX

        // Step 1: Query for the full User objects using the API, just like the original logic.
        val userQuery = ModelQuery.list(
            User::class.java,
            User.ID.eq(currentUserId).or(User.ID.eq(chatPartnerId))
        )

        Amplify.API.query(
            userQuery,
            { response ->
                if (response.hasData() && response.data.items.count() >= 2) {
                    val users = response.data.items
                    val sender = users.find { it.id == currentUserId }
                    val receiver = users.find { it.id == chatPartnerId }

                    if (sender != null && receiver != null) {
                        // Step 2: Once users are fetched, build the message with the full objects.
                        val newMessage = Message.builder()
                            .content(messageToSend)
                            .isRead(false)
                            .createdAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                            .updatedAt(Temporal.DateTime(OffsetDateTime.now().toString()))
                            .id(UUID.randomUUID().toString())
                            .sender(sender) // Using the full User object
                            .receiver(receiver) // Using the full User object
                            .build()

                        // Add to list for optimistic update
                        _messages.value = (_messages.value + newMessage).sortedBy { it.createdAt }

                        // Step 3: Save the new message using a mutation.
                        Amplify.API.mutate(
                            ModelMutation.create(newMessage),
                            { saveResponse ->
                                Log.i(
                                    "ChatViewModel",
                                    "Message sent: ${saveResponse.data.content}"
                                )
                            },
                            { saveError ->
                                Log.e(
                                    "ChatViewModel",
                                    "Failed to send message",
                                    saveError
                                )
                            }
                        )
                    } else {
                        Log.e("ChatViewModel", "Could not find sender or receiver in query result.")
                    }
                } else {
                    Log.e("ChatViewModel", "Could not find users in datastore.")
                    if (response.hasErrors()) {
                        Log.e(
                            "ChatViewModel",
                            "Failed to query users before sending message: ${response.errors}"
                        )
                    }
                    if (response.hasData()) {
                        Log.e(
                            "ChatViewModel",
                            "Failed to query users before sending message: ${response.data}, size: ${response.data.items.count()}"
                        )
                    }
                }
            },
            { queryError ->
                Log.e("ChatViewModel", "Failed to query users before sending message.", queryError)
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        try {
            subscriptionOperation?.cancel()
            Log.i("ChatViewModel", "Subscription cancelled.")
        } catch (e: Exception) {
            Log.e("ChatViewModel", "Failed to cancel subscription.", e)
        }
    }
}