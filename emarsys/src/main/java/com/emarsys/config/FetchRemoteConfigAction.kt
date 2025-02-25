package com.emarsys.config

import android.app.Activity
import com.emarsys.core.activity.ActivityLifecycleAction
import com.emarsys.core.api.proxyApi
import com.emarsys.core.api.result.CompletionListener
import com.emarsys.mobileengage.di.mobileEngage

class FetchRemoteConfigAction(private val configInternal: ConfigInternal,
                              private val completionListener: CompletionListener) : ActivityLifecycleAction {

    override fun execute(activity: Activity?) {
        configInternal.proxyApi(mobileEngage().coreSdkHandler)
                .refreshRemoteConfig(completionListener)
    }
}
