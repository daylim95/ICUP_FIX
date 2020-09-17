package com.example.hhj73.fix;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class ChatActivity extends AppCompatActivity implements ContractAdapter.ListBtnClickListener {

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
    Uri urNumber;
    final int callRequest = 123;

    ImageView urPro;
    ContractData contractData;
    ArrayList<ContractData> contractArrayList;
    ListView contractlistView;
    ContractAdapter contractAdapter;
    MediaPlayer mp;

    final static int CONDITION = R.id.conditionBtn;
    final static int FEE = R.id.monthlyFeeBtn;
    final static int PERIOD = R.id.periodMonthBtn;
    final static int EDATE = R.id.effectiveDateBtn;
    final static int SPECIAL = R.id.extraspecialBtn;

    CheckBox finalAgreeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
    }

    public void init() {
        urPro = (ImageView)findViewById(R.id.urProfile);
        urName = (TextView)findViewById(R.id.UrName);
        databaseReference = FirebaseDatabase.getInstance().getReference("chats");
        editChat = (EditText) findViewById(R.id.chatText);
        chats = new ArrayList<>();
        // arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chats);
        chatList = (ListView) findViewById(R.id.chatList);
        mp = MediaPlayer.create(this, R.raw.x);
        // layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // chatList.setLayoutManager(layoutManager);


        final Intent intent = getIntent();

        myID = intent.getStringExtra("myID");
        // 상대방 senior
        urID = intent.getStringExtra("urID");



        // 채팅방 생성
        users = new String[2];
        users[0] = myID;
        users[1] = urID;

        room = users[0]+"+"+users[1];

        databaseReference_user = FirebaseDatabase.getInstance().getReference("users");
        databaseReference_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me = dataSnapshot.child(myID).getValue(User.class);
                you =  dataSnapshot.child(urID).getValue(User.class);
                urName.setText(you.getName()); // 이름
                TextView roomName = (TextView) findViewById(R.id.roomName);
                roomName.setText(you.getName()+" 어르신");
                urNumber = Uri.parse(you.getPhone());// 전화번호
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        chatAdapter = new ChatAdapter(getApplicationContext(), chats, myID,"ME","YOU");
        chatList.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

        databaseReference.child(room).child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                chats.add(chatData);
                chatAdapter.notifyDataSetChanged();
                // chatList.smoothScrollToPosition(chatAdapter.getItemCount() - 1); // 아래로 스크롤

                //상대 프로필
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReferenceFromUrl("gs://xylophone-house.appspot.com");
                //사진 검사
                StorageReference pathRef = storageReference.child("Profile/Senior/"+urID+".JPG");
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

                urPro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent1 = new Intent(getApplicationContext(), SeniorDetail.class);
                        intent1.putExtra("myID", myID);
                        intent1.putExtra("urID", urID);
                        intent1.putExtra("type", false);
                        startActivity(intent1);
                    }
                });

                urPro.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT>=21)
                    urPro.setClipToOutline(true);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(ChatActivity.this, "삭제됨 ㄷㄷ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReference_contract = FirebaseDatabase.getInstance().getReference("contracts");

        databaseReference_contract.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contractData = dataSnapshot.child(room).getValue(ContractData.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
            }
        });





    }

    public void submit(View view) {
        // 채팅 보내기
        String chat = editChat.getText().toString();
        String str =   chat;

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


    public void contractLayoutBtn(View view) { // 계약서 버튼

        contractArrayList = new ArrayList<>();
        contractlistView = (ListView)findViewById(R.id.detailContractList);
        finalAgreeCheck = (CheckBox)findViewById(R.id.finalAgreeCheck);

        FrameLayout  layout = (FrameLayout)findViewById(R.id.contractLayout);
        layout.setVisibility(View.VISIBLE);


        if (contractData == null){
            Toast.makeText(getApplicationContext(),"don't exist",Toast.LENGTH_SHORT).show();

            //학생이름, 어르신이름, 월세, 주소, 어르신흡연, 학생흡연, 어르신펫, 학생펫, 어르신통금, 학생통금, 어르신도움, 학생 도움
            contractData= new ContractData(me.getName(),you.getName(),you.getCost(),you.getAddress(),you.getSmoking(),me.getSmoking(),
                    you.getPet(),me.getPet(),you.getCurfew(),me.getCurfew(),you.getHelp(),me.getHelp(),you.getUnique(),me.getUnique());
            Toast.makeText(this,contractData.getStartdate(),Toast.LENGTH_SHORT).show();
            databaseReference_contract.child(room).setValue(contractData);
        }else{
            Toast.makeText(getApplicationContext(),"exist",Toast.LENGTH_SHORT).show();
        }
        contractArrayList.add(contractData);
        contractAdapter = new ContractAdapter(this,R.layout.contract_row,contractArrayList,this);
        contractlistView.setAdapter(contractAdapter);
        if(contractData.isFinalagree_s()){
            layout.setEnabled(false);
        }else{
            layout.setEnabled(true);
        }

        //계약시작날짜 - 사용자입력 수정가능
        //계약자들 - 오토 수정불가
        //계약기간 - 사용자입력 수정가능
        //주소 - 오토 수정불가
        //월세 - 오토 수정가능
        //어르신 and 학생 나누어서
        //아침, 흡연, 펫, 통금, 도움, 특이합의사항

    }

    public void backChat(View view) { // 채팅으로 돌어가기
        FrameLayout layout = (FrameLayout)findViewById(R.id.contractLayout);
        layout.setVisibility(View.GONE);
    }

    public void back(View view) { // 채팅목록으로 뒤로가기
        Intent intent = new Intent(this, StudentChatList.class);
        intent.putExtra("id",myID);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void call(View view) { // 전화걸기
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

    public void contractSubmit(View view) {

        finalAgreeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                contractData.setFinalagree_j(isChecked);
                databaseReference_contract.child(room).setValue(contractData);
            }
        });
        if(contractData.isFinalagree_j()&&contractData.isFinalagree_s()){
            // you 어르신 me 학생

            mp.start();// 소리

            databaseReference_family = FirebaseDatabase.getInstance().getReference("families");
            databaseReference_family.child(room).child("studentAgree").setValue(true);
            Toast.makeText(this, "Agree Ok", Toast.LENGTH_SHORT).show();
            databaseReference_family.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(room).child("seniorAgree").exists()){
                        Family family = new Family(me.getPhone(), you.getPhone(), me.getName(), you.getName(), me.getId(), you.getId());
                        databaseReference_family.child(room).setValue(family);
                        Toast.makeText(ChatActivity.this, "Now We are Family!", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(getApplicationContext(), MatchedMain.class);
                    intent.putExtra("myID", myID);
                    intent.putExtra("urID",urID);
                    intent.putExtra("type", false);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } else if(contractData.isFinalagree_j()&&!contractData.isFinalagree_s()){
            Toast.makeText(this,"어르신이 동의하지 않았습니다!",Toast.LENGTH_SHORT).show();
        }else if(!contractData.isFinalagree_j()){
            Toast.makeText(this,"먼저 본인이 동의해주세요!",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onListBtnClick(int position) {
        ContractData c = (ContractData)contractArrayList.get(0);
        switch (position){
            case CONDITION://흡연 등등의 조건 ok
                Toast.makeText(this,"CONDITION",Toast.LENGTH_SHORT).show();
                setConsent();
                break;
            case FEE: //월세 ok
                setFeeDialog();
                Toast.makeText(this,"FEE",Toast.LENGTH_SHORT).show();
                break;
            case PERIOD: //계약기간 월단위 ok
                Toast.makeText(this,"PERIOD",Toast.LENGTH_SHORT).show();
                setPeroidDialog();
                break;
            case EDATE: // 실효날짜 ok
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,onDateSetListener,2018,6-1,10);
                datePickerDialog.show();
                Toast.makeText(this,"EDATE",Toast.LENGTH_SHORT).show();
                break;
            case SPECIAL: //특이사항 추가 및 제거 ok
                Toast.makeText(this,"SPECIAL",Toast.LENGTH_SHORT).show();
                setSpecailDialog();
                break;
            default:
                Toast.makeText(this,"DEFAULT",Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void setConsent(){
        final boolean smoke = contractData.isSmokingConsent();
        final boolean pet = contractData.isPetConsent();
        final boolean cerfew = contractData.isCerfewConsent();
        final boolean help = contractData.isHelpConsent();

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.consent_dialog,null);
        RadioGroup rg;
        RadioButton rb;

        if(smoke){
            rg = dialogView.findViewById(R.id.smoke_rd);
            rg.getChildAt(0).setEnabled(false);
            rg.getChildAt(1).setEnabled(false);
        }else {
            rb = dialogView.findViewById(R.id.radio_smoke_unconsent);
            rb.setChecked(true);
            final EditText et = dialogView.findViewById(R.id.smoke_consent_detail);
            et.setEnabled(false);
            rg = dialogView.findViewById(R.id.smoke_rd);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
                    String isConsent = radioButton.getText().toString();
                    if(isConsent.equals("합의")){
                        et.setEnabled(true);
                    }
                    else if(isConsent.equals("미합의")){
                        et.setEnabled(false);
                    }
                }
            });
        }
        if(pet){
            rg = dialogView.findViewById(R.id.pet_rd);
            rg.getChildAt(0).setEnabled(false);
            rg.getChildAt(1).setEnabled(false);
        }else {
            rb = dialogView.findViewById(R.id.radio_pet_unconsent);
            rb.setChecked(true);
            final EditText et = dialogView.findViewById(R.id.pet_consent_detail);
            et.setEnabled(false);
            rg = dialogView.findViewById(R.id.pet_rd);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
                    String isConsent = radioButton.getText().toString();
                    if(isConsent.equals("합의")){
                        et.setEnabled(true);
                    }
                    else if(isConsent.equals("미합의")){
                        et.setEnabled(false);
                    }
                }
            });
        }
        if(cerfew){
            rg = dialogView.findViewById(R.id.cerfew_rd);
            rg.getChildAt(0).setEnabled(false);
            rg.getChildAt(1).setEnabled(false);
        }else {
            rb = dialogView.findViewById(R.id.radio_cerfew_unconsent);
            rb.setChecked(true);
            final EditText et = dialogView.findViewById(R.id.cerfew_consent_detail);
            et.setEnabled(false);
            rg = dialogView.findViewById(R.id.cerfew_rd);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
                    String isConsent = radioButton.getText().toString();
                    if(isConsent.equals("합의")){
                        et.setEnabled(true);
                    }
                    else if(isConsent.equals("미합의")){
                        et.setEnabled(false);
                    }
                }
            });
        }
        if(help){
            rg = dialogView.findViewById(R.id.help_rd);
            rg.getChildAt(0).setEnabled(false);
            rg.getChildAt(1).setEnabled(false);
        }else {
            rb = dialogView.findViewById(R.id.radio_help_unconsent);
            rb.setChecked(true);
            final EditText et = dialogView.findViewById(R.id.help_consent_detail);
            et.setEnabled(false);
            rg = dialogView.findViewById(R.id.help_rd);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
                    String isConsent = radioButton.getText().toString();
                    if(isConsent.equals("합의")){
                        et.setEnabled(true);
                    }
                    else if(isConsent.equals("미합의")){
                        et.setEnabled(false);
                    }
                }
            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("조건 수정");
        builder.setMessage("전부 합의하셔야 하고, 합의 내용은 안적어도 무관합니다.");
        builder.setView(dialogView);
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setChangedConsent(R.id.smoke_rd,R.id.smoke_consent_detail,dialogView,smoke);
                setChangedConsent(R.id.pet_rd,R.id.pet_consent_detail,dialogView,pet);
                setChangedConsent(R.id.cerfew_rd,R.id.cerfew_consent_detail,dialogView,cerfew);
                setChangedConsent(R.id.help_rd,R.id.help_consent_detail,dialogView,help);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void setChangedConsent(int rg_id, int et_id, View v, boolean b){
        if (b) {  //동의가 이미 된 상태 -> 라디오 변화 신경 쓸 필요 ㄴㄴ, 동의변화도 체크할 필요 없음, 디테일변화만
            EditText et = (EditText)v.findViewById(et_id);
            String detail = et.getText().toString();
            switch (rg_id){
                case R.id.smoke_rd:
                    contractData.setSmoke_detail(detail);
                    break;
                case R.id.pet_rd:
                    contractData.setPet_detail(detail);
                    break;
                case R.id.cerfew_rd:
                    contractData.setCerfew_detail(detail);
                    break;
                case R.id.help_rd:
                    contractData.setHelp_detail(detail);
                    break;
                default:
                    break;
            }

        }
        else{   //동의가 안된 상태 -> 라디오변화 체크 -> 동의면 동의&디테일 둘다 저장, -> 미합의면 그대로
            RadioGroup radioGroup = (RadioGroup) v.findViewById(rg_id);
            RadioButton rb = (RadioButton)v.findViewById(radioGroup.getCheckedRadioButtonId());
            if(rb.getText().toString().equals("합의")){
                EditText et = (EditText)v.findViewById(et_id);
                String detail = et.getText().toString();
                switch (rg_id){
                    case R.id.smoke_rd:
                        contractData.setSmokingConsent(true);
                        contractData.setSmoke_detail(detail);
                        break;
                    case R.id.pet_rd:
                        contractData.setPetConsent(true);
                        contractData.setPet_detail(detail);
                        break;
                    case R.id.cerfew_rd:
                        contractData.setCerfewConsent(true);
                        contractData.setCerfew_detail(detail);
                        break;
                    case R.id.help_rd:
                        contractData.setHelpConsent(true);
                        contractData.setHelp_detail(detail);
                        break;
                    default:
                        break;
                }

            }
        }
        contractAdapter.notifyDataSetChanged();
        databaseReference_contract.child(room).setValue(contractData);
    }
    private void setSpecailDialog(){

        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.consent_special_dialog,null);
        String special = contractData.getExtraspecial();
        final EditText editText = (EditText)dialogView.findViewById(R.id.special_consent_detail);
        editText.setText(special);

        final RadioGroup rg = (RadioGroup)dialogView.findViewById(R.id.special_rd);

        if(contractData.isExtraConsent()){
            rg.getChildAt(0).setEnabled(false);
            rg.getChildAt(1).setEnabled(false);
        }
        else{
            RadioButton rb = (RadioButton) dialogView.findViewById(R.id.radio_special_unconsent);
            rb.setChecked(true);
            editText.setEnabled(false);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
                    String isConsent = radioButton.getText().toString();
                    if(isConsent.equals("합의")){
                        editText.setEnabled(true);
                    }
                    else if(isConsent.equals("미합의")){
                        editText.setEnabled(false);
                    }
                }
            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("특이 사항 수정 및 합의");
        builder.setMessage("수정양식은 자유롭게 적으시면 됩니다.");
        builder.setView(dialogView);
        builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RadioButton rb = (RadioButton)dialogView.findViewById(rg.getCheckedRadioButtonId());
                if(contractData.isExtraConsent()){
                    EditText et = (EditText)dialogView.findViewById(R.id.special_consent_detail);
                    String detail = et.getText().toString();
                    contractData.setExtraspecial(detail);
                }else{
                    if(rb.getText().toString().equals("합의")){
                        EditText et = (EditText)dialogView.findViewById(R.id.special_consent_detail);
                        String detail = et.getText().toString();
                        contractData.setExtraConsent(true);
                        contractData.setExtraspecial(detail);
                    }
                }
                contractAdapter.notifyDataSetChanged();
                databaseReference_contract.child(room).setValue(contractData);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    private void  setFeeDialog(){
        final EditText feeInput = new EditText(this);
        feeInput.setGravity(Gravity.RIGHT);

        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle("월세 변경(원 단위)")
                .setView(feeInput)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),feeInput.getText(),Toast.LENGTH_SHORT).show();
                        contractData.setMonthlyfee(feeInput.getText().toString());
                        contractAdapter.notifyDataSetChanged();
                        databaseReference_contract.child(room).setValue(contractData);
                    }
                })
                .show();
    }
    private void setPeroidDialog(){
        final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(this)
                .minValue(6)
                .maxValue(60)
                .defaultValue(1)
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(Color.BLACK)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle("계약기간(월 단위)")
                .setView(numberPicker)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        contractData.setMonthperiod(numberPicker.getValue());
                        contractAdapter.notifyDataSetChanged();
                        databaseReference_contract.child(room).setValue(contractData);
                    }
                })
                .show();
    }
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            int mon = month+1;
            String tempDate = year+"년 "+mon+"월 "+dayOfMonth+"일";
            contractData.setStartdate(tempDate);
            contractAdapter.notifyDataSetChanged();
            databaseReference_contract.child(room).setValue(contractData);
        }
    };
}
