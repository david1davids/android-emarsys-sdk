package com.emarsys

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ProcessLifecycleOwner
import com.emarsys.common.feature.InnerFeature.*
import com.emarsys.config.ConfigApi
import com.emarsys.config.EmarsysConfig
import com.emarsys.core.api.proxyApi
import com.emarsys.core.api.proxyWithLogExceptions
import com.emarsys.core.api.result.CompletionListener
import com.emarsys.core.database.DatabaseContract
import com.emarsys.core.database.trigger.TriggerEvent
import com.emarsys.core.database.trigger.TriggerType
import com.emarsys.core.feature.FeatureRegistry
import com.emarsys.di.DefaultEmarsysDependencies
import com.emarsys.di.EmarsysDependencyInjection
import com.emarsys.di.emarsys
import com.emarsys.di.isEmarsysComponentSetup
import com.emarsys.geofence.GeofenceApi
import com.emarsys.inapp.InAppApi
import com.emarsys.inbox.MessageInboxApi
import com.emarsys.mobileengage.di.mobileEngage
import com.emarsys.oneventaction.OnEventActionApi
import com.emarsys.predict.PredictApi
import com.emarsys.push.PushApi

object Emarsys {

    @JvmStatic
    val geofence: GeofenceApi
        get() = EmarsysDependencyInjection.geofence()

    @JvmStatic
    val config: ConfigApi
        get() = emarsys().config

    @JvmStatic
    val push: PushApi
        get() = EmarsysDependencyInjection.push()

    @JvmStatic
    val inApp: InAppApi
        get() = EmarsysDependencyInjection.inApp()

    @JvmStatic
    val onEventAction: OnEventActionApi
        get() = EmarsysDependencyInjection.onEventAction()

    @JvmStatic
    val messageInbox: MessageInboxApi
        get() = EmarsysDependencyInjection.messageInbox()

    @JvmStatic
    val predict: PredictApi
        get() = EmarsysDependencyInjection.predict()

    @JvmStatic
    fun setup(emarsysConfig: EmarsysConfig) {
        for (feature in emarsysConfig.experimentalFeatures) {
            FeatureRegistry.enableFeature(feature)
        }

        if (emarsysConfig.applicationCode != null) {
            FeatureRegistry.enableFeature(MOBILE_ENGAGE)
            FeatureRegistry.enableFeature(EVENT_SERVICE_V4)
        }

        if (emarsysConfig.merchantId != null) {
            FeatureRegistry.enableFeature(PREDICT)
        }


        if (!isEmarsysComponentSetup()) {
            DefaultEmarsysDependencies(emarsysConfig)
        }

        emarsys().coreSdkHandler.post {
            registerWatchDogs(emarsysConfig)
            registerLifecycleObservers()
            registerDatabaseTriggers()

            if (FeatureRegistry.isFeatureEnabled(MOBILE_ENGAGE)) {
                initializeMobileEngageContact()
            }
        }
    }

