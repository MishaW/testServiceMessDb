package com.mw.ServiceKgmMessage;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MyActivity extends Activity implements AdapterView.OnItemClickListener {
    /**
     * Called when the activity is first created.
     */
//    Button button;
//    ListView listView;
    BroadcastReceiver broadcastReceiver;
    ServiceConnection serviceConnectionKgmMessage;
    Intent intentKgmMessage;
    private final String FLAG_NOTIFICATION = "flag_notification";
    final String LOG_TAG = "KgmAArm";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int count = intent.getIntExtra("count_message", 0);
                for(int i=0; i<count; ++i){
                    System.out.println("MessageInDb ----"+intent.getStringExtra(String.valueOf(i)));
                }
            }
        };

        intentKgmMessage = new Intent("com.mw.ServiceKgmMessage.KgmMessage");
        serviceConnectionKgmMessage = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

//
//        String[] from = new String[] { dbMessageHelper.FIELD_MESSAGE, dbMessageHelper.FIELD_TYPE };
//        int[] to = new int[] { R.id.tvMessage, R.id.tvType };
//            simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.item_message, cursor, from, to);
//            listView = (ListView) findViewById(R.id.listView);
//            listView.setAdapter(simpleCursorAdapter);
//            listView.setOnItemClickListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter("com.mw.ServiceKgmMessage.intent.action.DATA"));
        intentKgmMessage.putExtra(FLAG_NOTIFICATION, false);
        bindService(intentKgmMessage, serviceConnectionKgmMessage, 0);
    }

    @Override
    public void onPause(){
        super.onPause();
        unbindService(serviceConnectionKgmMessage);
        unregisterReceiver(broadcastReceiver);
    }

    public void onStartServiceKgmAArm(View v){
        startService(new Intent("com.mw.testServise.KgmAArm"));
    }

    public void onStopServiceKgmAArm(View v){
        stopService(new Intent("com.mw.testServise.KgmAArm"));
    }

    public void onStartServiceMessageInDb(View v){
        startService(new Intent(this, KgmMessage.class));
    }

    public void onStopServiceMessageInDb(View v){
        stopService(new Intent(this, KgmMessage.class));
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
