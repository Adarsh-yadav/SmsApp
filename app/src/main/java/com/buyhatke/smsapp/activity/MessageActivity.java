package com.buyhatke.smsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.buyhatke.smsapp.R;

import java.util.ArrayList;

/**
 * Created by adarsh on 26/02/17.
 */

public class MessageActivity extends AppCompatActivity {

    String address;
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    final public static int SEND_SMS = 101;
    String mobile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        address = getIntent().getStringExtra("ADDRESS");
        getSupportActionBar().setTitle(address);
        InitialiseMessagesList();
        GetAllMessagesOf(address);
    }

    private void InitialiseMessagesList() {
        smsListView = (ListView) findViewById(R.id.SMSList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
    }

    private void GetAllMessagesOf(String address) {
        StringBuilder smsBuilder = new StringBuilder();
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_ALL = "content://sms/";
        arrayAdapter.clear();
        try {
            Uri uri1 = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Cursor cur = getContentResolver().query(uri1, projection, "address='" + address + "'", null, "date desc");
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int int_Type = cur.getInt(index_Type);

                    arrayAdapter.add(strbody);

                    smsBuilder.append("[ ");
                    smsBuilder.append(strAddress + ", ");
                    smsBuilder.append(intPerson + ", ");
                    smsBuilder.append(strbody + ", ");
                    smsBuilder.append(longDate + ", ");
                    smsBuilder.append(int_Type);
                    smsBuilder.append(" ]\n\n");
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            } // end if

        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }
    }

    public void SendMessage(View v) {
        checkAndroidVersion("8722708035");
    }

    public void checkAndroidVersion(String mobile){
        this.mobile= mobile;
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.SEND_SMS);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.SEND_SMS},SEND_SMS);
                return;
            }else{
                SendMessage();
            }
        } else {
            SendMessage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SendMessage();
                } else {
                    Toast.makeText(MessageActivity.this, "SEND_SMS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void SendMessage() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MessageActivity.this);
        alertDialog.setMessage("Enter your message");

        final String[] password = new String[1];

        final EditText input = new EditText(MessageActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("SEND",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendSms(mobile, input.getText().toString());
                    }
                });

        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void sendSms(String edt_phoneNo, String message)
    {
        try {
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(edt_phoneNo, null, message, null, null);
            Toast.makeText(MessageActivity.this, "Message sent", Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception ex) {
            Toast.makeText(MessageActivity.this, "Your sms has failed...",
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();

        }
    }
}
