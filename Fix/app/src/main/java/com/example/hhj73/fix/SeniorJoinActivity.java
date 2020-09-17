package com.example.hhj73.fix;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by hhj73 on 2018-04-09.
 */

public class SeniorJoinActivity extends Activity {
    LinearLayout zero;
    LinearLayout first;
    LinearLayout second;
    LinearLayout third;
    LinearLayout forth;
    LinearLayout fifth;
    LinearLayout sixth;
    LinearLayout seventh;
    DatabaseReference databaseReference;
    String id;
    String pw;
    String name;
    String bday;
    String phoneNumber;
    boolean gender;
    boolean smoking;
    boolean curfew;
    boolean pet;
    boolean help;
    String uniqueness;
    String strAddress;
    String cost;
    String location;
    EditText joinID;
    EditText pw1;
    EditText pw2;
    EditText add2;
    RadioGroup genderGroup;
    RadioButton femaleBtn;
    RadioButton maleBtn;
    EditText EditBday;
    EditText EditName;
    EditText EditPhone;
    EditText EditCost;
    EditText EditUnique;
    EditText EditAddress1, EditAddress2;
    CheckBox helpCheck;
    CheckBox petCheck;
    CheckBox curfewCheck;
    CheckBox smokeCheck;

    MediaPlayer mp;
    Button next2Btn;

    final int REQUEST_IMAGE_CAPTURE= 123;
    final int REQUEST_STORAGE=234;

    private Uri filePath;

    String strt;        //xml 중 address2 에 들어가는 string
    boolean addcount=false;   //화면 다시 시작한 위치
    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_senior);

        init();
    }

    public void init() {
        mp = MediaPlayer.create(this, R.raw.dding);
        joinID = (EditText) findViewById(R.id.JoinID);
        pw1 = (EditText) findViewById(R.id.pw1);
        pw2 = (EditText) findViewById(R.id.pw2);

        genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        femaleBtn = (RadioButton) findViewById(R.id.femaleButton);
        maleBtn = (RadioButton) findViewById(R.id.maleButton);
        EditBday = (EditText) findViewById(R.id.BDay);
        EditName = (EditText) findViewById(R.id.name);
        EditPhone = (EditText) findViewById(R.id.phoneNum);
        EditCost = (EditText) findViewById(R.id.cost);
        helpCheck = (CheckBox) findViewById(R.id.helpCheck);
        petCheck = (CheckBox) findViewById(R.id.petCheck);
        curfewCheck = (CheckBox) findViewById(R.id.curfewCheck);
        smokeCheck = (CheckBox) findViewById(R.id.smokeCheck);
        EditUnique = (EditText) findViewById(R.id.uniqueness);
        EditAddress1 = (EditText) findViewById(R.id.address);
        EditAddress2 = (EditText) findViewById(R.id.address2);

        Intent intent = getIntent();
        strt = intent.getStringExtra("ADDRESS");
        addcount=intent.getBooleanExtra("ADDCOUNT",false);
        phoneNumber =intent.getStringExtra("number");
        bday =intent.getStringExtra("date");
        name=intent.getStringExtra("name");
        id =intent.getStringExtra("id");
        pw =intent.getStringExtra("pw1");
        pw2.setText(intent.getStringExtra("pw2"));
        location = intent.getStringExtra("location");
        if(intent.getBooleanExtra("gender",true)==true)
            gender = true;
        else
            gender = false;


        zero = (LinearLayout)findViewById(R.id.zero);
        first = (LinearLayout)findViewById(R.id.first);
        second = (LinearLayout)findViewById(R.id.second);
        third = (LinearLayout)findViewById(R.id.third);
        forth = (LinearLayout)findViewById(R.id.forth);
        fifth = (LinearLayout)findViewById(R.id.fifth);
        sixth = (LinearLayout)findViewById(R.id.sixth);
        seventh = (LinearLayout)findViewById(R.id.seventh);

        TextView agree = (TextView)findViewById(R.id.agreeOk);
        agree.setMovementMethod(new ScrollingMovementMethod());

        databaseReference = FirebaseDatabase.getInstance().getReference("users");




        if(addcount==true){
            zero.setVisibility(view.GONE);
            forth.setVisibility(view.VISIBLE);
            add2= (EditText)findViewById(R.id.address);
            add2.setText(strt);
            // Toast.makeText(this, strt, Toast.LENGTH_SHORT).show();   //test intent values
            addcount=false;
        }
    }

    public void seniorJoinSuccess(View view) {
        // 노인회원 가입 완료
        mp.start();
        User user = new User(true, id, pw, name, bday, gender, phoneNumber, strAddress, cost, smoking, curfew, pet, help, uniqueness,location, "");
        databaseReference.child(id).setValue(user);

        //파이어베이스 Storage 저장 (Uri 받아서 저장함)
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String fileName = id+"";
        StorageReference storageRef = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com")
                .child("IDcard/Senior/"+fileName); //올리기
        storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SeniorJoinActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
            }
        });

