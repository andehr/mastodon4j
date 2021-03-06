package com.sys1yagi.mastodon4j.api.entity

import com.google.gson.annotations.SerializedName

/**
 * see more https://github.com/tootsuite/documentation/blob/master/Using-the-API/API.md#results
 */
class Results(
        @SerializedName("accounts")
        val accounts: List<Account> = emptyList(), // An array of matched Accounts

        @SerializedName("statuses")
        val statuses: List<Status> = emptyList(), //	An array of matchhed Statuses

        @SerializedName("hashtags")
        val hashtags: List<Tag2> = emptyList(), //	An array of matched hashtags

        @SerializedName("groups")
        val groups: List<Group> = emptyList() // An array of matched groups
) {

}
