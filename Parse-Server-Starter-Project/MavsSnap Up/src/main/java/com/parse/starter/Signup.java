package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void signup(View myview){


        EditText emailedText = (EditText) findViewById(R.id.emailEditText);
        EditText passwordedText = (EditText) findViewById(R.id.editPassword);
        EditText cnfpasswordedText = (EditText) findViewById(R.id.editCnfPassword);
        EditText nameedText = (EditText) findViewById(R.id.editName);
        EditText phoneedText = (EditText) findViewById(R.id.editPhone);
        ParseUser user = new ParseUser();

        if(passwordedText.getText().toString().matches(cnfpasswordedText.getText().toString())){
            user.setUsername(emailedText.getText().toString());
            user.setPassword(passwordedText.getText().toString());
            user.put("mobile",phoneedText.getText().toString());
            user.setEmail(emailedText.getText().toString());
            user.put("name",nameedText.getText().toString());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null)
                    {
                        Toast.makeText(Signup.this, "Signup successful", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this, "Password and Confirm Password should be Identical", Toast.LENGTH_SHORT).show();
        }




    }



    public void login(View myview){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
