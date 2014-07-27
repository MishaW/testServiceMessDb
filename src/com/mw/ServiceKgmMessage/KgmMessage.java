package com.mw.ServiceKgmMessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import com.mw.kgmspread.AEvent;
import com.mw.kgmspread.Subscriber;
import com.mw.testServiceMessDb.MyActivity;

/**
 * Created by mishaw on 27.07.14.
 */
public class KgmMessage extends Service implements Subscriber {

    boolean flagConnectService;
    private ServiceConnection serviceConnection;
    private Intent intentKgmAArm;
    private AEventBroadcastReceiver aEventBroadcastReceiver;
    private BroadcastReceiver dataBroadcastReceiver;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager notificationManager;
    private final String LOG_TAG = "KgmAArm::";
    private final String TAG_WAKE_LOCK = "Service MessageInDb";
    public final String BROADCAST_ACTION_ = "com.mw.testServise.intent.action.AEVENT";

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
        dataBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };


        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG_WAKE_LOCK);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
        return null;
    }

    @Override
    public void setEvent(AEvent aEvent) {
        Log.d(LOG_TAG, aEvent.toString());
//        dbMessageHelper.dbOpen(true);
//        if ( dbMessageHelper.isSushestvuet(aEvent) ){
//            if ( aEvent.value == 0 )
//                dbMessageHelper.delete(aEvent);
//        } else {
//            if ( aEvent.value == 1 ){
//                dbMessageHelper.insertAEvent(aEvent, "ZAGLUSHKA");
//                sendNotification(aEvent.toString());
//            }
//        }
//        dbMessageHelper.dbClose();
    }

    private void sendNotification(String s){
        Notification notification = new Notification(R.drawable.button, "Messgae In DB Service", System.currentTimeMillis());

        Intent intent = new Intent(this, MyActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notification.setLatestEventInfo(this, "Notification's title", s, pendingIntent);

        notification.flags += Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(1, notification);
    }
}