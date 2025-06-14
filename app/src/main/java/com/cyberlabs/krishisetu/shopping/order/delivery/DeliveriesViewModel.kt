package com.cyberlabs.krishisetu.shopping.order.delivery

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
class DeliveriesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _deliveries = MutableStateFlow<List<Delivery>>(emptyList())
    val deliveries: StateFlow<List<Delivery>> = _deliveries

    private val isFarmer = savedStateHandle.get<Boolean>("isFarmer") == true

    init {
        viewModelScope.launch {
            fetchDeliveries(authRepository.getCurrUserID(), isFarmer)
        }
    }

    private fun fetchDeliveries(currentUserId: String, isFarmer: Boolean) {
        if (currentUserId == "null") return

        if (isFarmer) {
            Amplify.API.query(
                ModelQuery.list(Delivery::class.java, Delivery.FARMER.eq(currentUserId)),
                { response ->
                    if (response.hasData()) _deliveries.value = response.data.items.filterNotNull()
                    else if (response.hasErrors()) Log.e("DeliveriesViewModel", "GraphQL errors: ${response.errors}")
                }, { apiError ->
                    Log.e("DeliveriesViewModel", "API error while fetching deliveries for farmer: ${apiError.message}")
                }
            )
        } else {
            Amplify.API.query(
                ModelQuery.list(Delivery::class.java, Delivery.BUYER.eq(currentUserId)),
                {response ->
                    if (response.hasData()) _deliveries.value = response.data.items.filterNotNull()
                    else if (response.hasErrors()) Log.e("DeliveriesViewModel", "GraphQL errors: ${response.errors}")
                }, { apiError ->
                    Log.e("DeliveriesViewModel", "API error while fetching deliveries for buyer: ${apiError.message}")
                }
            )
        }
    }

}