//        databaseReference.child(id).child("type").setValue(true); // 노인 회원임
//        databaseReference.child(id).child("pw").setValue(pw); // 비밀번호
//        databaseReference.child(id).child("name").setValue(name); // 이름
//        databaseReference.child(id).child("bday").setValue(bday); // 생일
//
//        databaseReference.child(id).child("gender").setValue(str); // 성별
//        databaseReference.child(id).child("phone").setValue(phoneNumber); // 연락처
//        databaseReference.child(id).child("address").setValue(strAddress); // 주소
//        databaseReference.child(id).child("cost").setValue(cost); // 월세
//        databaseReference.child(id).child("smoking").setValue(smoking); // 흡연
//        databaseReference.child(id).child("curfew").setValue(curfew); // 통금
//        databaseReference.child(id).child("pet").setValue(pet); // 반려동물
//        databaseReference.child(id).child("help").setValue(help); // 도움
//        databaseReference.child(id).child("unique").setValue(uniqueness); // 특이사항

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent); //액티비티 이동
        overridePendingTransition(0, 0);
        Toast.makeText(this, "Join success.",Toast.LENGTH_SHORT).show();
    }


    public void next(View view) { //다음버튼
            mp.start();
            switch (view.getId()) {
                case R.id.next0:
                    CheckBox agree = (CheckBox) findViewById(R.id.agree);
                    if (agree.isChecked()) {
                        zero.setVisibility(view.GONE);
                        first.setVisibility(view.VISIBLE);
                    } else
                        Toast.makeText(this, "동의하셔야 가입이 가능합니다", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.next1:
                    if(checkPW()) {
                        first.setVisibility(view.GONE);
                        second.setVisibility(view.VISIBLE);
                    }
                    break;
                case R.id.next2:

                    setName_bday_gender();
                    second.setVisibility(view.GONE);
                    third.setVisibility(view.VISIBLE);
                    break;

                case R.id.next3:
                    setPhoneNumber();
                    third.setVisibility(view.GONE);
                    forth.setVisibility(view.VISIBLE);
                    break;

                case R.id.next4:
                    setAddress_cost();
                    forth.setVisibility(view.GONE);
                    fifth.setVisibility(view.VISIBLE);
                    break;
                case R.id.next5:
                    // 사진 저장
                    fifth.setVisibility(view.GONE);
                    sixth.setVisibility(view.VISIBLE);
                    break;
                case R.id.next6:
                    setSpecial();
                    sixth.setVisibility(view.GONE);
                    seventh.setVisibility(view.VISIBLE);
                    break;
                case R.id.next7:
                    setUniqueness();
                    seventh.setVisibility(view.GONE);
                    break;
            }

    }

    public void IDcheck(View view) { //아이디 중복확인

        // 아이디 가져오기
        id = joinID.getText().toString();

        // 중복 확인
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while(child.hasNext()) {
                    if(child.next().getKey().equals(id)) {
                        Toast.makeText(SeniorJoinActivity.this, "중복되는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        joinID.setText("");
                        id = null;
                        databaseReference.removeEventListener(this);
                        return;
                    }
                }
                joinID.setEnabled(false);
                Toast.makeText(SeniorJoinActivity.this, "사용할 수 있는 ID입니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public boolean checkPW() {
        // 두번 쓴 비밀번호 가져옴
        String str1 = pw1.getText().toString();
        String str2 = pw2.getText().toString();

        // 비밀번호 동일 여부 확인
        if(str1.equals(str2)) {
            // 동일하다
            pw = str1;
            Toast.makeText(this, "비밀번호 설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            // 틀렸다
            Toast.makeText(this, "비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void setName_bday_gender() {
        // 이름 받아옴
        name = EditName.getText().toString();

        // 생년월일 받아옴
        bday = EditBday.getText().toString();

        // 성별 받아옴
        if(femaleBtn.isChecked())
            gender = true;
        else if(maleBtn.isChecked())
            gender = false;


        Toast.makeText(this, name+", "+bday+", "+gender, Toast.LENGTH_SHORT).show();

    }

    public void setPhoneNumber() {
        phoneNumber = EditPhone.getText().toString();
        Toast.makeText(this, phoneNumber, Toast.LENGTH_SHORT).show();
    }

    public void setAddress_cost() {
        // 주소 넣는 구문
       String string1 = EditAddress1.getText().toString();
       String string2 = EditAddress2.getText().toString();
       strAddress = string1 + " " + string2;

        // 가격 받아옴
        cost = EditCost.getText().toString();
        Toast.makeText(this, cost, Toast.LENGTH_SHORT).show();
    }

    public void setSpecial() {
        if(smokeCheck.isChecked())
            smoking = true;
        else
            smoking = false;

        if(curfewCheck.isChecked())
            curfew = true;
        else
            curfew = false;

        if(petCheck.isChecked())
            pet = true;
        else
            pet = false;

        if(helpCheck.isChecked())
            help = true;
        else
            help = false;
    }

    public void setUniqueness() {
        uniqueness = EditUnique.getText().toString();
    }

    public void search(View view) { //주소 API 검색

       // Toast.makeText(this, "주소 불러왔음",Toast.LENGTH_SHORT).show();      //delete
        Intent address = new Intent(this,Address.class);
        address.putExtra("activity",true);
        address.putExtra("name",EditName.getText().toString() );
        address.putExtra("number",EditPhone.getText().toString());
        address.putExtra("id",joinID.getText().toString());
        address.putExtra("pw1", pw1.getText().toString());
        address.putExtra("pw2", pw2.getText().toString());
        address.putExtra("date",EditBday.getText().toString());
        address.putExtra("activity",false);
        if(femaleBtn.isChecked())
            gender = true;
        else if(maleBtn.isChecked())
            gender = false;
        address.putExtra("gender",gender);
        startActivity(address);

    }

    public void takePhoto(View view) { //사진찍어 신분증 인증
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(checkAppPermission(new String[]{android.Manifest.permission.CAMERA})){
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
        }
        else
            askPermission(new String[]{android.Manifest.permission.CAMERA},REQUEST_IMAGE_CAPTURE);
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
            case REQUEST_IMAGE_CAPTURE :
                if (checkAppPermission(permissions)) {
                    Toast.makeText(this, "승인완료",Toast.LENGTH_SHORT).show();
                    takePhoto(view);
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
                    loadPhote(view);
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
      if(requestCode==REQUEST_IMAGE_CAPTURE){
          Bundle extras = data.getExtras();
          Bitmap imageBitmap = (Bitmap)extras.get("data");
          Matrix matrix = new Matrix();
          matrix.postRotate(90); //90도 회전
          Bitmap.createBitmap(imageBitmap, 0,0,
                  imageBitmap.getWidth(),imageBitmap.getHeight(),matrix,true);
        ((ImageView)findViewById(R.id.imageview)).setImageBitmap(imageBitmap);
          File file = createImageFile();
          if(file!=null){
              FileOutputStream fout;
              try{
                  fout = new FileOutputStream(file);
                  imageBitmap.compress(Bitmap.CompressFormat.PNG,70,fout);
                  fout.flush();
              }catch (Exception e){}
              filePath = Uri.fromFile(file);
          }
      }else if(requestCode == REQUEST_STORAGE && resultCode==RESULT_OK){
          try{
              filePath = data.getData();
          Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
              Matrix matrix = new Matrix();
              matrix.postRotate(90); //90도 회전
              Bitmap.createBitmap(image, 0,0,
                      image.getWidth(),image.getHeight(),matrix,true);
          ((ImageView)findViewById(R.id.imageview)).setImageBitmap(image);
          }
          catch (IOException ex){}
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

    public void loadPhote(View view) { //앨범에서 신분증 가져오기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(checkAppPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})){
            if(intent.resolveActivity(getPackageManager())!=null)
                startActivityForResult(intent,REQUEST_STORAGE);
        }
        else
            askPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE);
    }


}
