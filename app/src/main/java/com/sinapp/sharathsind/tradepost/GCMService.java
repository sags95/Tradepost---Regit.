package com.sinapp.sharathsind.tradepost;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by sharathsind on 2015-12-30.
 */
public class GCMService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

    }

}
