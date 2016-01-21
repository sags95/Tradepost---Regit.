package com.sinapp.sharathsind.tradepost;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sharathsind on 2015-05-26.
 */
public class Constants {
    public final static String URl="http:services.tradepost.me:8084/";
    public static String GCM_Key,username;
    public static int userid;
    public static SQLiteDatabase db;
    public void shareTextUrl(Activity a) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
//        share.putExtra(Intent.EXTRA_SUBJECT, "Check out Tradepost! It lets you trade anything locally.");
        share.putExtra(Intent.EXTRA_TEXT, "Check out Tradepost! It lets you trade anything locally.\nhttp://goo.gl/1Bdzi1");

        a.startActivity(Intent.createChooser(share, "Share link!"));
    }
}
