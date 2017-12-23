package com.parse.starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class UsersList extends AppCompatActivity
{

  private ListView listView;
  ArrayList<String> users = new ArrayList<>();
  ArrayAdapter<String> arrayAdapter;
  List<String> isFollowing = new ArrayList<>();

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater menuInflater;
    menuInflater = new MenuInflater(this);
    menuInflater.inflate(R.menu.tweet_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    if (item.getItemId() == R.id.tweet)
    {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Send a tweet");

      final EditText tweetEdit = new EditText(this);
      builder.setView(tweetEdit);

      builder.setPositiveButton("Send", new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
          Log.i("Tweet Content", tweetEdit.getText().toString());

          //Store the tweet

          ParseObject tweet = new ParseObject("Tweet");

          tweet.put("username", ParseUser.getCurrentUser().getUsername());
          tweet.put("tweet", tweetEdit.getText().toString());

          tweet.saveInBackground(new SaveCallback()
          {
            @Override
            public void done(ParseException e)
            {
              if (e == null)
              {
                Toast.makeText(UsersList.this, "Tweet sent!", Toast.LENGTH_SHORT).show();
              } else
              {
                Toast.makeText(UsersList.this, "Couldn't send :( Try again later!", Toast.LENGTH_SHORT).show();
              }
            }
          });
        }
      });

      builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
          dialogInterface.cancel();
        }
      });
      builder.show();

    } else if (item.getItemId() == R.id.logOut)
    {
      ParseUser.logOut();
      Intent intent = new Intent(getApplicationContext(), MainActivity.class);
      startActivity(intent);
    } else if (item.getItemId() == R.id.feedItem)
    {
      Intent feedIntent = new Intent(getApplicationContext(), Feed.class);
      startActivity(feedIntent);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_users_list);
    setTitle("User List");

        /*Check if the user has an isFollowing list!*/
    if (ParseUser.getCurrentUser().getList("isFollowing") == null)
    {
      List<String> list = new ArrayList<>();
      ParseUser.getCurrentUser().put("isFollowing", list);
    }

    //Do the user search
    ParseQuery<ParseUser> query = ParseUser.getQuery();
    query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
    query.findInBackground(new FindCallback<ParseUser>()
    {
      @Override
      public void done(List<ParseUser> objects, ParseException e)
      {
        if (e == null && objects.size() > 0)
        {
          for (ParseUser user : objects)
          {
            users.add(user.getUsername());
          }
          arrayAdapter.notifyDataSetChanged();
          for (String s : users)
          {
            if (ParseUser.getCurrentUser().getList("isFollowing").contains(s))
            {
              //If it has a user name, then check true!
              listView.setItemChecked(users.indexOf(s), true);
            } else
            {
              listView.setItemChecked(users.indexOf(s), false);
            }
          }
        }
      }
    });

    listView = (ListView) findViewById(R.id.listView);
    arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, users);
    //Allow the user to check mark
    listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    listView.setAdapter(arrayAdapter);
    arrayAdapter.notifyDataSetChanged();

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
      {
        //The view we are working with (the item that was clicked)
        //This refers to the check box or the check mark
        CheckedTextView checkedTextView = (CheckedTextView) view;

        if (checkedTextView.isChecked())
        {
          Log.i("Info", "Row is Checked");
          //If it's checked, then the user is following!
          addOrRemove(1, i);
        } else
        {
          Log.i("Info", "Not Checked");
          //Remove following
          addOrRemove(0, i);
        }
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback()
        {
          @Override
          public void done(ParseException e)
          {
            if (e == null)
            {
              Log.i("Save", "Success");
            } else
            {
              Log.i("Save", e.getMessage().substring(e.getMessage().indexOf(" ")));
            }
          }
        });
      }
    });


  }

  public void addOrRemove(int i, int index)
  {
    if (i == 1)
    {
      isFollowing.add(users.get(index));
    } else
    {
      isFollowing.remove(users.get(index));
    }
    ParseUser.getCurrentUser().remove("isFollowing");
    ParseUser.getCurrentUser().put("isFollowing", isFollowing);
  }


}
