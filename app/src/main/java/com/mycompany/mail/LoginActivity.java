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
    public String login,password;


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
        sPref = getSharedPreferences("UserLogPass",MODE_PRIVATE);
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
            login = loginText.getText().toString();
            password = passwordText.getText().toString();

            RequestLogin requestLogin = new RequestLogin(login,password,remember,this);
            requestLogin.execute();

        } catch (NullPointerException e) {
            Toast.makeText(this, "Не введен логин/пароль!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void Registration(View view) {
        Intent intent = new Intent(this, Registration_Activity.class);
        startActivity(intent);
    }

}
