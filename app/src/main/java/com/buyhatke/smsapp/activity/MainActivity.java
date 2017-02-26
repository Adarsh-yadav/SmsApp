package com.buyhatke.smsapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.buyhatke.smsapp.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static MainActivity inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    FloatingActionButton fab;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Messaging");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        insertDummyContactWrapper();
    }

    private void insertDummyContactWrapper() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasWriteContactsPermission = 0;

            hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }else{
            refreshSmsInbox();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    smsListView = (ListView) findViewById(R.id.SMSList);
                    arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
                    smsListView.setAdapter(arrayAdapter);
                    smsListView.setOnItemClickListener(this);

                    refreshSmsInbox();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void refreshSmsInbox() {
        Uri uri = Uri.parse("content://sms/");
        Cursor c = getContentResolver().query(uri, new String[]{"Distinct address", "thread_id"}, null, null, null);
        ArrayList<String> list;
        list = new ArrayList<>();
        list.clear();
        arrayAdapter.clear();
        int msgCount = c.getCount();
        if (c.moveToFirst()) {
            for (int ii = 0; ii < msgCount; ii++) {
                String address = c.getString(c.getColumnIndexOrThrow("address")).toString();
                list.add(address);
                arrayAdapter.add(address);
                c.moveToNext();
            }
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        Intent inent = new Intent(MainActivity.this, MessageActivity.class);
        inent.putExtra("ADDRESS", smsMessagesList.get(pos));
        startActivity(inent);
    }
}
