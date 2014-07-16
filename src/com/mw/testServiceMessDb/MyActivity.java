package com.mw.testServiceMessDb;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import dbmessage.DBMessageHelper;

public class MyActivity extends Activity implements AdapterView.OnItemClickListener {
    /**
     * Called when the activity is first created.
     */
    Button button;
    ListView listView;
    SimpleCursorAdapter simpleCursorAdapter;
    Cursor cursor;
    Intent intent;
    DBMessageHelper dbMessageHelper;
    final String LOG_TAG = "KgmAArm";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        dbMessageHelper = new DBMessageHelper(this);
//        dbMessageHelper.dbOpen(true);
//        cursor = dbMessageHelper.getAllMessage();
//        startManagingCursor(cursor);
//
//        String[] from = new String[] { dbMessageHelper.FIELD_MESSAGE, dbMessageHelper.FIELD_TYPE };
//        int[] to = new int[] { R.id.tvMessage, R.id.tvType };
//
//        if (cursor != null){
//            simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.item_message, cursor, from, to);
//            listView = (ListView) findViewById(R.id.listView);
//            listView.setAdapter(simpleCursorAdapter);
//            listView.setOnItemClickListener(this);
//        }
//        dbMessageHelper.dbClose();
    }

    public void onStartServiceKgmAArm(View v){
        startService(new Intent("com.mw.testServise.KgmAArm"));
    }

    public void onStopServiceKgmAArm(View v){
        stopService(new Intent("com.mw.testServise.KgmAArm"));
    }

    public void onStartServiceMessageInDb(View v){
        startService(new Intent(this, MessageInDb.class));
    }

    public void onStopServiceMessageInDb(View v){
        stopService(new Intent(this, MessageInDb.class));
    }

    public void onNotification(View v){

    }

    private void updateListMessage(){}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
