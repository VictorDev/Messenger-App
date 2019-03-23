package com.mycompany.mail;


import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Zapros extends Thread {

    private int IdUser;
    private boolean isCorrect, isEmpty;
    private String logPas;
    String result;
    String line;
    String UserName;

    public Zapros(String logPas) {
        this.logPas = logPas;
    }

    public Zapros(String UserName, String logPas) {
        this.UserName = UserName;
        this.logPas = logPas;
    }


    @Override
    public void run() {
        try {
            if (UserName == null) {
                ZaprosLogin();
            } else {
                Registration();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getIdUser() {
        return IdUser;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public boolean getIsEmpty() {
        return isEmpty;
    }

    public String getUserName() {
        return UserName;
    }

    private void ZaprosLogin() {
        try {
            URL url = new URL("http://tremyzkiybreugolnik.000webhostapp.com/CorrectLoginPas.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
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

    }


    void Registration() {
        try {
            URL url = new URL("http://tremyzkiybreugolnik.000webhostapp.com/registration.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write("OutName=" + UserName + "&OutsideLoginPass=" + logPas);
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
    }

}
