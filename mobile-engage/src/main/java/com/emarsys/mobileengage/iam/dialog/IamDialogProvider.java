package com.emarsys.mobileengage.iam.dialog;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class IamDialogProvider {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public IamDialog provideDialog(String campaignId, String sid, String url, String requestId) {
        return IamDialog.create(campaignId, sid, url, requestId);
    }

}
