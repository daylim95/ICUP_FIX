package com.example.hhj73.fix;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Join extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
    }

    public void seniorJoin(View view) {
        Intent intent = new Intent(Join.this, SeniorJoinActivity.class);
        startActivity(intent); //액티비티 이동
        overridePendingTransition(0, 0);
    }

    public void studentJoin(View view) {
        Intent intent = new Intent(Join.this, StudentJoinActivity.class);
        startActivity(intent); //액티비티 이동
        overridePendingTransition(0, 0);
    }
}
