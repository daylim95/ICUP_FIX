package com.example.hhj73.fix;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class poll_senior extends AppCompatActivity {
    String myID;
    String urID;
    Boolean Null;

    RadioGroup r1, r2, r3, r4, r5, r6, r7;
    EditText congest;

    DatabaseReference databaseReference;
    DatabaseReference databaseReference_family;

    Family family;
    String room;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_senior);
        init();
    }

    private void init() {
        mp = MediaPlayer.create(this, R.raw.x);
        Intent intent = getIntent();
        myID = intent.getStringExtra("myID");
        urID = intent.getStringExtra("urID");
        Null = intent.getBooleanExtra("null",false);
        if(Null)
            room = "null+"+myID;
        else
            room = urID+"+"+myID;

        databaseReference = FirebaseDatabase.getInstance().getReference("evaluation");
        databaseReference_family = FirebaseDatabase.getInstance().getReference("families");
        databaseReference_family.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                family = dataSnapshot.child(room).getValue(Family.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        r1 = (RadioGroup)findViewById(R.id.radioGroup1);
        r2 = (RadioGroup)findViewById(R.id.radioGroup2);
        r3 = (RadioGroup)findViewById(R.id.radioGroup3);
        r4 = (RadioGroup)findViewById(R.id.radioGroup4);
        r5 = (RadioGroup)findViewById(R.id.radioGroup5);
        r6 = (RadioGroup)findViewById(R.id.radioGroup6);
        r7 = (RadioGroup)findViewById(R.id.radioGroup7);
        congest = (EditText)findViewById(R.id.congest);
    }

    public void submit(View view) {//제출
        int score = 0;
        switch (r1.getCheckedRadioButtonId()){
            case R.id.radio1_1:
                score += 3;
                break;
            case R.id.radio1_2:
                score += 2;
                break;
            case R.id.radio1_3:

                break;
        }
        switch (r2.getCheckedRadioButtonId()){
            case R.id.radio2_1:
                score += 3;
                break;
            case R.id.radio2_2:
                score += 2;
                break;
            case R.id.radio2_3:

                break;
        }
        switch (r3.getCheckedRadioButtonId()){
            case R.id.radio3_1:
                score += 3;
                break;
            case R.id.radio3_2:
                score += 2;
                break;
            case R.id.radio3_3:

                break;
        }
        switch (r4.getCheckedRadioButtonId()){
            case R.id.radio4_1:
                score += 3;
                break;
            case R.id.radio4_2:
                score += 2;
                break;
            case R.id.radio4_3:

                break;
        }
        switch (r5.getCheckedRadioButtonId()){
            case R.id.radio5_1:
                score += 3;
                break;
            case R.id.radio5_2:
                score += 2;
                break;
            case R.id.radio5_3:

                break;
        }
        switch (r6.getCheckedRadioButtonId()){
            case R.id.radio6_1:
                score += 3;
                break;
            case R.id.radio6_2:
                score += 2;
                break;
            case R.id.radio6_3:

                break;
        }
        switch (r7.getCheckedRadioButtonId()){
            case R.id.radio7_1:
                score += 3;
                break;
            case R.id.radio7_2:
                score += 2;
                break;
            case R.id.radio7_3:

                break;
        }

        double resultScore = (double)score/(double) 21 * 100;
        String newRoom = urID + "+null";

        mp.start();
        databaseReference.child(urID).child(myID).child("score").setValue(resultScore);
        databaseReference.child(urID).child(myID).child("congestion").setValue(congest.getText().toString());
        databaseReference_family.child(room).setValue(null);//지우기
        if(!Null){ //상대가 평가 안했다면 남겨두기
            databaseReference_family.child(newRoom).setValue(family);}
            Intent intent = new Intent(this, SeniorMain.class);
            intent.putExtra("curUser", myID);
            startActivity(intent);
            finish();
    }
}