    private fun registerLifecycleObservers() {
        val appLifecycleObserver = emarsys().appLifecycleObserver
        emarsys().uiHandler.post {
            ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun setAuthenticatedContact(
        contactFieldId: Int,
        openIdToken: String,
        completionListener: CompletionListener? = null
    ) {
        if (FeatureRegistry.isFeatureEnabled(MOBILE_ENGAGE)
            || !FeatureRegistry.isFeatureEnabled(MOBILE_ENGAGE)
            && !FeatureRegistry.isFeatureEnabled(PREDICT)
        ) {
            EmarsysDependencyInjection.mobileEngageApi()
                .proxyApi(mobileEngage().coreSdkHandler)
                .setAuthenticatedContact(contactFieldId, openIdToken, completionListener)
        }

        FeatureRegistry.disableFeature(PREDICT)
    }

    @JvmStatic
    @JvmOverloads
    fun setContact(
        contactFieldId: Int,
        contactFieldValue: String,
        completionListener: CompletionListener? = null
    ) {
        if (FeatureRegistry.isFeatureEnabled(MOBILE_ENGAGE)
            || !FeatureRegistry.isFeatureEnabled(MOBILE_ENGAGE)
            && !FeatureRegistry.isFeatureEnabled(PREDICT)
        ) {
            EmarsysDependencyInjection.mobileEngageApi()
                .proxyApi(mobileEngage().coreSdkHandler)
                .setContact(contactFieldId, contactFieldValue, completionListener)
        }
        if (FeatureRegistry.isFeatureEnabled(PREDICT)) {
            EmarsysDependencyInjection.predictRestrictedApi()
                .proxyApi(mobileEngage().coreSdkHandler)
                .setContact(contactFieldId, contactFieldValue)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun clearContact(completionListener: CompletionListener? = null) {
        if (FeatureRegistry.isFeatureEnabled(MOBILE_ENGAGE)
            || !FeatureRegistry.isFeatureEnabled(MOBILE_ENGAGE)
            && !FeatureRegistry.isFeatureEnabled(PREDICT)
        ) {
            EmarsysDependencyInjection.mobileEngageApi()
                .proxyApi(mobileEngage().coreSdkHandler)
                .clearContact(completionListener)
        }
        if (FeatureRegistry.isFeatureEnabled(PREDICT)) {
            EmarsysDependencyInjection.predictRestrictedApi()
                .proxyApi(mobileEngage().coreSdkHandler)
                .clearContact()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun trackDeepLink(
        activity: Activity,
        intent: Intent,
        completionListener: CompletionListener? = null
    ) {
        EmarsysDependencyInjection.deepLinkApi()
            .proxyApi(mobileEngage().coreSdkHandler)
            .trackDeepLinkOpen(activity, intent, completionListener)
    }

    @JvmStatic
    @JvmOverloads
    fun trackCustomEvent(
        eventName: String,
        eventAttributes: Map<String, String>?,
        completionListener: CompletionListener? = null
    ) {
        EmarsysDependencyInjection.eventServiceApi()
            .proxyApi(mobileEngage().coreSdkHandler)
            .trackCustomEventAsync(eventName, eventAttributes, completionListener)
    }

    private fun registerWatchDogs(config: EmarsysConfig) {
        config.application.registerActivityLifecycleCallbacks(
            emarsys().activityLifecycleWatchdog
        )
        config.application.registerActivityLifecycleCallbacks(emarsys().currentActivityWatchdog)
    }

    private fun registerDatabaseTriggers() {
        if (FeatureRegistry.isFeatureEnabled(PREDICT)) {
            emarsys().coreSQLiteDatabase
                .registerTrigger(
                    DatabaseContract.SHARD_TABLE_NAME,
                    TriggerType.AFTER,
                    TriggerEvent.INSERT,
                    emarsys().predictShardTrigger
                )
        }

        emarsys().coreSQLiteDatabase
            .registerTrigger(
                DatabaseContract.SHARD_TABLE_NAME,
                TriggerType.AFTER,
                TriggerEvent.INSERT,
                emarsys().logShardTrigger
            )
    }

    private fun initializeMobileEngageContact() {
        val deviceInfoPayload = emarsys().deviceInfoPayloadStorage.get()
        val contactToken = emarsys().contactTokenStorage.get()
        val requestContext = emarsys().requestContext
        val clientState = emarsys().clientStateStorage.get()
        val deviceInfo = emarsys().deviceInfo

        if (contactToken == null && !requestContext.hasContactIdentification()) {
            if (clientState == null || deviceInfoPayload != null && deviceInfoPayload != deviceInfo.deviceInfoPayload) {
                EmarsysDependencyInjection.clientServiceApi()
                    .proxyWithLogExceptions()
                    .trackDeviceInfo(null)
            }
            EmarsysDependencyInjection.mobileEngageApi()
                .proxyWithLogExceptions()
                .setContact()
        }
    }
}