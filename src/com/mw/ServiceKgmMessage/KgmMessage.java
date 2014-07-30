package com.mw.ServiceKgmMessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.util.Pair;
import com.mw.kgmspread.AEvent;
import com.mw.kgmspread.AEventBroadcastReceiver;
import com.mw.kgmspread.Event;
import com.mw.kgmspread.Subscriber;

import java.util.*;

/**
 * Created by mishaw on 27.07.14.
 */
public class KgmMessage extends Service implements Subscriber {

    boolean flagConnectService;
    private ServiceConnection serviceConnection;
    private Intent intentKgmAArm;
    private AEventBroadcastReceiver aEventBroadcastReceiver;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager notificationManager;
    private final String LOG_TAG = "KgmAArm::";
    private final String TAG_WAKE_LOCK = "Service KgmMessage";
    private final String BROADCAST_ACTION_DATA = "com.mw.ServiceKgmMessage.intent.action.DATA";
    private final String COUNT_MESSAGE = "count_message";
    boolean flagSendNotification;
    private HashMap<Event, HashMap<Integer, Long>> dataMessage;
    ParserMessage parserMessage;
    private HashMap<Event, Pair<HashMap<Integer, String>, String>> dataTranslateMessage;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "MessageInDb::onCreate");
        intentKgmAArm = new Intent("com.mw.testServise.KgmAArm");
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(LOG_TAG, "MessageInDb::onServiceConnected");
                flagConnectService = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(LOG_TAG, "MessageInDb::onServiceDisconnected");
                flagConnectService = false;
            }
        };
        aEventBroadcastReceiver = new AEventBroadcastReceiver(this);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG_WAKE_LOCK);
        dataMessage = new HashMap<Event, HashMap<Integer, Long>>();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        flagSendNotification = false;
        parserMessage = new ParserMessage(getResources().getXml(R.xml.config_message));
        if (parserMessage.parsing()){
            dataTranslateMessage = parserMessage.getDataTranslateMessage();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "MessageInDb::onStartCommand");
        registerReceiver(aEventBroadcastReceiver, new IntentFilter(AEventBroadcastReceiver.BROADCAST_ACTION_AEVENT));
        bindService(intentKgmAArm, serviceConnection, BIND_AUTO_CREATE);
        wakeLock.acquire();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (flagConnectService)
            unbindService(serviceConnection);
        unregisterReceiver(aEventBroadcastReceiver);
        wakeLock.release();
        Log.d(LOG_TAG, "MessageInDb::onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        flagSendNotification = false;
        sendUpdate(null);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        flagSendNotification = true;
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        flagSendNotification = false;
        sendUpdate(null);
    }

    @Override
    public void setEvent(AEvent aEvent) {
        Log.d(LOG_TAG, aEvent.toString());

        HashMap<Integer, Long> tmpData;
        if (aEvent.event != Event.NO_MESSAGE){
            if (dataMessage.containsKey(aEvent.event)) {
                tmpData = dataMessage.get(aEvent.event);
            } else {
                tmpData = new HashMap<Integer, Long>();
                dataMessage.put(aEvent.event, tmpData);
            }

            if (tmpData.containsKey(aEvent.number)){
                if (aEvent.value == 0){
                    tmpData.remove(aEvent.number);
                }
            } else {
                if (aEvent.value != 0){
                    tmpData.put(aEvent.number, aEvent.value);
                    sendUpdate(aEvent);
                }
            }
        }
    }

    private void sendUpdate(AEvent aEvent) {
        if (flagSendNotification){
            sendNotification(getMessage(aEvent.event, aEvent.number, aEvent.value));
        } else {
            Intent intentDataMessage = new Intent(BROADCAST_ACTION_DATA);
            int countMessage = 0;
            HashMap<Integer, Long> tmpData;
            Set<Event> setEvents = dataMessage.keySet();
            for (Event event : setEvents) {
                tmpData = dataMessage.get(event);
                Set<Integer> setNumbers = tmpData.keySet();
                for (Integer number : setNumbers) {
                    long value = tmpData.get(number);

                    intentDataMessage.putExtra(String.valueOf(countMessage), getMessage(event, number, value));
                    ++countMessage;
                }
            }
            intentDataMessage.putExtra(COUNT_MESSAGE, countMessage);
            sendBroadcast(intentDataMessage);
        }
    }

    private String getMessage(Event event, int number, long value) {
        if (dataTranslateMessage.containsKey(event))
            return String.format(dataTranslateMessage.get(event).second, dataTranslateMessage.get(event).first.get(number));
        return "event=" + event.toString() + " number = " + String.valueOf(number) + " value=" + String.valueOf(value);
    }

    private void sendNotification(String s){
        Notification notification = new Notification(R.drawable.button, s, System.currentTimeMillis());

        Intent intent = new Intent(this, MyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notification.setLatestEventInfo(this, "Kgm Message", s, pendingIntent);

        notification.flags += Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(1, notification);
    }
}