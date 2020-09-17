package com.example.hhj73.fix;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by skrud on 2018-05-21.
 */

public class SelectPhotoMode extends Activity {
    static final int REQUEST_IMAGE_CAPTURE=123;
    static final int REQUEST_STORAGE =234;
    ImageView imageView;
    View view;
    private static String imagePath = "";
    Bitmap bitmapPhoto;
    Uri filePath = null;
    String id;
    Boolean type;//어르신 true
    MediaPlayer mp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_photo_mode);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getBooleanExtra("type",true);
        mp = MediaPlayer.create(this, R.raw.dding);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap image = null;
        if(requestCode == REQUEST_STORAGE && resultCode==RESULT_OK){
            try{
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                Matrix matrix = new Matrix();
                matrix.postRotate(90); //90도 회전
                Bitmap.createBitmap(image, 0,0,
                        image.getWidth(),image.getHeight(),matrix,true);
                if(image!=null){
                    filePath = data.getData();
                    Intent intent = getIntent();
                    intent.putExtra("profilePhoto",image);
                    setResult(RESULT_OK,intent);
                    Toast.makeText(getApplicationContext(),"Photo Selected!",Toast.LENGTH_SHORT).show();
                    imageView = (ImageView)findViewById(R.id.profileImageChange);
                    imageView.setImageBitmap(image);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Didn't Selected Any Photo!",Toast.LENGTH_SHORT).show();
                }
            }
            catch (IOException ex){}
        }else if(requestCode==REQUEST_IMAGE_CAPTURE){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            Matrix matrix = new Matrix();
            Bitmap.createBitmap(imageBitmap, 0,0,
                    imageBitmap.getWidth(),imageBitmap.getHeight(),matrix,true);
            ((ImageView)findViewById(R.id.profileImageChange)).setImageBitmap(imageBitmap);
            File file = createImageFile();
            if(file!=null){
                FileOutputStream fout;
                try{
                    fout = new FileOutputStream(file);
                    imageBitmap.compress(Bitmap.CompressFormat.PNG,70,fout);
                    fout.flush();
                }catch (Exception e){}
                filePath = Uri.fromFile(file);
                Toast.makeText(getApplicationContext(),"Photo Selected!",Toast.LENGTH_SHORT).show();
            }
        }


    }

    public File createImageFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File mFileTemp = null;
        String root = getDir("my_sub_dir", Context.MODE_PRIVATE).getAbsolutePath();
        File myDir = new File(root +"/Img");
        if(!myDir.exists()){
            myDir.mkdir();
        }
        try{
            mFileTemp = File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
        }catch (IOException e1){}
        return mFileTemp;
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
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE :
                if (checkAppPermission(permissions)) {
                    Toast.makeText(this, "승인완료",Toast.LENGTH_SHORT).show();
                    goCamera(view);
                    // 퍼미션 동의했을 때 할 일
                } else {
                    Toast.makeText(this, "사용 불가",Toast.LENGTH_SHORT).show();
                    // 퍼미션 동의하지 않았을 때 할일
                    finish();
                }
                break;
            case REQUEST_STORAGE:
                if (checkAppPermission(permissions)) {
                    Toast.makeText(this, "승인완료",Toast.LENGTH_SHORT).show();
                    goGallery(view);
                    // 퍼미션 동의했을 때 할 일
                } else {
                    Toast.makeText(this, "사용 불가",Toast.LENGTH_SHORT).show();
                    // 퍼미션 동의하지 않았을 때 할일
                    finish();
                }
                break;
        }
    }
    public void goCamera(View view) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir.getAbsolutePath()+"/my_picture.jpg");
        imagePath = image.getAbsolutePath();
        Log.d("take Photo","Photo will be saved at: "+imagePath);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(checkAppPermission(new String[]{android.Manifest.permission.CAMERA})){
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }
        else
            askPermission(new String[]{android.Manifest.permission.CAMERA},REQUEST_IMAGE_CAPTURE);

    }
    public void goGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(checkAppPermission(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE})){
            if(intent.resolveActivity(getPackageManager())!=null)
                startActivityForResult(intent,REQUEST_STORAGE);
        }
        else
            askPermission(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE);
    }

    public void saveProfilePic(View view) {
        mp.start();
        //파이어베이스 Storage 저장 (Uri 받아서 저장함)
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String fileName;
        if(type)
            fileName = "Senior/"+id+".JPG";
        else
            fileName = "Student/"+id+".JPG";
        StorageReference storageRef = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com").child("Profile/"+fileName); //올리기
        if(filePath!=null){
        storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                setResult(RESULT_OK);
                finish();
            }
        });}
    }
}
