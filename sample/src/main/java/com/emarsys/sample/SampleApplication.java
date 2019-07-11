package com.emarsys.sample;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.emarsys.Emarsys;
import com.emarsys.config.EmarsysConfig;
import com.emarsys.mobileengage.api.EventHandler;
import com.emarsys.mobileengage.api.NotificationEventHandler;
import com.emarsys.sample.configuration.MobileEngageCredentials;

import org.json.JSONObject;

public class SampleApplication extends Application implements EventHandler, NotificationEventHandler {

    private static final String TAG = "SampleApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Emarsys SDK version: " + com.emarsys.BuildConfig.VERSION_NAME);

        EmarsysConfig config = new EmarsysConfig.Builder()
                .application(this)
                .predictMerchantId("1428C8EE286EC34B")
                .mobileEngageApplicationCode(MobileEngageCredentials.INTEGRATION_APPLICATION_CODE)
                .contactFieldId(3)
                .inAppEventHandler(this)
                .notificationEventHandler(this)
                .build();

        createNotificationChannels();

        Emarsys.setup(config);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel("ems_sample_news", "News", "News and updates go into this channel", NotificationManager.IMPORTANCE_HIGH);
            createNotificationChannel("ems_sample_messages", "Messages", "Important messages go into this channel", NotificationManager.IMPORTANCE_HIGH);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String id, String name, String description, int importance) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void handleEvent(String eventName, JSONObject payload) {
        Toast.makeText(this, eventName + " - " + payload.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleEvent(Context context, String eventName, @Nullable JSONObject payload) {
        Toast.makeText(this, eventName + " - " + payload.toString(), Toast.LENGTH_LONG).show();
    }
}