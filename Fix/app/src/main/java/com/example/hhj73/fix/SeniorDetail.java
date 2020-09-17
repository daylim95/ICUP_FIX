package com.example.hhj73.fix;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class SeniorDetail extends AppCompatActivity {
    String urId;
    String myId;
    Boolean type;

    TextView title;
    ImageView roomImage;
    TextView rent;
    TextView address;
    TextView uniqueness;
    TextView messgae;
    ImageView smoke;
    ImageView help;
    ImageView curfew;
    ImageView pet;
    User senior;
    ContractData contractData;
    MediaPlayer mp;

    long now = System.currentTimeMillis();
    Date date;
    SimpleDateFormat sdf;
    String getTime;
    int SeniorOld;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    DatabaseReference database_contract_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_detail);
        Intent intent = getIntent();
        myId = intent.getStringExtra("myID");
        urId = intent.getStringExtra("urID");
        type = intent.getBooleanExtra("type", true);
        init();
    }


    private void init() {

        mp = MediaPlayer.create(this, R.raw.dding);
        messgae = (TextView)findViewById(R.id.profileMessage);
        title = (TextView)findViewById(R.id.Dtitle);
        roomImage = (ImageView)findViewById(R.id.roomImage);
        rent = (TextView)findViewById(R.id.rent);
        address = (TextView)findViewById(R.id.Daddress);
        uniqueness = (TextView)findViewById(R.id.Duniqueness);

        date = new Date(now);
        sdf = new SimpleDateFormat("yyyy");
        getTime = sdf.format(date);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com");

        //사진 검사
        StorageReference pathRef = storageReference.child("Room/"+urId+".JPG");
        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//있음
            @Override
            public void onSuccess(Uri uri) {//있음
                Glide.with(getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .into(roomImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        databaseReference = firebaseDatabase.getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                senior = dataSnapshot.child(urId).getValue(User.class);
                String add;
                messgae.setText(senior.getProfileMsg());
                SeniorOld = 1900 + Integer.parseInt(senior.getBday())/10000;
                SeniorOld = Integer.parseInt(getTime) - SeniorOld;

                if(senior.getGender())
                    add = " 할머니댁 ("+SeniorOld+"세)";
                else
                    add = " 할아버지댁("+SeniorOld+"세)";

                title.setText(senior.getName()+add);
                rent.setText("월 "+moneyFormatToWon(senior.getCost()));
                address.setText(senior.getAddress());
                uniqueness.setText(senior.getUnique());

                if(senior.getSmoking())
                    smoke = (ImageView)findViewById(R.id.smokeX);
                else
                    smoke = (ImageView)findViewById(R.id.smokeO);
                smoke.setVisibility(View.INVISIBLE);
                if(senior.getCurfew())
                    curfew = (ImageView)findViewById(R.id.curfewX);
                else
                    curfew = (ImageView)findViewById(R.id.curfewO);
                curfew.setVisibility(View.INVISIBLE);

                if(senior.getPet())
                    pet = (ImageView)findViewById(R.id.petX);
                else
                    pet = (ImageView)findViewById(R.id.petO);
                pet.setVisibility(View.INVISIBLE);

                if(senior.getHelp())
                    help = (ImageView)findViewById(R.id.helpX);
                else
                    help = (ImageView)findViewById(R.id.helpO);
                help.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //myId = 학생, urId = 어르신
    public void make_first_contract(String myId, String urId){
    }

    public static String moneyFormatToWon(String inputMoney) {
        String str = String.format("%,d 원", Integer.parseInt(inputMoney));
        return  str;
    }

    public void talk(View view) {//채팅으로
        mp.start();
        make_first_contract(myId,urId);
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("myID", myId);
        intent.putExtra("urID", urId);
        startActivity(intent);
    }

    public void back(View view) { //그냥 뒤로가기
        Intent intent = new Intent(this, MatchingActivity.class);
        intent.putExtra("curUser",myId);
        if(type)
            startActivity(intent);
        else
            finish();
        overridePendingTransition(0, 0);
    }
}
