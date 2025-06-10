package com.cyberlabs.krishisetu.shopping.cropListing.cropSearch

interface SearchRepository {
    suspend fun searchCrops(
        query: String,
        isNextPage: Boolean,
        currentNextToken: String?,
        setNextToken: (String) -> Unit
    ): List<CropSearchData> // Returns crop ID's list
}