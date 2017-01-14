package com.placepickexample;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.placepickexample.Fragments.HomeFrag;


import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    FragmentManager manager;

    public static TextView loc;
    public static Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         toolbar=(Toolbar)findViewById(R.id.myToolbar);

        setSupportActionBar(toolbar);

      //  menu=(ImageView)findViewById(R.id.menu);
       // toolbar.hasExpandedActionView();



       /* serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    startService(intent);
                }
                else
                    stopService(intent);
            }
        });*/

        Toast toast=Toast.makeText(this, "Swipe to delete reminders!!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
         manager=getSupportFragmentManager();
         manager.beginTransaction()
                .replace(R.id.container,new HomeFrag())
                 .addToBackStack(null)
                .commit();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("count:",getSupportFragmentManager().getBackStackEntryCount()+"");

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            //getSupportFragmentManager().popBackStack();

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit App")
                    .setMessage("Close RemindMe@ and activate all reminders!")
                    .setPositiveButton("GoodBye",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    manager.beginTransaction()
                                            .replace(R.id.container,new HomeFrag())
                                            .addToBackStack(null)
                                            .commit();
                                }
                            });
            AlertDialog alert = builder.create();

            alert.setCancelable(false);

            alert.show();
               }

       /* if (time > 2000+System.currentTimeMillis())
        {
            super.onBackPressed();
        }
        time=System.currentTimeMillis();
*/

    }


}

