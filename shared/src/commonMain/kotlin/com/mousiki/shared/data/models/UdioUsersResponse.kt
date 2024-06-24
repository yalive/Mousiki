package com.mousiki.shared.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UdioUsersResponse(

    @SerialName("data")
    val data: List<UdioUser> = emptyList()
)

@Serializable
data class UdioUser(
    val id: String,
    val username: String? = null,
    val description: String? = null,
    val website: String? = null,
    @SerialName("full_name")
    val fullName: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("following_count")
    val followingCount: Int?= null,
    @SerialName("follower_count")
    val followerCount: Int?= null,
    @SerialName("search_text")
    val searchText: String? = null
)
