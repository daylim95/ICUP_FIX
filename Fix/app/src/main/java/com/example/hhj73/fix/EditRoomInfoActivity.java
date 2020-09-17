package com.example.hhj73.fix;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class EditRoomInfoActivity extends AppCompatActivity {

    ImageView newPhoto;
    EditText newPrice;
    DatabaseReference databaseReference;

    String curUser;
    View view;

    static final int REQUEST_STORAGE = 234;

    Uri filePath = null;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room_info);
        mp = MediaPlayer.create(this, R.raw.dding);
        init();
    }

    public void init() {
        newPhoto = (ImageView) findViewById(R.id.newPhoto);
        newPrice = (EditText) findViewById(R.id.newPrice);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        Intent intent = getIntent();
        curUser = intent.getStringExtra("curUser");
    }

    public void editPhoto(View view) {
        mp.start();
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(checkAppPermission(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE})){
            if(intent.resolveActivity(getPackageManager())!=null)
                startActivityForResult(intent, REQUEST_STORAGE);
        }
        else
            askPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE);
    }

    public void save(View view) {
        // 저장 버튼
        mp.start();
        // 돈 입력함?
        String price = newPrice.getText().toString();

        if(price != null) {
            databaseReference.child(curUser).child("cost").setValue(price);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            String fileName = curUser + "";
            StorageReference storageRef = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com")
                    .child("Room/"+fileName+".JPG"); //올리기
            storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Toast.makeText(getApplicationContext(), "Uploaded!", Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent(this, SeniorMain.class);
            intent.putExtra("curUser", curUser);
            startActivity(intent);

            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "금액을 입력하세요 ㅡㅡ", Toast.LENGTH_SHORT).show();
        }
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
                    editPhoto(view);
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
                ((ImageView)findViewById(R.id.newPhoto)).setImageBitmap(image);
            }
            catch (IOException ex){}
        }

    }

}
