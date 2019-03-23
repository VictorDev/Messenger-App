package com.mycompany.mail;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestRegistration extends AsyncTask<Void,Void,Void> {
    String name, login, password, line, result;
    boolean isEmpty;
    Context context;


    public RequestRegistration(String name, String login, String password, Context context) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("http://tremyzkiybreugolnik.000webhostapp.com/registration.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            String logPas = login + password;
            out.write("OutName=" + name + "&OutsideLoginPass=" + logPas);
            out.close();
            conn.connect();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line + "\n");
            }
            in.close();
            result = sb.toString();
            Log.e("pass 2", "connection success" + result);
            conn.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MyTag", "ошибка!!!");
        }

        try {
            JSONObject myjson = new JSONObject(result);
            isEmpty = myjson.getBoolean("empty");
            Log.e("Registration", String.valueOf(isEmpty));

        } catch (Exception e) {
            Log.i("Registration", "error");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isEmpty) {
            Toast.makeText(context, "Регистрация прошла успешно. Вернитесь на главную страницу", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Такой логин уже существует!", Toast.LENGTH_LONG).show();
        }
        super.onPostExecute(aVoid);
    }
}
