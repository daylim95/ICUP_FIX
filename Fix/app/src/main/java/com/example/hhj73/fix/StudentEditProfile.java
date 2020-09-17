package com.example.hhj73.fix;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class StudentEditProfile extends AppCompatActivity {
    String id;
    final int PHOTO = 123;
    ImageView imageView;
    EditText message;
    DatabaseReference databaseReference;
    User me;
    Boolean type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit_profile);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getBooleanExtra("type", false);
        message = (EditText)findViewById(R.id.profileMessageInput);
        imageView = (ImageView)findViewById(R.id.profilePhotoInput);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me =  dataSnapshot.child(id).getValue(User.class);
                message.setText(me.getProfileMsg());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com");
        //사진 검사
        StorageReference pathRef = storageReference.child("Profile/Student/"+id+".JPG");
        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//있음
            @Override
            public void onSuccess(Uri uri) {//있음
                Glide.with(getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .into(imageView);
                imageView.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT>=21)
                    imageView.setClipToOutline(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public void StudentEditPhoto(View view) {

        Intent intent = new Intent(this, SelectPhotoMode.class);
        intent.putExtra("id",id);
        intent.putExtra("type",false);
        startActivityForResult(intent,PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com");
        //사진 검사
        StorageReference  pathRef= storageReference.child("Profile/Student/"+id+".JPG");

        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//있음

            @Override
            public void onSuccess(Uri uri) {//있음
                imageView.setImageURI(null);
                Glide.with(getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .into(imageView);
                imageView.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT>=21)
                    imageView.setClipToOutline(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StudentEditProfile.this, "로딩지연", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveProfile(View view) {
        databaseReference.child(id).child("profileMsg").setValue(message.getText().toString());

        if(type)
            finish();
        else{
        Intent intent = new Intent(this, MatchingActivity.class);
        intent.putExtra("curUser",id);
        startActivity(intent);
        overridePendingTransition(0, 0);}
    }

    public void logOut(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
