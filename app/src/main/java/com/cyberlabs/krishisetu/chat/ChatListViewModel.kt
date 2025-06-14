package com.cyberlabs.krishisetu.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Message
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserRole
import com.cyberlabs.krishisetu.util.users.querySuspend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ChatListDataItem(
    val userId: String,
    val name: String,
    val role: UserRole,          // from your generated enum
    val profilePicUrl: String? = null   // optional local fallback
)

@HiltViewModel
class ChatListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val role = savedStateHandle.get<String>("role")

    var chatList by mutableStateOf<List<ChatListDataItem>>(emptyList())
        private set

    var currentUserId by mutableStateOf<String?>(null)
        private set

    init {
        fetchCurrentUserId()
    }

    fun fetchCurrentUserId() {
        Amplify.Auth.getCurrentUser(
            { user ->
                currentUserId = user.userId
                loadChatPartners()
            },
            { error -> Log.e("ChatListViewModel", "Error getting current user", error) }
        )
    }

    private fun loadChatPartners() {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Fetch all messages involving me:
            val allMessages = querySuspend<Message>(
                Message.SENDER.eq(currentUserId).or(Message.RECEIVER.eq(currentUserId))
            )

            // 2. Extract the other-party IDs:
            val partnerIds = allMessages
                .map { if (it.sender.id == currentUserId) it.receiver.id else it.sender.id }
                .toSet()
                .filterNot { it == currentUserId }

            // 3. Load each partner’s User record:
            val partners = partnerIds.map { partnerId ->
                async {
                    querySuspend<User>(
                        User.ID.eq(partnerId)
                    ).firstOrNull()?.let { user ->
                        ChatListDataItem(
                            userId = user.id,
                            name = user.name,
                            role = user.role,
                            profilePicUrl = user.profilePicture
                        )
                    }
                }
            }.awaitAll().filterNotNull()

            withContext(Dispatchers.Main) {
                // 4. Publish back to Compose
                chatList = partners.sortedBy { it.name.lowercase() }
            }
        }
    }
}
