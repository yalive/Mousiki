package com.mousiki.shared.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AiSearchRequest(
    val searchQuery: SearchQuery? = null,
    val pageSize: Int? = null,
    val pageParam: Int? = null,
    val userIds: List<String> = emptyList()
)

@Serializable
data class SearchQuery(
    val sort: String? = null,
    val userId: String? = null,
    val searchTerm: String? = null
)

