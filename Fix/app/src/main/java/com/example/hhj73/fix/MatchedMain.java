package com.example.hhj73.fix;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MatchedMain extends AppCompatActivity {
    String myID;
    String urID;
    Boolean type;
    ImageView p_stu;
    ImageView p_sin;
    TextView title;
    Family family;
    String senior;
    String student;
    MediaPlayer mp;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy년 M월 dd일");
    Date today;
    Date expireDate;
    String room;

    DatabaseReference databaseReference;
    DatabaseReference databaseReference_contract;
    ContractData contractData;
    StorageReference pathRef;
    Intent intentProfile; // 프로필 누를 때
    final  int CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_main);
        init();
    }

    private void init() {
        mp = MediaPlayer.create(this, R.raw.dding);
        today = new Date();
        final Intent intent = getIntent();
        myID = intent.getStringExtra("myID");
        urID = intent.getStringExtra("urID");
        type = intent.getBooleanExtra("type",true);

        p_sin = (ImageView)findViewById(R.id.p_senior);
        p_stu = (ImageView)findViewById(R.id.p_student);
        title = (TextView)findViewById(R.id.matchedTitle);

        if(type){
            senior = myID;
            student = urID;
        }else{
            senior = urID;
            student = myID;
        }
        room = student+"+"+senior;

        databaseReference = FirebaseDatabase.getInstance().getReference("families");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                family = dataSnapshot.child(room).getValue(Family.class); // 객체 받아옴
                title.setText(family.getName_senior()+" X " +family.getName_student()); // 타이틀
                room = family.getId_student()+"+"+family.getId_senior();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadProfile();

        databaseReference_contract = FirebaseDatabase.getInstance().getReference("contracts");
        databaseReference_contract.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contractData = dataSnapshot.child(room).getValue(ContractData.class);
                try{
                    // 계약 만료일
                    expireDate = dateFormat.parse(contractData.getExpirationdate());

                    long dif = expireDate.getTime() - today.getTime();
                    dif = dif / ( 24*60*60*1000);

                    if(dif<0){ // 계약 만료
                        if(type){ // 어르신
                            Intent intent1 = new Intent(getApplicationContext(), poll_senior.class);
                            intent1.putExtra("myID", myID);
                            intent1.putExtra("urID", family.getId_student());
                            if(urID.equals("null"))
                                intent1.putExtra("null", true);
                            startActivity(intent1);
                            finish();
                        }else{ //학생
                            Intent intent1 = new Intent(getApplicationContext(), Poll.class);
                            intent1.putExtra("myID", myID);
                            intent1.putExtra("urID", family.getId_senior());
                            if(urID.equals("null"))
                                intent1.putExtra("null", true);
                            startActivity(intent1);
                            finish();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "계약 종료까지\n"+dif+"일 남았습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // 클릭이벤트
        if(myID.equals(senior)){ // 내가 어르신이면
            // 내사진 누르면 프로필 수정으로
            // 상대사진 누르면 정보보기로
            p_sin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentProfile = new Intent(getApplicationContext(), EditProfileActivity.class);
                    intentProfile.putExtra("id", myID);
                    intentProfile.putExtra("type", true);
                    startActivityForResult(intentProfile, CODE);
                }
            });

            p_stu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentProfile = new Intent(getApplicationContext(), StudentDetail.class);
                    intentProfile.putExtra("urID", urID);
                    startActivityForResult(intentProfile, CODE);
                }
            });

        }else{ // 학생이면
            p_sin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentProfile = new Intent(getApplicationContext(), SeniorDetail.class);
                    intentProfile.putExtra("myID", myID);
                    intentProfile.putExtra("urID", urID);
                    intentProfile.putExtra("type", false);
                    startActivityForResult(intentProfile, CODE);
                }
            });

            p_stu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intentProfile = new Intent(getApplicationContext(), StudentEditProfile.class);
                    intentProfile.putExtra("id", myID);
                    intentProfile.putExtra("type", true);
                    startActivityForResult(intentProfile, CODE);
                }
            });
        }
    }

    public void goChat(View view) { //==========================================구현해주셈

        mp.start();
        Intent intent; // 매칭후 채팅 엑티비티 만들고 연결.
        intent = new Intent(this, ChatAfterMatchedActivity.class);
        intent.putExtra("myID", myID);
        intent.putExtra("urID", urID);
        startActivity(intent);
    }

    public void loadProfile(){
        //프로필사진
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com");

        p_stu.setImageURI(null);
        p_sin.setImageURI(null);
        // 어르신 사진 검사
        pathRef = storageReference.child("Profile/Senior/"+senior+".JPG");
        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//있음
            @Override
            public void onSuccess(Uri uri) {//있음
                Glide.with(getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .into(p_sin);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        // 학생 사진 검사
        pathRef = storageReference.child("Profile/Student/"+student+".JPG");
        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//있음
            @Override
            public void onSuccess(Uri uri) {//있음
                Glide.with(getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .into(p_stu);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        // 둥글게 출력
        p_sin.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21)
            p_sin.setClipToOutline(true);
        p_stu.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21)
            p_stu.setClipToOutline(true);
    }

    public void goContract(View view) { // 계약서 목록 출력 =========================구현해 주라긔
        Intent intent = new Intent(this,ContractMatchedActivity.class);
        intent.putExtra("room", room);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if(requestCode == CODE){
        loadProfile();
    }
    }

    public void emergency(View view) { // 비상전화
        mp.start();
        Intent intent = new Intent(getApplicationContext(), emergency.class);
        if(type)
            intent.putExtra("urPhone", family.phone_student);
        else
            intent.putExtra("urPhone", family.phone_senior);
        startActivityForResult(intent, CODE);
    }

}
