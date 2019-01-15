package com.mycompany.mail;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MessageService extends Service {
    SQLiteDatabase dbMail;
    Cursor cursor;
    String last_time;
    String server = "http://tremyzkiybreugolnik.000webhostapp.com/mychat.php";
    String link;

    public MessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        dbMail = openOrCreateDatabase("dbMail", Context.MODE_PRIVATE, null);
        dbMail.execSQL("CREATE TABLE IF NOT EXISTS dbMail (_id integer primary key, text, idAuthor, NameAuthor)");
        Intent iN = new Intent(getApplicationContext(), MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pI = PendingIntent.getActivity(getApplicationContext(),
                0, iN, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder bI = new Notification.Builder(
                getApplicationContext());
        bI.setContentIntent(pI)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("работаю...");

        Notification notification = bI.build();
        startForeground(101, notification);

        startLoop();
        return super.onStartCommand(intent, flags, startId);
    }

    void startLoop() {
        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //получаем последний id записи
                        cursor = dbMail.rawQuery("SELECT * FROM dbMail", null);
                        if (cursor.moveToLast()) {
                            last_time = cursor.getString(cursor.getColumnIndex("_id"));
                            Log.i("Service", "last_id = " + last_time);
                            link = server + "?action=select&ID=" + last_time;
                        } else {
                            link = server + "?action=select";
                        }
                        cursor.close();

                        //подключаемся к серверу
                        URL url = new URL(link);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setConnectTimeout(10000);
                        conn.connect();

                        //получаем данные с сервака
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
                        Log.e("Service", "ответ сервера " + result);
                        conn.disconnect();

                        //обработаем и запишем ответ в бд
                        if (result != null && !result.equals("")) {
                            JSONArray jsonArray = new JSONArray(result);
                            JSONObject jObject;

                            // разберем JSON массив построчно
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jObject = jsonArray.getJSONObject(i);

                                //создаем новое сообщение
                                ContentValues new_mess = new ContentValues();
                                new_mess.put("_id", Integer.parseInt(jObject.getString("ID"))); //id записи сервера(для правильной синхронизации)
                                new_mess.put("text", jObject.getString("message"));// text
                                new_mess.put("idAuthor", jObject.getString("idUser"));//или idAuthor
                                new_mess.put("NameAuthor", jObject.getString("UserName"));//NameAuthor

                                //запишем новое сообщение
                                dbMail.insert("dbMail", null, new_mess);
                                new_mess.clear();
                            }
                            sendBroadcast(new Intent(MainActivity.broadcastAction));
                        }
                    } catch (MalformedURLException mue) {
                        Log.e("Service", "ошибка урл " + mue.getMessage());
                    } catch (IOException ioe) {
                        Log.i("Service", "ошибка ввода/вывода " + ioe.getMessage());
                    } catch (JSONException je) {
                        Log.i("Service", "ошибка jsonArray " + je.getMessage());
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ie) {
                        Log.i("Service ", "ошибка остановки сервиса " + ie.getMessage());
                    }
                }
            }
        });

        thr.setDaemon(true);
        thr.start();


    }
}
