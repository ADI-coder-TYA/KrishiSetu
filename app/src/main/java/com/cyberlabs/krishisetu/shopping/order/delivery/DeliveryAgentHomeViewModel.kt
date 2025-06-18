package com.cyberlabs.krishisetu.shopping.order.delivery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Delivery
import com.cyberlabs.krishisetu.authentication.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryAgentHomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _deliveries = MutableStateFlow<List<Delivery>>(emptyList())
    val deliveries: StateFlow<List<Delivery>> = _deliveries

    init {
        viewModelScope.launch {
            fetchDeliveries(authRepository.getCurrUserID())
        }
    }

    private fun fetchDeliveries(currentUserID: String) {
        Amplify.API.query(
            ModelQuery.list(Delivery::class.java, Delivery.AGENT.eq(currentUserID)),
            { response ->
                Log.i("DeliveryAgentViewModel", "${response.data.items}")
                if (response.hasData()) _deliveries.value = response.data.items.filterNotNull()
                else if (response.hasErrors()) Log.e("DeliveryAgentViewModel", "GraphQL errors: ${response.errors}")
            }, { apiError ->
                Log.e("DeliveryAgentViewModel", "API error while fetching deliveries: ${apiError.message}")
            }
        )
    }

}
