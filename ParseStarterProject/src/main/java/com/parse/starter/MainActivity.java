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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity
{

  private EditText userName, userPass;
  private Button signUp;


  public void setUserName(EditText userName)
  {
    this.userName = userName;
  }

  public void setUserPass(EditText userPass)
  {
    this.userPass = userPass;
  }

  public void setSignUp(Button signUp)
  {
    this.signUp = signUp;
  }

  public EditText getUserPass()
  {
    EditText copy = this.userPass;
    return copy;
  }

  public EditText getUserName()
  {
    EditText copy = this.userName;
    return copy;
  }

  public Button getSignUp()
  {
    Button copy = this.signUp;
    return copy;
  }

  public void setUserNamePass()
  {
    this.setUserName((EditText) findViewById(R.id.userName));
    this.setUserPass((EditText) findViewById(R.id.userPass));
  }

  public void redirectUser()
  {
    if (ParseUser.getCurrentUser() != null)
    {
      Intent intent = new Intent(getApplicationContext(), UsersList.class);
      startActivity(intent);
    }

  }

  public void loginSignUp(View v)
  {
    this.setUserNamePass();

    ParseUser.logInInBackground(this.getUserName().getText().toString(), this.getUserPass().getText().toString(), new LogInCallback()
    {
      @Override
      public void done(ParseUser user, ParseException e)
      {
        if (e == null)
        {
          Log.i("logged in", "YESSSS");
          redirectUser();
        } else
        {
          //Sign the user up in this case!
          ParseUser parseUser = new ParseUser();
          parseUser.setUsername(MainActivity.this.getUserName().getText().toString());
          parseUser.setPassword(MainActivity.this.getUserPass().getText().toString());
          parseUser.signUpInBackground(new SignUpCallback()
          {
            @Override
            public void done(ParseException e)
            {
              if (e == null)
              {
                Log.i("Sign up", "Success");
                redirectUser();
              } else
              {
                Toast.makeText(MainActivity.this, e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();
              }
            }
          });
        }
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    redirectUser();

    this.setSignUp((Button) findViewById(R.id.logInSignUp));


    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}