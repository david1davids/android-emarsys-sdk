package com.emarsys.mobileengage.fake

import android.os.Handler
import com.emarsys.core.CoreCompletionHandler
import com.emarsys.core.DefaultCoreCompletionHandler
import com.emarsys.core.activity.ActivityLifecycleWatchdog
import com.emarsys.core.activity.CurrentActivityWatchdog
import com.emarsys.core.database.CoreSQLiteDatabase
import com.emarsys.core.database.repository.Repository
import com.emarsys.core.database.repository.SqlSpecification
import com.emarsys.core.device.DeviceInfo
import com.emarsys.core.di.addDependency
import com.emarsys.core.di.getDependency
import com.emarsys.core.endpoint.ServiceEndpointProvider
import com.emarsys.core.provider.activity.CurrentActivityProvider
import com.emarsys.core.provider.timestamp.TimestampProvider
import com.emarsys.core.provider.uuid.UUIDProvider
import com.emarsys.core.request.RestClient
import com.emarsys.core.response.ResponseHandlersProcessor
import com.emarsys.core.shard.ShardModel
import com.emarsys.core.shard.ShardModelRepository
import com.emarsys.core.storage.CoreStorageKey
import com.emarsys.core.storage.KeyValueStore
import com.emarsys.core.storage.StringStorage
import com.emarsys.core.util.FileDownloader
import com.emarsys.core.util.log.Logger
import com.emarsys.mobileengage.MobileEngageInternal
import com.emarsys.mobileengage.MobileEngageRequestContext
import com.emarsys.mobileengage.RefreshTokenInternal
import com.emarsys.mobileengage.client.ClientServiceInternal
import com.emarsys.mobileengage.deeplink.DeepLinkInternal
import com.emarsys.mobileengage.di.MobileEngageDependencyContainer
import com.emarsys.mobileengage.endpoint.Endpoint
import com.emarsys.mobileengage.event.EventHandlerProvider
import com.emarsys.mobileengage.event.EventServiceInternal
import com.emarsys.mobileengage.geofence.GeofenceInternal
import com.emarsys.mobileengage.iam.InAppInternal
import com.emarsys.mobileengage.iam.InAppPresenter
import com.emarsys.mobileengage.iam.model.buttonclicked.ButtonClicked
import com.emarsys.mobileengage.iam.model.displayediam.DisplayedIam
import com.emarsys.mobileengage.inbox.InboxInternal
import com.emarsys.mobileengage.inbox.MessageInboxInternal
import com.emarsys.mobileengage.inbox.model.NotificationCache
import com.emarsys.mobileengage.notification.ActionCommandFactory
import com.emarsys.mobileengage.push.PushInternal
import com.emarsys.mobileengage.push.PushTokenProvider
import com.emarsys.mobileengage.responsehandler.MobileEngageTokenResponseHandler
import com.emarsys.mobileengage.storage.MobileEngageStorageKey
import com.nhaarman.mockitokotlin2.mock

