package com.example.blind_assistance_project.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import com.example.blind_assistance_project.R;

public class Splash extends AppCompatActivity {

    MediaPlayer welcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        welcome=MediaPlayer.create(Splash.this,R.raw.welcome);
        welcome.start();
        Handler h= new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this, Feature_activity.class);
                startActivity(i);
                finish();
            }
        },4000);
    }
}