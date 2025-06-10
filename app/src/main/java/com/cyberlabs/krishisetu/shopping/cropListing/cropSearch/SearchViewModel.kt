package com.cyberlabs.krishisetu.shopping.cropListing.cropSearch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberlabs.krishisetu.crops.CropRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val cropRepository: CropRepository
) : ViewModel() {
    private val _recentSearches = mutableListOf<String>()
    val recentSearches: List<String> = _recentSearches

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private var currentNextToken: String? = null

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<List<CropSearchData>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    fun updateQuery(query: String) {
        _query.value = query
        currentNextToken = null
    }

    fun updateRecentSearches(query: String) {
        _recentSearches.add(query)
    }

    fun searchCrops(isNextPage: Boolean = false) {
        viewModelScope.launch {
            _isSearching.value = true
            try {
                val crops =
                    searchRepository.searchCrops(_query.value, isNextPage, currentNextToken) {
                        currentNextToken = it
                    }
                val correctedCrops = crops.map { crop -> // Get the realUrls from the S3 keys
                    val imageUrl = cropRepository.getImageUrl(crop.imageUrl)
                    crop.copy(imageUrl = imageUrl)
                }
                _searchResults.value = if (isNextPage) _searchResults.value + correctedCrops else correctedCrops
                Log.i("SearchViewModel", "Search results: ${_searchResults.value}")
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error searching crops: ${e.localizedMessage}", e)
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun fetchNextPage() {
        if (currentNextToken != null && !_isSearching.value) {
            searchCrops(isNextPage = true)
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
}
