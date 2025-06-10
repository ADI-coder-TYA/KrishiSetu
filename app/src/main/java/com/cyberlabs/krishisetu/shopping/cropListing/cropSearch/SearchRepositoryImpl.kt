package com.cyberlabs.krishisetu.shopping.cropListing.cropSearch

import android.util.Log
import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.core.Amplify
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SearchRepositoryImpl @Inject constructor() : SearchRepository {
    override suspend fun searchCrops(
        query: String,
        isNextPage: Boolean,
        currentNextToken: String?,
        setNextToken: (String) -> Unit
    ): List<CropSearchData> {
        val graphQuery = """
        query SearchCrops(
            ${'$'}filter: SearchableCropFilterInput,
            ${'$'}limit: Int,
            ${'$'}nextToken: String
        ) {
            searchCrops(
                filter: ${'$'}filter,
                limit: ${'$'}limit,
                nextToken: ${'$'}nextToken
            ) {
                items {
                    id
                    title
                    price
                    imageUrl
                }
                nextToken
                total
            }
        }
    """.trimIndent()

        val variables = mapOf(
            "filter" to mapOf(
                "title" to mapOf(
                    "match" to query
                )
            ),
            "limit" to 50,
            "nextToken" to if (isNextPage) currentNextToken else null
        )

        val request: GraphQLRequest<String> = SimpleGraphQLRequest(
            graphQuery,
            variables.toMutableMap(),
            String::class.java,
            GsonVariablesSerializer()
        )

        return suspendCoroutine { continuation ->
            Amplify.API.query(
                request,
                { response ->
                    if (response.hasErrors()) {
                        val errorMessages = response.errors.joinToString { it.message }
                        Log.e("SearchRepository", "GraphQL errors: $errorMessages")
                        continuation.resumeWithException(RuntimeException("GraphQL errors: $errorMessages"))
                        return@query
                    }
                    val data = response.data
                    try {
                        val crops = parseSearchCropsJson(data)
                        setNextToken(parseNextTokenJson(data))
                        continuation.resume(crops)
                    } catch (e: Exception) {
                        Log.e("SearchRepository", "Error parsing JSON: ${e.localizedMessage}", e)
                        continuation.resumeWithException(e)
                    }
                },
                { error ->
                    Log.e("SearchRepository", "API error: ${error.localizedMessage}")
                    continuation.resumeWithException(error)
                }
            )
        }
    }

    private fun parseNextTokenJson(json: String): String {
        return JSONObject(json)
            .getJSONObject("searchCrops")
            .optString("nextToken", "")
    }

    private fun parseSearchCropsJson(json: String): List<CropSearchData> {
        val cropSearchList = mutableListOf<CropSearchData>()
        val root = JSONObject(json)
            .getJSONObject("searchCrops")
            .getJSONArray("items")

        for (i in 0 until root.length()) {
            val item = root.getJSONObject(i)
            cropSearchList.add(
                CropSearchData(
                    id = item.getString("id"),
                    title = item.getString("title"),
                    price = item.getDouble("price").toInt(),
                    imageUrl = item["imageUrl"].toString()
                )
            )
        }

        return cropSearchList
    }
}

data class CropSearchData(
    val id: String,
    val title: String,
    val price: Int,
    val imageUrl: String
)