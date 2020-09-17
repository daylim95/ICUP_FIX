package com.example.hhj73.fix;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class StudentDetail extends AppCompatActivity {
    String urId;

    TextView title;
    ImageView userImage;
    TextView uniqueness;
    TextView message;
    ImageView smoke;
    ImageView help;
    ImageView curfew;
    ImageView pet;
    User student;

    long now = System.currentTimeMillis();
    Date date;
    SimpleDateFormat sdf;
    String getTime;
    int StudentOld;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        Intent intent = getIntent();
        urId = intent.getStringExtra("urID");
        init();
    }

    private void init() {
        title = (TextView)findViewById(R.id.Dtitle);
        userImage = (ImageView)findViewById(R.id.userImage);
        uniqueness = (TextView)findViewById(R.id.Duniqueness);
        message = (TextView)findViewById(R.id.profileMessage);

        date = new Date(now);
        sdf = new SimpleDateFormat("yyyy");
        getTime = sdf.format(date);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com");

        //사진 검사
        StorageReference pathRef = storageReference.child("Profile/Student/"+urId+".JPG");
        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//있음
            @Override
            public void onSuccess(Uri uri) {//있음
                Glide.with(getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .into(userImage);
                userImage.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT>=21)
                    userImage.setClipToOutline(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                student = dataSnapshot.child(urId).getValue(User.class);
                String add;

                message.setText(student.getProfileMsg());
                StudentOld = 1900 + Integer.parseInt(student.getBday())/10000;
                StudentOld = Integer.parseInt(getTime) - StudentOld;

                    add = " 학생("+StudentOld+"살)";

                title.setText(student.getName()+add);
                uniqueness.setText(student.getUnique());

                if(student.getSmoking())
                    smoke = (ImageView)findViewById(R.id.smokeX);
                else
                    smoke = (ImageView)findViewById(R.id.smokeO);
                smoke.setVisibility(View.INVISIBLE);
                if(student.getCurfew())
                    curfew = (ImageView)findViewById(R.id.curfewX);
                else
                    curfew = (ImageView)findViewById(R.id.curfewO);
                curfew.setVisibility(View.INVISIBLE);

                if(student.getPet())
                    pet = (ImageView)findViewById(R.id.petX);
                else
                    pet = (ImageView)findViewById(R.id.petO);
                pet.setVisibility(View.INVISIBLE);

                if(student.getHelp())
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

    public void back(View view) { //그냥 뒤로가기
        Intent intent = new Intent(this, ChatActivitySenior.class);
            finish();
        overridePendingTransition(0, 0);
    }
}
