package com.iexceed.common

interface PostAppSecToken {
    fun executeAfterAppSecToken(refreshServerNonce: Boolean)

}
