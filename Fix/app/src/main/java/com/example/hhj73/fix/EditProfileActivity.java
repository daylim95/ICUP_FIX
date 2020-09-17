package com.example.hhj73.fix;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
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

public class EditProfileActivity extends Activity {
    final static int PHOTO=111;
    private ImageView profileImageView;
    private Bitmap photo;
    private EditText message;
    String id;
    Boolean type;
    DatabaseReference databaseReference;
    User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
    }
    public void init(){
        Intent intent = getIntent();
        type = intent.getBooleanExtra("type",false);
        id = intent.getStringExtra("id");
        message = (EditText)findViewById(R.id.profileMessageInput);
        profileImageView = (ImageView)findViewById(R.id.profilePhotoInput);

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
        StorageReference pathRef = storageReference.child("Profile/Senior/"+id+".JPG");
        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//있음
            @Override
            public void onSuccess(Uri uri) {//있음
                Glide.with(getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .into(profileImageView);
                profileImageView.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT>=21)
                    profileImageView.setClipToOutline(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        profileImageView.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT>=21)
            profileImageView.setClipToOutline(true);
    }


    public void editPhoto(View view) {
        Intent intent = new Intent(this, SelectPhotoMode.class);
        intent.putExtra("id",id);
        intent.putExtra("type",true);
        startActivityForResult(intent,PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com");
        //사진 검사
        StorageReference pathRef = storageReference.child("Profile/Senior/"+id+".JPG");
        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//있음
            @Override
            public void onSuccess(Uri uri) {//있음
                profileImageView.setImageURI(null);
                Glide.with(getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .into(profileImageView);
                profileImageView.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT>=21)
                   profileImageView.setClipToOutline(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "로딩지연", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveProfile(View view) {
        databaseReference.child(id).child("profileMsg").setValue(message.getText().toString());

        Intent saveIntent;
        if(type)
            finish();
        else{
        saveIntent= new Intent(this,SeniorMain.class);
        saveIntent.putExtra("curUser",id);
        setResult(RESULT_OK,saveIntent);
        startActivity(saveIntent);
        overridePendingTransition(0, 0);}
    }

    public void logout(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
