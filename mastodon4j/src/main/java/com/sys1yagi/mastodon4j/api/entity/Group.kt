package com.sys1yagi.mastodon4j.api.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Andrew D. Robertson on 25/05/2020.
 */
data class Group (
        @SerializedName("id") val id: Long = 0L,
        @SerializedName("title") val title: String = "",
        @SerializedName("description") val description: String = "",
        @SerializedName("cover_image_url")val coverImageUrl: String = "",
        @SerializedName("is_archived") val isArchived: Boolean = false,
        @SerializedName("member_count") val memberCount: Int = 0,
        @SerializedName("created_at") val createdAt: String = ""
){
}