class FakeMobileEngageDependencyContainer(
        coreSdkHandler: Handler = mock(),
        activityLifecycleWatchdog: ActivityLifecycleWatchdog = mock(),
        currentActivityWatchdog: CurrentActivityWatchdog = mock(),
        coreSQLiteDatabase: CoreSQLiteDatabase = mock(),
        deviceInfo: DeviceInfo = mock(),
        shardRepository: Repository<ShardModel, SqlSpecification> = mock(),
        timestampProvider: TimestampProvider = mock(),
        uuidProvider: UUIDProvider = mock(),
        logShardTrigger: Runnable = mock(),
        mobileEngageInternal: MobileEngageInternal = mock(),
        loggingMobileEngageInternal: MobileEngageInternal = mock(),
        pushInternal: PushInternal = mock(),
        loggingPushInternal: PushInternal = mock(),
        inboxInternal: InboxInternal = mock(),
        loggingInboxInternal: InboxInternal = mock(),
        messageInboxInternal: MessageInboxInternal = mock(),
        loggingMessageInboxInternal: MessageInboxInternal = mock(),
        inAppInternal: InAppInternal = mock(),
        loggingInAppInternal: InAppInternal = mock(),
        deepLinkInternal: DeepLinkInternal = mock(),
        loggingDeepLinkInternal: DeepLinkInternal = mock(),
        eventServiceInternal: EventServiceInternal = mock(),
        loggingEventServiceInternal: EventServiceInternal = mock(),
        clientServiceInternal: ClientServiceInternal = mock(),
        loggingClientServiceInternal: ClientServiceInternal = mock(),
        refreshTokenInternal: RefreshTokenInternal = mock(),
        completionHandler: DefaultCoreCompletionHandler = mock(),
        requestContext: MobileEngageRequestContext = mock(),
        inAppPresenter: InAppPresenter = mock(),
        logger: Logger = mock(),
        deviceInfoPayloadStorage: StringStorage = mock(),
        contactFieldValueStorage: StringStorage = mock(),
        contactTokenStorage: StringStorage = mock(),
        refreshContactTokenStorage: StringStorage = mock(),
        clientStateStorage: StringStorage = mock(),
        pushTokenStorage: StringStorage = mock(),
        responseHandlersProcessor: ResponseHandlersProcessor = mock(),
        notificationCache: NotificationCache = mock(),
        restClient: RestClient = mock(),
        pushTokenProvider: PushTokenProvider = mock(),
        clientServiceProvider: ServiceEndpointProvider = mock(),
        eventServiceProvider: ServiceEndpointProvider = mock(),
        deepLinkServiceProvider: ServiceEndpointProvider = mock(),
        mobileEngageV2ServiceProvider: ServiceEndpointProvider = mock(),
        inboxServiceProvider: ServiceEndpointProvider = mock(),
        messageInboxServiceProvider: ServiceEndpointProvider = mock(),
        clientServiceStorage: StringStorage = mock(),
        eventServiceStorage: StringStorage = mock(),
        deepLinkServiceStorage: StringStorage = mock(),
        mobileEngageV2ServiceStorage: StringStorage = mock(),
        inboxServiceStorage: StringStorage = mock(),
        messageInboxServiceStorage: StringStorage = mock(),
        logLevelStorage: StringStorage = mock(),
        fileDownloader: FileDownloader = mock(),
        actionCommandFactory: ActionCommandFactory = mock(),
        silentMessageActionCommandFactory: ActionCommandFactory = mock(),
        notificationEventHandlerProvider: EventHandlerProvider = mock(),
        silentMessageEventHandlerProvider: EventHandlerProvider = mock(),
        geofenceEventHandlerProvider: EventHandlerProvider = mock(),
        currentActivityProvider: CurrentActivityProvider = mock(),
        geofenceInternal: GeofenceInternal = mock(),
        loggingGeofenceInternal: GeofenceInternal = mock(),
        buttonClickedRepository: Repository<ButtonClicked, SqlSpecification> = mock(),
        displayedIamRepository: Repository<DisplayedIam, SqlSpecification> = mock(),
        keyValueStore: KeyValueStore = mock(),
        contactTokenResponseHandler: MobileEngageTokenResponseHandler = mock()
) : MobileEngageDependencyContainer {
    override val dependencies: MutableMap<String, Any?> = mutableMapOf()

    init {
        addDependency(dependencies, coreSdkHandler, "coreSdkHandler")
        addDependency(dependencies, activityLifecycleWatchdog)
        addDependency(dependencies, currentActivityWatchdog)
        addDependency(dependencies, coreSQLiteDatabase)
        addDependency(dependencies, deviceInfo)
        addDependency(dependencies, shardRepository, "shardRepository")
        addDependency(dependencies, timestampProvider)
        addDependency(dependencies, uuidProvider)
        addDependency(dependencies, completionHandler)
        addDependency(dependencies, logger)
        addDependency(dependencies, responseHandlersProcessor)
        addDependency(dependencies, restClient)
        addDependency(dependencies, logLevelStorage, CoreStorageKey.LOG_LEVEL.key)
        addDependency(dependencies, fileDownloader)
        addDependency(dependencies, currentActivityProvider)
        addDependency(dependencies, logShardTrigger, "logShardTrigger")
        addDependency(dependencies, mobileEngageInternal, "defaultInstance")
        addDependency(dependencies, loggingMobileEngageInternal, "loggingInstance")
        addDependency(dependencies, pushInternal, "defaultInstance")
        addDependency(dependencies, loggingPushInternal, "loggingInstance")
        addDependency(dependencies, inboxInternal, "defaultInstance")
        addDependency(dependencies, loggingInboxInternal, "loggingInstance")
        addDependency(dependencies, messageInboxInternal, "defaultInstance")
        addDependency(dependencies, loggingMessageInboxInternal, "loggingInstance")
        addDependency(dependencies, inAppInternal, "defaultInstance")
        addDependency(dependencies, loggingInAppInternal, "loggingInstance")
        addDependency(dependencies, deepLinkInternal, "defaultInstance")
        addDependency(dependencies, loggingDeepLinkInternal, "loggingInstance")
        addDependency(dependencies, eventServiceInternal, "defaultInstance")
        addDependency(dependencies, loggingEventServiceInternal, "loggingInstance")
        addDependency(dependencies, clientServiceInternal, "defaultInstance")
        addDependency(dependencies, loggingClientServiceInternal, "loggingInstance")
        addDependency(dependencies, refreshTokenInternal)
        addDependency(dependencies, requestContext)
        addDependency(dependencies, inAppPresenter)
        addDependency(dependencies, deviceInfoPayloadStorage, MobileEngageStorageKey.DEVICE_INFO_HASH.key)
        addDependency(dependencies, contactFieldValueStorage, MobileEngageStorageKey.CONTACT_FIELD_VALUE.key)
        addDependency(dependencies, contactTokenStorage, MobileEngageStorageKey.CONTACT_TOKEN.key)
        addDependency(dependencies, clientStateStorage, MobileEngageStorageKey.CLIENT_STATE.key)
        addDependency(dependencies, pushTokenStorage, MobileEngageStorageKey.PUSH_TOKEN.key)
        addDependency(dependencies, refreshContactTokenStorage, MobileEngageStorageKey.PUSH_TOKEN.key)
        addDependency(dependencies, notificationCache)
        addDependency(dependencies, pushTokenProvider)
        addDependency(dependencies, clientServiceProvider, Endpoint.ME_V3_CLIENT_HOST)
        addDependency(dependencies, eventServiceProvider, Endpoint.ME_V3_EVENT_HOST)
        addDependency(dependencies, deepLinkServiceProvider, Endpoint.DEEP_LINK)
        addDependency(dependencies, mobileEngageV2ServiceProvider, Endpoint.ME_BASE_V2)
        addDependency(dependencies, inboxServiceProvider, Endpoint.INBOX_BASE)
        addDependency(dependencies, messageInboxServiceProvider, Endpoint.ME_V3_INBOX_HOST)
        addDependency(dependencies, clientServiceStorage, MobileEngageStorageKey.CLIENT_SERVICE_URL.key)
        addDependency(dependencies, eventServiceStorage, MobileEngageStorageKey.EVENT_SERVICE_URL.key)
        addDependency(dependencies, deepLinkServiceStorage, MobileEngageStorageKey.DEEPLINK_SERVICE_URL.key)
        addDependency(dependencies, mobileEngageV2ServiceStorage, MobileEngageStorageKey.ME_V2_SERVICE_URL.key)
        addDependency(dependencies, inboxServiceStorage, MobileEngageStorageKey.INBOX_SERVICE_URL.key)
        addDependency(dependencies, messageInboxServiceStorage, MobileEngageStorageKey.MESSAGE_INBOX_SERVICE_URL.key)
        addDependency(dependencies, actionCommandFactory, "notificationActionCommandFactory")
        addDependency(dependencies, silentMessageActionCommandFactory, "silentMessageActionCommandFactory")
        addDependency(dependencies, notificationEventHandlerProvider, "notificationEventHandlerProvider")
        addDependency(dependencies, silentMessageEventHandlerProvider, "silentMessageEventHandlerProvider")
        addDependency(dependencies, geofenceEventHandlerProvider, "geofenceEventHandlerProvider")
        addDependency(dependencies, geofenceInternal, "defaultInstance")
        addDependency(dependencies, loggingGeofenceInternal, "loggingInstance")
        addDependency(dependencies, buttonClickedRepository, "buttonClickedRepository")
        addDependency(dependencies, displayedIamRepository, "displayedIamRepository")
        addDependency(dependencies, keyValueStore)
        addDependency(dependencies, contactTokenResponseHandler, "contactTokenResponseHandler")
    }

    override fun getCoreSdkHandler(): Handler = getDependency(dependencies, "coreSdkHandler")

    override fun getActivityLifecycleWatchdog(): ActivityLifecycleWatchdog = getDependency(dependencies)

    override fun getCurrentActivityWatchdog(): CurrentActivityWatchdog = getDependency(dependencies)

    override fun getCoreSQLiteDatabase(): CoreSQLiteDatabase = getDependency(dependencies)

    override fun getDeviceInfo(): DeviceInfo = getDependency(dependencies)

    override fun getTimestampProvider(): TimestampProvider = getDependency(dependencies)

    override fun getUuidProvider(): UUIDProvider = getDependency(dependencies)

    override fun getLogShardTrigger(): Runnable = getDependency(dependencies, "logShardTrigger")

    override fun getLogger(): Logger = getDependency(dependencies)

    override fun getRestClient(): RestClient = getDependency(dependencies)

    override fun getFileDownloader(): FileDownloader = getDependency(dependencies)

    override fun getShardRepository(): Repository<ShardModel, SqlSpecification> = getDependency<ShardModelRepository>()

    override fun getMobileEngageInternal(): MobileEngageInternal = getDependency(dependencies, "defaultInstance")

    override fun getLoggingMobileEngageInternal(): MobileEngageInternal = getDependency(dependencies, "loggingInstance")

    override fun getClientServiceInternal(): ClientServiceInternal = getDependency(dependencies, "defaultInstance")

    override fun getLoggingClientServiceInternal(): ClientServiceInternal = getDependency(dependencies, "loggingInstance")

    override fun getInboxInternal(): InboxInternal = getDependency(dependencies, "defaultInstance")

    override fun getLoggingInboxInternal(): InboxInternal = getDependency(dependencies, "loggingInstance")

    override fun getMessageInboxInternal(): MessageInboxInternal = getDependency(dependencies, "defaultInstance")

    override fun getLoggingMessageInboxInternal(): MessageInboxInternal = getDependency(dependencies, "loggingInstance")

    override fun getInAppInternal(): InAppInternal = getDependency(dependencies, "defaultInstance")

    override fun getLoggingInAppInternal(): InAppInternal = getDependency(dependencies, "loggingInstance")

    override fun getDeepLinkInternal(): DeepLinkInternal = getDependency(dependencies, "defaultInstance")

    override fun getLoggingDeepLinkInternal(): DeepLinkInternal = getDependency(dependencies, "loggingInstance")

    override fun getPushInternal(): PushInternal = getDependency(dependencies, "defaultInstance")

    override fun getLoggingPushInternal(): PushInternal = getDependency(dependencies, "loggingInstance")

    override fun getEventServiceInternal(): EventServiceInternal = getDependency(dependencies, "defaultInstance")

    override fun getLoggingEventServiceInternal(): EventServiceInternal = getDependency(dependencies, "loggingInstance")

    override fun getRefreshTokenInternal(): RefreshTokenInternal = getDependency(dependencies)

    override fun getCoreCompletionHandler(): CoreCompletionHandler = getDependency(dependencies)

    override fun getRequestContext(): MobileEngageRequestContext = getDependency(dependencies)

    override fun getInAppPresenter(): InAppPresenter = getDependency(dependencies)

    override fun getDeviceInfoPayloadStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.DEVICE_INFO_HASH.key)

    override fun getContactFieldValueStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.CONTACT_FIELD_VALUE.key)

    override fun getContactTokenStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.CONTACT_TOKEN.key)

    override fun getClientStateStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.CLIENT_STATE.key)

    override fun getPushTokenStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.PUSH_TOKEN.key)

    override fun getRefreshContactTokenStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.REFRESH_TOKEN.key)

    override fun getLogLevelStorage(): StringStorage = getDependency(CoreStorageKey.LOG_LEVEL.key)

    override fun getResponseHandlersProcessor(): ResponseHandlersProcessor = getDependency(dependencies)

    override fun getNotificationCache(): NotificationCache = getDependency(dependencies)

    override fun getPushTokenProvider(): PushTokenProvider = getDependency(dependencies)

    override fun getClientServiceProvider(): ServiceEndpointProvider = getDependency(Endpoint.ME_V3_CLIENT_HOST)

    override fun getEventServiceProvider(): ServiceEndpointProvider = getDependency(Endpoint.ME_V3_EVENT_HOST)

    override fun getDeepLinkServiceProvider(): ServiceEndpointProvider = getDependency(Endpoint.DEEP_LINK)

    override fun getInboxServiceProvider(): ServiceEndpointProvider = getDependency(Endpoint.INBOX_BASE)

    override fun getMessageInboxServiceProvider(): ServiceEndpointProvider = getDependency(Endpoint.ME_V3_INBOX_HOST)

    override fun getMobileEngageV2ServiceProvider(): ServiceEndpointProvider = getDependency(Endpoint.ME_BASE_V2)

    override fun getClientServiceStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.CLIENT_SERVICE_URL.key)

    override fun getEventServiceStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.EVENT_SERVICE_URL.key)

    override fun getDeepLinkServiceStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.DEEPLINK_SERVICE_URL.key)

    override fun getInboxServiceStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.INBOX_SERVICE_URL.key)

    override fun getMessageInboxServiceStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.MESSAGE_INBOX_SERVICE_URL.key)

    override fun getMobileEngageV2ServiceStorage(): StringStorage = getDependency(dependencies, MobileEngageStorageKey.ME_V2_SERVICE_URL.key)

    override fun getNotificationActionCommandFactory(): ActionCommandFactory = getDependency(dependencies, "notificationActionCommandFactory")

    override fun getSilentMessageActionCommandFactory(): ActionCommandFactory = getDependency(dependencies, "silentMessageActionCommandFactory")

    override fun getNotificationEventHandlerProvider(): EventHandlerProvider = getDependency(dependencies, "notificationEventHandlerProvider")

    override fun getSilentMessageEventHandlerProvider(): EventHandlerProvider = getDependency(dependencies, "silentMessageEventHandlerProvider")

    override fun getGeofenceEventHandlerProvider(): EventHandlerProvider = getDependency(dependencies, "geofenceEventHandlerProvider")

    override fun getCurrentActivityProvider(): CurrentActivityProvider = getDependency(dependencies)

    override fun getGeofenceInternal(): GeofenceInternal = getDependency(dependencies, "defaultInstance")

    override fun getLoggingGeofenceInternal(): GeofenceInternal = getDependency(dependencies, "loggingInstance")

    override fun getButtonClickedRepository(): Repository<ButtonClicked, SqlSpecification> = getDependency(dependencies, "buttonClickedRepository")

    override fun getDisplayedIamRepository(): Repository<DisplayedIam, SqlSpecification> = getDependency(dependencies, "displayedIamRepository")

    override fun getKeyValueStore(): KeyValueStore = getDependency(dependencies)

    override fun getContactTokenResponseHandler(): MobileEngageTokenResponseHandler = getDependency(dependencies, "contactTokenResponseHandler")
}
