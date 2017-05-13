package com.example.arpit.sportit.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.arpit.sportit.R;
import com.example.arpit.sportit.Fragments.ViewAllFragment;

public class ViewAll extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ViewAllFragment())
                .commit();
    }
}
