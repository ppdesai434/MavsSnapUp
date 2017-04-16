/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


//    ParseQuery<ParseObject> query = ParseQuery.getQuery("Item");
//    query.getInBackground("28lZvgRsWK", new GetCallback<ParseObject>() {
//      @Override
//      public void done(ParseObject object, ParseException e) {
//        if(e==null && object != null)
//        {
//          object.put("score",250);
//          object.saveInBackground();
//          Log.i("Object Value",object.getString("username"));
//          Log.i("Object Value", Integer.toString(object.getInt("score")));
//        }
//      }
//    });


//    ParseObject item = new ParseObject("Item");
//    item.put("username","PD");
//    item.put("score",85);
//    item.saveInBackground(new SaveCallback() {
//      @Override
//      public void done(ParseException e) {
//          if(e==null)
//          {
//            Log.i("SaveInBackground","Successful");
//          }
//        else{
//            Log.i("SaveInBackground","Failed. Error:"+e.toString());
//          }
//
//      }
//    });




//    ParseUser user = new ParseUser();
//
//    user.setUsername("tejaswi@mavs.uta.edu");
//    user.setPassword("teja123");
//    user.put("mobile","123456789");
//    user.setEmail("tejaswi@mavs.uta.edu");
//    user.put("name","Tejaswi");
//    user.signUpInBackground(new SignUpCallback() {
//      @Override
//      public void done(ParseException e) {
//        if(e==null)
//        {
//          Log.i("Signup","Successful");
//        }
//        else{
//          Log.i("Signup","Failed " + e);
//        }
//      }
//    });


    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  public void signUp(View view){

      Intent intent = new Intent(this, Signup.class);
      startActivity(intent);

  }


    public void login(View myview){

        EditText emailText = (EditText) findViewById(R.id.emailEditText);
        EditText passwordText = (EditText) findViewById(R.id.passwordEditText);
        if( emailText.getText().toString().matches("") || passwordText.getText().toString().matches("")){
            Toast.makeText(this, "Username and password are required", Toast.LENGTH_SHORT).show();
        }
        else{

            ParseUser user = new ParseUser();
            user.logInInBackground(emailText.getText().toString(), passwordText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null)
                    {
                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}
