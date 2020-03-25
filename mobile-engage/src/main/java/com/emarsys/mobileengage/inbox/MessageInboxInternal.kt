package com.emarsys.mobileengage.inbox

import com.emarsys.core.api.result.ResultListener
import com.emarsys.core.api.result.Try
import com.emarsys.mobileengage.api.inbox.MessageInboxResult


interface MessageInboxInternal {
    fun fetchInboxMessages(resultListener: ResultListener<Try<MessageInboxResult>>)
}