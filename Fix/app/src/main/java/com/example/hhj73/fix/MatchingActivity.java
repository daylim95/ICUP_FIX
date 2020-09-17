package com.example.hhj73.fix;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class MatchingActivity extends AppCompatActivity {

    ListView matchList;
    ArrayList<User> users;
    // ArrayAdapter arrayAdapter;
    MatchingAdapter matchingAdapter;
    DatabaseReference databaseReference;
    String curUser;
    final int FILTER = 123;
    final int DETAIL = 234;

    double limit;//거리제한
    float pet;
    float help;
    float smoke;
    float curfew;
    double lot, lat;
    int minCost, maxCost;

    MediaPlayer mp;
    HashMap<String, Double> score;
    HashMap<String, String> scoreData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        init();
    }

    public void init() {
        mp = MediaPlayer.create(this, R.raw.dding);

        Intent intent = getIntent();
        curUser = intent.getStringExtra("curUser");
        matchList = (ListView) findViewById(R.id.matchList);
        users = new ArrayList<>();
//        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, users);
//        matchList.setAdapter(arrayAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        score = new HashMap<>();
        scoreData = new HashMap<>();
        matchingAdapter = new MatchingAdapter(getApplicationContext(), users);
        matchList.setAdapter(matchingAdapter);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                User myInfo = dataSnapshot.child(curUser).getValue(User.class); // 내 정보
//                String Mgender = dataSnapshot.child(curUser).child("gender").getValue().toString(); // 내 성별
//                String Sgender; // 어르신 성별

                boolean myGender = myInfo.getGender();
                boolean Sgender;

                while(child.hasNext()) {
                    String id = child.next().getKey();
                    User senior = dataSnapshot.child(id).getValue(User.class);
                    // Sgender = dataSnapshot.child(id).child("gender").getValue().toString();
                    Sgender = senior.getGender(); // 어르신 성별
                    boolean type = senior.getType();

                    if(myGender == Sgender && type) { // 나랑 성별 같은 어르신
                        users.add(senior);
                        matchingAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        matchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User)(adapterView.getItemAtPosition(i));

                Intent intent = new Intent(getApplicationContext(), SeniorDetail.class);
                intent.putExtra("myID", curUser);
                intent.putExtra("urID", user.getId());
                intent.putExtra("type", false);
                startActivityForResult(intent, DETAIL);
                overridePendingTransition(0, 0);
            }
        });

    }
    public void profile(View view) { //프로필
        Intent intent = new Intent(this, StudentEditProfile.class);
        intent.putExtra("id", curUser);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void ChatList(View view) {
        Intent intent = new Intent(this, StudentChatList.class);
        intent.putExtra("id",curUser);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void filter(View view) {//필터

        mp.start();// 소리
        Intent intent = new Intent(this,Filter.class);
        startActivityForResult(intent,FILTER);
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        if(requestCode == FILTER && resultCode == RESULT_OK) { //데이터값 입력받을때 예외처리 필요.
            init();
            lot = data.getDoubleExtra("lot", 0);// 기준점 위치
            lat = data.getDoubleExtra("lat", 0);
            limit = data.getDoubleExtra("miter", Double.MAX_VALUE);//미터제한

            pet = data.getFloatExtra("pet", 0); //생활 속성
            help = data.getFloatExtra("help", 0);
            smoke = data.getFloatExtra("smoke", 0);
            curfew = data.getFloatExtra("curfew", 0);

            minCost = data.getIntExtra("minCost", 0); // 가격
            maxCost = data.getIntExtra("maxCost", -1);

            //======================================================▼구현해 주세요^^▼===============================================================//


            //단계별로 점수를 계산을 한 다음 최종 점수로 순서를 가린다.
            // 총점 100점중 거리 40, 생활 30, 가격 30 으로 나눠진다.

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    double DminScore = Double.MAX_VALUE; // 거리 최소
                    double AmaxScore = Double.MIN_VALUE; // 속성점수 최대
                    double CmaxScore = Double.MIN_VALUE; // 가격 최대

                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                    for (int i = 0; i < users.size(); i++) {
                        // User user = dataSnapshot.child(users.get(i)).getValue(User.class);
                        User user = dataSnapshot.child(users.get(i).getId()).getValue(User.class);
                        // 거리 계산
                        // 거리최소 DminScore, 거리 저장 D_score
                        double D_score = 0;
                        if (lot != 0 && lat != 0) {
                            String str = user.getLocation();
                            StringTokenizer stringTokenizer = new StringTokenizer(str, "/");
                            float _lat = Float.parseFloat(stringTokenizer.nextToken());
                            float _lot = Float.parseFloat(stringTokenizer.nextToken());

                            Location locationA = new Location("point A");
                            locationA.setLatitude(lat);
                            locationA.setLongitude(lot);

                            Location locationB = new Location("point B");
                            locationB.setLatitude(_lat);
                            locationB.setLongitude(_lot);

                             double distance = locationA.distanceTo(locationB);

                            D_score = distance / 300 * (-5); // 거리 점수 저장
                            if (distance > limit)
                                D_score = -1; // 범위 넘어갈 때 -1
                            else {
                                if (D_score < DminScore) { // 거리 최소 저장됨
                                    DminScore = D_score;
                                }
                            }
                        }
                        // 속성 계산
                        // 속성 최대 AmaxScore 속성점수 저장 A_score

                        double A_score = 0; // 속성점수 저장
                        User me = dataSnapshot.child(curUser).getValue(User.class);
                        if (curfew != 0) {
                            if (me.getCurfew() == user.getCurfew()) {
                                A_score += same(curfew);
                            } else {
                                A_score += diff(curfew);
                            }
                        }
                        if (help != 0) {
                            if (me.getHelp() == user.getHelp()) {
                                A_score += same(help);
                            } else {
                                A_score += diff(help);
                            }
                        }
                        if (pet != 0) {
                            if (me.getPet() == user.getPet()) {
                                A_score += same(pet);
                            } else {
                                A_score += diff(pet);
                            }
                        }
                        if (smoke != 0) {
                            if (me.getSmoking() == user.getSmoking()) {
                                A_score += same(smoke);
                            } else {
                                A_score += diff(smoke);
                            }
                        }

                        if (AmaxScore < A_score) // 속성 최고저장.
                            AmaxScore = A_score;

                        // 가격 계산
                        // 가격 최대 CmaxScore , 가격 저장 C_score

                        double userCost = Double.parseDouble(user.cost);
                        double C_score = 0;

                        C_score = (userCost / 1000);// 1000원 단워로 점수화
                        if (CmaxScore < C_score) // 속성 최고저장.
                            CmaxScore = C_score;

                        if (maxCost != -1 || minCost != 0) {
                            if (userCost >= minCost && userCost <= maxCost)
                                C_score = C_score / 2; // 범위안에 들어가면 반으로
                            else if (maxCost == -1 && userCost >= minCost) {
                                C_score = C_score / 2;
                            }else if(minCost == 0 && userCost <= maxCost){
                                C_score = C_score / 2;
                            }
                            else { // 범위에 안들어감
                                C_score = -1;
                            }
                        }

                        scoreData.put(users.get(i).getId(), D_score + "/" + A_score + "/" + C_score);//총 데이터 저장
                    }
                    ArrayList keySet = new ArrayList(scoreData.keySet());

                    for (int i = 0; i < scoreData.size(); i++) { // 최종점수 계산
                        String str = scoreData.get(keySet.get(i));
                        StringTokenizer stringTokenizer = new StringTokenizer(str, "/");
                        double D_s = Double.parseDouble(stringTokenizer.nextToken()); // 거리 점수
                        double A_s = Double.parseDouble(stringTokenizer.nextToken()); // 속성 점수
                        double C_s = Double.parseDouble(stringTokenizer.nextToken()); // 가격 점수

                        double DmaxScore = DminScore * (-1);
                        if (D_s != -1) {// 선택했을 떄만
                            D_s = ((D_s + DmaxScore) / DmaxScore) * 40; // 거리점수 계산완료
                        } else
                            continue;

                        A_s = A_s / AmaxScore * 30; // 속성점수 계산완료

                        if (C_s != -1) {// 범위안에 있을 때만
                            C_s = (Math.abs((C_s - CmaxScore)) / CmaxScore) * 30;
                            score.put((String) keySet.get(i), D_s + A_s + C_s); //총점 100점 점수 넣기
                        }
                    }
                    //정렬후 출력 해야하는데.
                    users.clear();
                    Iterator it = sortByValue(score).iterator();
                    while(it.hasNext()) {
                        String temp = (String) it.next();
                        User u = dataSnapshot.child(temp).getValue(User.class);
                        // users.add(temp + " = " + score.get(temp));
                        users.add(u);
                    }
                    matchingAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public List sortByValue(final Map map) {

        List<String> list = new ArrayList();

        list.addAll(map.keySet());

        Collections.sort(list,new Comparator() {

            public int compare(Object o1,Object o2) {

                Object v1 = map.get(o1);

                Object v2 = map.get(o2);

                return ((Comparable) v2).compareTo(v1);
            }
        });
        //Collections.reverse(list); // 주석시 오름차순
        return list;
    }

    public float same(float imp){ //속성 같을때
        return (Math.abs(3-imp) + 1) * (4f);
    }
    public float diff(float imp){ //속성 다를때
        return (Math.abs(5 - imp)) * (1.5f);
    }
}
