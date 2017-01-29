package com.piisoft.upecfacerecognition;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import java.sql.Date;
import java.text.DateFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                ShowFdActivity();
            }
        });

        // Start AEScreenOnOffService Service

        Intent service = new Intent(getApplicationContext(), AEScreenOnOffService.class);
        getApplicationContext().startService(service);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
/*
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MainActivity.this.OpenFaceTrackerActivity();
                return true;
            }
        });*/
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.add_authorized_user) {
            OpenFaceTrackerActivity();
            return true;
        }
        if (id == R.id.show_authorized_User) {
            ShowAuthorizedUser();
            return true;
        }

        if (id == R.id.action_settings) {
            OpenFaceTrackerActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ShowAuthorizedUser(){
        Intent intent = new Intent(MainActivity.this, EnrolledGalleryActivity.class);
        startActivity(intent);
     }

    private void ShowFdActivity(){
        Intent intent = new Intent(MainActivity.this, FdActivity.class);
        startActivity(intent);
    }


    private void OpenFaceTrackerActivity() {
        Intent intent = new Intent(MainActivity.this, FaceTrackerActivity.class);
        /*EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);*/

        startActivity(intent);
        TextView tv = (TextView)  findViewById(R.id.sample_text);

        tv.setText(System.currentTimeMillis() + "");
    }

    public void OpenFaceTrackerActivity(View view) {
        OpenFaceTrackerActivity();
    }



}
