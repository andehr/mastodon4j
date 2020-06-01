package com.sys1yagi.mastodon4j.api.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Andrew D. Robertson on 22/05/2020.
 */
data class History (
    @SerializedName("day") val day: Long = 0L,
    @SerializedName("uses") val uses: Int = 0,
    @SerializedName("accounts") val accounts: Int = 0){
}