package com.example.hhj73.fix;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivitySenior extends AppCompatActivity {
    EditText editChat;
    ArrayList<ChatData> chats;
    // ArrayAdapter arrayAdapter;
    ListView chatList;
    // RecyclerView.LayoutManager layoutManager;
    ChatAdapter chatAdapter;
    String myName;
    String myID;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference_user;
    DatabaseReference databaseReference_family;
    TextView urName;
    String urID;
    String users[];
    String room;
    Uri urNumber;
    User you;
    User me;
    final int callRequest = 123;
    final int DETAIL = 234;

    ImageView urPro;
    MediaPlayer mp;

    ListView detailContractList;
    ContractData contractData;
    ContractAdapterSenior contractAdapterSenior;
    DatabaseReference databaseReference_contract;
    ArrayList<ContractData> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_senior);
        init();
    }

    public void init() {
        urPro = (ImageView)findViewById(R.id.urProfile);
        urName = (TextView)findViewById(R.id.UrName);
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        editChat = (EditText) findViewById(R.id.chatText);
        chats = new ArrayList<>();
        chatList = (ListView) findViewById(R.id.chatList);

        Intent intent = getIntent();
        myID = intent.getStringExtra("myID");

        // 상대방
        urID = intent.getStringExtra("urID");


        // 채팅방 생성
        users = new String[2];
        users[0] = urID;
        users[1] = myID;

        room = users[0]+"+"+users[1];

        databaseReference_user = FirebaseDatabase.getInstance().getReference("users");
        databaseReference_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me =  dataSnapshot.child(myID).getValue(User.class);
                you =  dataSnapshot.child(urID).getValue(User.class);
                urName.setText(you.getName()); // 이름
                TextView roomName = (TextView) findViewById(R.id.roomName);
                roomName.setText(you.getName()+" 학생");
                urNumber = Uri.parse(you.getPhone());// 전화번호

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        chatAdapter = new ChatAdapter(getApplicationContext(), chats, myID, "ME", "YOU");
        chatList.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

        databaseReference.child(room).child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ChatData chatData = dataSnapshot.getValue(ChatData.class);

                chats.add(chatData);
                chatAdapter.notifyDataSetChanged();

                //상대 프로필
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com");
                //사진 검사
                StorageReference pathRef = storageReference.child("Profile/Student/"+urID+".JPG");
                pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//있음
                    @Override
                    public void onSuccess(Uri uri) {//있음
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .centerCrop()
                                .into(urPro);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                urPro.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT>=21)
                    urPro.setClipToOutline(true);

                urPro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent1 = new Intent(getApplicationContext(), StudentDetail.class);
                        intent1.putExtra("myID", myID);
                        intent1.putExtra("urID", urID);
                        startActivityForResult(intent1, DETAIL);
                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(), "삭제됨 ㄷㄷ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference_contract = FirebaseDatabase.getInstance().getReference("contracts");
        DatabaseReference db = databaseReference_contract.child(room);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contractData = dataSnapshot.getValue(ContractData.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void submit(View view) {
        // 채팅 보내기
        String chat = editChat.getText().toString();
        String str =  chat;

//        chats.add(str);
//        arrayAdapter.notifyDataSetChanged();

        editChat.setText("");

        // 내가 보낸 메시지 DB에 저장
        // 시간
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("MM-dd hh:mm");
        String timeStr = dayTime.format(new Date(time));

        ChatData chatData = new ChatData(myID, str, timeStr);
        databaseReference.child(room).child("chat").push().setValue(chatData);
    }


    public void contractLayoutBtn(View view) { //계약서 버튼
        FrameLayout layout = (FrameLayout)findViewById(R.id.contractLayout);
        layout.setVisibility(View.VISIBLE);
        arrayList = new ArrayList<>();
        arrayList.add(contractData);
        detailContractList = (ListView)findViewById(R.id.detailContractList);
        contractAdapterSenior = new ContractAdapterSenior(this,R.layout.contract_row_senior,arrayList);
        detailContractList.setAdapter(contractAdapterSenior);

    }

    public void backChat(View view) { //채팅으로 돌어가기
        FrameLayout layout = (FrameLayout)findViewById(R.id.contractLayout);
        layout.setVisibility(View.GONE);
    }

    public void back(View view) { //채팅목록으로 뒤로가기
        Intent intent = new Intent(this, SeniorChatList.class);
        intent.putExtra("id",myID);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void call(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+urNumber));
        if(checkAppPermission(new String[]{android.Manifest.permission.CALL_PHONE}))//call phone 체크
            startActivity(callIntent);
        else
            askPermission(new String[]{android.Manifest.permission.CALL_PHONE}, callRequest);//요구

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
            case callRequest :
                if (checkAppPermission(permissions)) {
                    Toast.makeText(this, "승인완료",Toast.LENGTH_SHORT).show();
                    // 퍼미션 동의했을 때 할 일
                } else {
                    Toast.makeText(this, "사용 불가",Toast.LENGTH_SHORT).show();
                    // 퍼미션 동의하지 않았을 때 할일
                    finish();
                }
                break;
        }
    }


    public void contractSubmit(View view) { // 계약서 제출
        // you 학생 me 어르신


        CheckBox finalAgreeCheck = (CheckBox)findViewById(R.id.finalAgreeCheck_s);
        if(finalAgreeCheck.isChecked()){
            contractData.setFinalagree_s(true);
        }else{
            contractData.setFinalagree_s(false);
        }
        databaseReference_contract.child(room).setValue(contractData);
        /*finalAgreeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                contractData.setFinalagree_s(isChecked);
                databaseReference_contract.child(room).setValue(contractData);
            }
        });*/

        if(contractData.isFinalagree_j()) {
            Toast.makeText(this, "최종 동의하셨습니다. 학생에게 제출을 요청하세요.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "동의 체크박스를 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
        databaseReference_family = FirebaseDatabase.getInstance().getReference("families");
        databaseReference_family.child(room).child("seniorAgree").setValue(true);
        Toast.makeText(this, "Agree Ok", Toast.LENGTH_SHORT).show();
        mp = MediaPlayer.create(this, R.raw.x);
        mp.start();// 소리
        /*
        databaseReference_family.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(room).child("studentAgree").exists()){
                    mp.start();// 소리
                    Family family = new Family(you.getPhone(), me.getPhone(), you.getName(), me.getName(), you.getId(), me.getId());
                    databaseReference_family.child(room).setValue(family);
                    Toast.makeText(ChatActivitySenior.this, "Now We are Family!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });*/
    }
}
