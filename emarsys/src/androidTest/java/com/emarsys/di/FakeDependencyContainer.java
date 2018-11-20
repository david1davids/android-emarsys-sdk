package com.emarsys.di;

import android.os.Handler;

import com.emarsys.core.DefaultCoreCompletionHandler;
import com.emarsys.core.DeviceInfo;
import com.emarsys.core.activity.ActivityLifecycleWatchdog;
import com.emarsys.core.activity.CurrentActivityWatchdog;
import com.emarsys.core.database.CoreSQLiteDatabase;
import com.emarsys.mobileengage.MobileEngageInternal;
import com.emarsys.mobileengage.RequestContext;
import com.emarsys.mobileengage.api.NotificationEventHandler;
import com.emarsys.mobileengage.deeplink.DeepLinkInternal;
import com.emarsys.mobileengage.iam.InAppInternal;
import com.emarsys.mobileengage.iam.InAppPresenter;
import com.emarsys.mobileengage.inbox.InboxInternal;
import com.emarsys.predict.PredictInternal;

public class FakeDependencyContainer implements EmarysDependencyContainer {
    private final Handler coreSdkHandler;
    private final ActivityLifecycleWatchdog activityLifecycleWatchdog;
    private final CurrentActivityWatchdog currentActivityWatchdog;
    private final CoreSQLiteDatabase coreSQLiteDatabase;
    private final DeviceInfo deviceInfo;
    private final MobileEngageInternal mobileEngageInternal;
    private final InboxInternal inboxInternal;
    private final InAppInternal inAppInternal;
    private final DeepLinkInternal deepLinkInternal;
    private final DefaultCoreCompletionHandler completionHandler;
    private final RequestContext requestContext;
    private final InAppPresenter inAppPresenter;
    private final NotificationEventHandler notificationEventHandler;
    private final PredictInternal predictInternal;
    private final Runnable predictShardTrigger;

    public FakeDependencyContainer(
            Handler coreSdkHandler,
            ActivityLifecycleWatchdog activityLifecycleWatchdog,
            CurrentActivityWatchdog currentActivityWatchdog,
            CoreSQLiteDatabase coreSQLiteDatabase,
            DeviceInfo deviceInfo,
            MobileEngageInternal mobileEngageInternal,
            InboxInternal inboxInternal,
            InAppInternal inAppInternal,
            DeepLinkInternal deepLinkInternal,
            DefaultCoreCompletionHandler completionHandler,
            RequestContext requestContext,
            InAppPresenter inAppPresenter,
            NotificationEventHandler notificationEventHandler,
            PredictInternal predictInternal,
            Runnable predictShardTrigger) {
        this.coreSdkHandler = coreSdkHandler;
        this.activityLifecycleWatchdog = activityLifecycleWatchdog;
        this.currentActivityWatchdog = currentActivityWatchdog;
        this.coreSQLiteDatabase = coreSQLiteDatabase;
        this.deviceInfo = deviceInfo;
        this.mobileEngageInternal = mobileEngageInternal;
        this.inboxInternal = inboxInternal;
        this.inAppInternal = inAppInternal;
        this.deepLinkInternal = deepLinkInternal;
        this.completionHandler = completionHandler;
        this.requestContext = requestContext;
        this.inAppPresenter = inAppPresenter;
        this.notificationEventHandler = notificationEventHandler;
        this.predictInternal = predictInternal;
        this.predictShardTrigger = predictShardTrigger;
    }

    @Override
    public Handler getCoreSdkHandler() {
        return coreSdkHandler;
    }

    @Override
    public ActivityLifecycleWatchdog getActivityLifecycleWatchdog() {
        return activityLifecycleWatchdog;
    }

    @Override
    public CurrentActivityWatchdog getCurrentActivityWatchdog() {
        return currentActivityWatchdog;
    }

    @Override
    public CoreSQLiteDatabase getCoreSQLiteDatabase() {
        return coreSQLiteDatabase;
    }

    @Override
    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    @Override
    public MobileEngageInternal getMobileEngageInternal() {
        return mobileEngageInternal;
    }

    @Override
    public InboxInternal getInboxInternal() {
        return inboxInternal;
    }

    @Override
    public InAppInternal getInAppInternal() {
        return inAppInternal;
    }

    @Override
    public DeepLinkInternal getDeepLinkInternal() {
        return deepLinkInternal;
    }

    @Override
    public DefaultCoreCompletionHandler getCoreCompletionHandler() {
        return completionHandler;
    }

    @Override
    public RequestContext getRequestContext() {
        return requestContext;
    }

    @Override
    public InAppPresenter getInAppPresenter() {
        return inAppPresenter;
    }

    @Override
    public NotificationEventHandler getNotificationEventHandler() {
        return notificationEventHandler;
    }

    @Override
    public PredictInternal getPredictInternal() {
        return predictInternal;
    }

    @Override
    public Runnable getPredictShardTrigger() {
        return predictShardTrigger;
    }
}
