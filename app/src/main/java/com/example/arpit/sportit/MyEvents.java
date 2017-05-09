package com.example.arpit.sportit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.widget.ListView;

import java.util.ArrayList;

public class MyEvents extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MyEventsFragment())
                .commit();
    }
}
