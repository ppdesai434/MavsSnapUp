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
import android.widget.Switch;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
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


    ParseUser user = new ParseUser();

    user.setUsername("anagar");
    user.setPassword("nagar123");
    user.put("mobile","123456789");
    user.setEmail("aman@mavs.uta.edu");
    user.put("name","Aman");
    user.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(ParseException e) {
        if(e==null)
        {
          Log.i("Signup","Successful");
        }
        else{
          Log.i("Signup","Failed " + e);
        }
      }
    });


    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}