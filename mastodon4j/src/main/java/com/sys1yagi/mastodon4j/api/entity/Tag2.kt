package com.sys1yagi.mastodon4j.api.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Andrew D. Robertson on 22/05/2020.
 */
class Tag2 (
        @SerializedName("name") val name: String = "",
        @SerializedName("url") val url: String = "",
        @SerializedName("history") val history: List<History> = emptyList()){
}