package com.juicy.picturetransfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class Receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, ObserveService.class);
        context.startService(myIntent);

    }
}