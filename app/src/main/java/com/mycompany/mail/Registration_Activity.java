package com.mycompany.mail;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;

public class Registration_Activity extends Activity {
    EditText ETname, ETlogin, ETpass;
    String Sname, Slogin, Spass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_);
        ETname = findViewById(R.id.UserNameText);
        ETlogin = findViewById(R.id.loginText);
        ETpass = findViewById(R.id.passwordText);
    }

    public void onClick(View view) {
        try {
            Sname = ETname.getText().toString();
            Slogin = ETlogin.getText().toString();
            Spass = ETpass.getText().toString();
            String Slogpass = Slogin + Spass;

            Zapros zapros = new Zapros(Sname, Slogpass);
            zapros.start();
            TimeUnit.SECONDS.sleep(1);

            boolean emptyColumn = zapros.getIsEmpty();
            if (emptyColumn) {
                Toast.makeText(this, "Регистрация прошла успешно. Вернитесь на главную страницу", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Такой логин уже существует!", Toast.LENGTH_LONG).show();
            }

        } catch (NullPointerException e) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
