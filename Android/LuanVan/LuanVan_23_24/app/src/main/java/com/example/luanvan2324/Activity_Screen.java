package com.example.luanvan2324;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.luanvan2324.databinding.ActivityScreenBinding;


public class Activity_Screen extends AppCompatActivity {

    ActivityScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Activity_Screen.this, Activity_Home.class));
                finish();
            }
        }, 3000);


    }

}