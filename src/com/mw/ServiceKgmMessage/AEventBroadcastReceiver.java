package com.mw.ServiceKgmMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mw.kgmspread.AEvent;
import com.mw.kgmspread.Subscriber;

/**
 * Created by mishaw on 17.05.14.
 */
public class AEventBroadcastReceiver extends BroadcastReceiver {

    public final static String BROADCAST_ACTION_AEVENT = "com.mw.testServise.intent.action.AEVENT";
    public final static String DATE_AEVENT = "Date_AEvent";
    Subscriber subscriber;

    AEventBroadcastReceiver(Subscriber subscriber){
        this.subscriber = subscriber;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (subscriber != null){
            AEvent aEvent = intent.getParcelableExtra(DATE_AEVENT);
            subscriber.setEvent(aEvent);
        }
    }
}
