package com.example.arpit.sportit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static android.R.attr.onClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView myEventsTextView = (TextView) findViewById(R.id.myEvents);
        myEventsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MyEvents.class);
                startActivity(intent);
            }
        });

        TextView viewAllTextView = (TextView) findViewById(R.id.viewAll);
        viewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ViewAll.class);
                startActivity(intent);
            }
        });

        TextView attendnigTextView = (TextView) findViewById(R.id.attending);
        attendnigTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Attending.class);
                startActivity(intent);
            }
        });
    }
}
