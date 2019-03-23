package com.mycompany.mail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;

public class RequestLogin extends AsyncTask<Void, Void, Void> {
    CheckBox checkBox;
    private int IdUser;
    private boolean isCorrect;
    String result;
    String line;
    String UserName, login, password;
    Context context;
    final String LOGIN = "login";
    final String PASSWORD = "password";
    final String ISCHECKED = "isChecked";
    public final String ID = "id";
    public final String USERNAME = "userName";

    RequestLogin(String login, String password, CheckBox checkBox, Context context){
        this.login = login;
        this.password = password;
        this.checkBox = checkBox;
        this.context = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }



    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL("http://tremyzkiybreugolnik.000webhostapp.com/CorrectLoginPas.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            String logPas = login+password;
            out.write("OutsideLoginPass=" + logPas);
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

        //обрабатываем полученный json объект
        try {
            JSONObject myjson = new JSONObject(result);
            isCorrect = myjson.getBoolean("is");
            IdUser = Integer.parseInt(myjson.getString("ID"));
            UserName = myjson.getString("UserName");
        } catch (Exception e) {
            Log.e("MyTag", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (isCorrect) {
            if (checkBox.isChecked()) {
                SharedPreferences sPref = context.getSharedPreferences("UserLogPass",MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(LOGIN, login);
                ed.putString(PASSWORD, password);
                ed.putBoolean(ISCHECKED, true);
                ed.commit();
            }

            //нужно отправить id и userName
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(ID, IdUser);
            intent.putExtra(USERNAME, UserName);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Введен неверный логин/пароль! Или слабое соединение", Toast.LENGTH_LONG).show();
        }
        super.onPostExecute(result);
    }
}
