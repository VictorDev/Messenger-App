package com.mycompany.mail;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;


public class LoginActivity extends Activity {
    EditText loginText, passwordText;
    CheckBox remember;
    SharedPreferences sPref;
    final String LOGIN = "login";
    final String PASSWORD = "password";
    final String ISCHECKED = "isChecked";
    public final String ID = "id";
    public final String USERNAME = "userName";
    public boolean isCorrect = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginText = findViewById(R.id.loginText);
        passwordText = findViewById(R.id.passwordText);
        remember = findViewById(R.id.loginCheckBox);
    }

    //заполняю поля логином и паролем
    @Override
    protected void onStart() {
        super.onStart();
        sPref = getPreferences(MODE_PRIVATE);
        if (sPref.getString(LOGIN, "") != null) {
            loginText.setText(sPref.getString(LOGIN, ""));
        }
        if (sPref.getString(PASSWORD, "") != null) {
            passwordText.setText(sPref.getString(PASSWORD, ""));
        }
        if (sPref.getBoolean(ISCHECKED, false)) {
            remember.setChecked(true);
        }
    }

    public void Login(View view) {
        try {
            String login = loginText.getText().toString();
            String password = passwordText.getText().toString();
            String logPas = login + password;

            Zapros zapros = new Zapros(logPas);
            zapros.start();
            TimeUnit.SECONDS.sleep(1);

            isCorrect = zapros.getIsCorrect();
            if (isCorrect) {
                int idUser = zapros.getIdUser();
                String userName = zapros.getUserName();
                if (remember.isChecked()) {
                    sPref = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString(LOGIN, login);
                    ed.putString(PASSWORD, password);
                    ed.putBoolean(ISCHECKED, true);
                    ed.commit();
                }

                //нужно отправить id и userName
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra(ID, idUser);
                intent.putExtra(USERNAME, userName);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Введен неверный логин/пароль! Или слабое соединение", Toast.LENGTH_LONG).show();
            }

        } catch (NullPointerException e) {
            Toast.makeText(this, "Не введен логин/пароль!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (InterruptedException ie) {
            Log.i("LoginActivity", "ошибка остановки активности " + ie.getMessage());
        }
    }

    public void Registration(View view) {
        Intent intent = new Intent(this, Registration_Activity.class);
        startActivity(intent);
    }


}
