package com.mycompany.mail;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {
    EditText et;
    Button bt;
    String text;
    ListView listView;
    private String userName;
    private int idUser;
    public final static String broadcastAction = "com.mycompany.mail.Update_Receiver";
    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = findViewById(R.id.message);
        bt = findViewById(R.id.button);
        listView = findViewById(R.id.mainListView);
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        idUser = intent.getIntExtra("id", 0);
        startService(new Intent(this, MessageService.class));
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                update_listview();
            }
        };
        IntentFilter ReceiverFilter = new IntentFilter(broadcastAction);
        registerReceiver(br, ReceiverFilter);
    }

    public void send(View view) {
        isCorrectId();
        text = et.getText().toString();
        if (!text.equals("")) {
            Background background = new Background();
            background.execute();
            bt.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        update_listview();
    }

    void isCorrectId() {
        if (idUser == 0) {
            Toast.makeText(this, "Ошибка аудентификации, зайдите в приложение ещё раз", Toast.LENGTH_SHORT);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    void update_listview() {

        SQLiteDatabase dbMail;
        dbMail = openOrCreateDatabase("dbMail", Context.MODE_PRIVATE, null);
        Cursor cursor;
        try {
            cursor = dbMail.rawQuery("SELECT * FROM dbMail", null);
            if (cursor.moveToFirst()) {
                ArrayList<HashMap<String, Object>> mList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> hm;
                do {
                    //если моё сообщение
                    if (cursor.getString(cursor.getColumnIndex("idAuthor")).equals(String.valueOf(idUser))) {
                        hm = new HashMap<>();
                        hm.put("author", "");
                        hm.put("myName", cursor.getString(cursor.getColumnIndex("NameAuthor")));
                        hm.put("text", "");
                        hm.put("myText", cursor.getString(cursor.getColumnIndex("text")));
                        mList.add(hm);


                    } else {
                        hm = new HashMap<>();
                        hm.put("author", cursor.getString(cursor.getColumnIndex("NameAuthor")));
                        hm.put("myName", "");
                        hm.put("text", cursor.getString(cursor.getColumnIndex("text")));
                        hm.put("myText", "");
                        mList.add(hm);
                    }
                } while (cursor.moveToNext());
                String[] from = {"author", "myName", "text", "myText"};
                int[] to = {R.id.AuthorName, R.id.myName, R.id.AuthorText, R.id.myText};
                SimpleAdapter lvAdapter = new SimpleAdapter(this, mList, R.layout.my_list_item, from, to);
                listView.setAdapter(lvAdapter);

            }
            cursor.close();
        } catch (SQLiteException sqle) {
            Log.i("Activity", "костыль гыгы");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }

    public class Background extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //подключаемся к серверу и передаем данные
                URL url = new URL("http://tremyzkiybreugolnik.000webhostapp.com/mychat.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write("action=insert&message=" + text + "&idUser=" + idUser + "&userName=" + userName);
                out.close();
                conn.connect();

                //получаем ответ
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line + "\n");
                }
                in.close();
                String result = sb.toString();
                Log.e("pass 2", "connection success" + result);


            } catch (MalformedURLException mue) {
                Log.e("Background", " Ошибка урл = " + mue.getMessage());
            } catch (IOException ioe) {
                Log.i("Background", "ошибка чтения файла = " + ioe.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            bt.setEnabled(true);
            et.setText("");
        }

    }
}
