package com.placepickexample;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import me.wangyuwei.particleview.ParticleView;

public class SplashActivity extends AppCompatActivity {

    ParticleView mParticleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);}
        catch (Exception e){

        }
        setContentView(R.layout.activity_splash);
        mParticleView=(ParticleView)findViewById(R.id.particle);
        mParticleView.startAnim();
        mParticleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {

                Toast toast=Toast.makeText(SplashActivity.this, "Welcome", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
