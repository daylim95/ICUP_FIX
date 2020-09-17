package com.example.hhj73.fix;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class SeniorFirst extends AppCompatActivity {

    final int REQUEST_STORAGE = 123;
    Uri filePath;
    View view;
    String id;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_first);
        Intent intent = getIntent();
        mp = MediaPlayer.create(this, R.raw.login);
        id = intent.getStringExtra("curUser");
    }

    boolean checkAppPermission(String[] requestPermission){
        boolean[] requestResult = new boolean[requestPermission.length];
        for(int i=0; i< requestResult.length; i++){
            requestResult[i] = (ContextCompat.checkSelfPermission(this,
                    requestPermission[i]) == PackageManager.PERMISSION_GRANTED );
            if(!requestResult[i]){
                return false;
            }
        }
        return true;
    }

    void askPermission(String[] requestPermission, int REQ_PERMISSION) {
        ActivityCompat.requestPermissions(
                this,
                requestPermission,
                REQ_PERMISSION
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {//승인확인
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (checkAppPermission(permissions)) {
                    Toast.makeText(this, "승인완료",Toast.LENGTH_SHORT).show();
                    loadPhoto(view);
                    // 퍼미션 동의했을 때 할 일
                } else {
                    Toast.makeText(this, "사용 불가",Toast.LENGTH_SHORT).show();
                    // 퍼미션 동의하지 않았을 때 할일
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //인텐트 갔다온 결과
    if(requestCode == REQUEST_STORAGE && resultCode==RESULT_OK){
        try{
            filePath = data.getData();
            Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            Matrix matrix = new Matrix();
            matrix.postRotate(90); //90도 회전
            Bitmap.createBitmap(image, 0,0,
                    image.getWidth(),image.getHeight(),matrix,true);
            ((ImageView)findViewById(R.id.RoomImgRegister)).setImageBitmap(image);
            ((TextView)findViewById(R.id.text1)).setText("");

        }
        catch (IOException ex){}
    }

    }
    public void loadPhoto(View view) { //앨범에서 신분증 가져오기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(checkAppPermission(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE})){
            if(intent.resolveActivity(getPackageManager())!=null)
                startActivityForResult(intent,REQUEST_STORAGE);
        }
        else
            askPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE);
    }

    public void startSenior(View view) {//시작
        mp.start();// 로그인 소리
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String fileName = id+"";
        StorageReference storageRef = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com")
                .child("Room/"+fileName+".JPG"); //올리기
        storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SeniorFirst.this, "Uploaded!", Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = new Intent(this, SeniorMain.class);
        intent.putExtra("curUser",id);
        startActivity(intent);
    }
}
