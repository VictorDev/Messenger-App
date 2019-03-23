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

            RequestRegistration requestRegistration = new RequestRegistration(Sname,Slogin,Spass,this);
            requestRegistration.execute();

        } catch (NullPointerException e) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_LONG).show();
        }
    }
}
