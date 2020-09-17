package com.example.hhj73.fix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ChatAfterMatchedActivity extends AppCompatActivity {

    EditText editChat;
    TextView urName;

    ArrayList<ChatData> chats;
    ListView chatList;
    ChatAdapter chatAdapter;
    String myName;
    String myID;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference_user;
    DatabaseReference databaseReference_family;
    DatabaseReference databaseReference_contract;
    String urID;
    String users[];
    String room;
    User you;
    User me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_after_matched);

        init();
    }

    public void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        editChat = (EditText) findViewById(R.id.chatText);
        chats = new ArrayList<>();
        chatList = (ListView) findViewById(R.id.chatList);

        final Intent intent = getIntent();

        myID = intent.getStringExtra("myID");
        // 상대방 senior
        urID = intent.getStringExtra("urID");


        //chatAdapter = new ChatAdapter(getApplicationContext(), chats, myID);

        //type = intent.getBooleanExtra("type", true);
        chatAdapter = new ChatAdapter(getApplicationContext(), chats, myID, "ME", "YOU");

        chatList.setAdapter(chatAdapter);

        // 채팅방 생성
        users = new String[2];
        users[0] = myID;
        users[1] = urID;

        Arrays.sort(users);

        room = users[0]+"+"+users[1];

        databaseReference.child(room).child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ChatData chatData = dataSnapshot.getValue(ChatData.class);

                chats.add(chatData);
                chatAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // 방 제목
        ((TextView) findViewById(R.id.roomName)).setText(urID+"님");
    }


    public void submit(View view) {
        // 채팅 보내기
        String chat = editChat.getText().toString();
        String str = chat;

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
}
