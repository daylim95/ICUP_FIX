package com.example.hhj73.fix;

import android.content.Intent;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Address extends AppCompatActivity {

    WebView webView;
    TextView result;
    Handler handler;
    String confirm;

    String ID;
    String Pw1;
    String Pw2;
    String Number;
    Boolean Gender;
    String Name;
    String Date;

    TextView tv ;
    Boolean activity;

    String  lnglataddress;
    double  Longitude;
    double  Latitude;
    MediaPlayer mp;
    Boolean type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        mp = MediaPlayer.create(this, R.raw.dding);
        Intent intent = getIntent();
        type = intent.getBooleanExtra("type", false);
       activity = intent.getBooleanExtra("activity",true);
        if(activity==true){
            ID = intent.getStringExtra("id");
            Pw1 = intent.getStringExtra("pw1");
            Pw2 = intent.getStringExtra("pw2");
            Number = intent.getStringExtra("number");
            Gender = intent.getBooleanExtra("gender",false);
            Name = intent.getStringExtra("name");
        }
        else{
            ID = intent.getStringExtra("id");
            Pw1 = intent.getStringExtra("pw1");
            Pw2 = intent.getStringExtra("pw2");
            Number = intent.getStringExtra("number");
            Gender = intent.getBooleanExtra("gender",false);
            Name = intent.getStringExtra("name");
            Date = intent.getStringExtra("date");
        }
        result = (TextView) findViewById(R.id.result);

        // WebView 초기화
        init_webView();

        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
    }

    public void init_webView() {
        // WebView 설정
        webView = (WebView) findViewById(R.id.webview);
        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        // 두 번째 파라미터는 사용될 php에도 동일하게 사용해야함
        webView.addJavascriptInterface(new AndroidBridge(), "TestApp");
        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());

//----------------------------------------------------------------------------------------------

//        webView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//               view.loadUrl(url);
//               return true;
//            }
//        });

        //--------------------------------------------------------------------------------------
        //webView.loadUrl("http://kuxzl.entd21u.my03w.com/addtest.php");    //php환경
        webView.loadUrl("http://kuxzl.oicp.io:31717/add.html");             //hosting 31717  ,host 80 금지
    }

    public void addclick(View view) {
        mp.start();
        Intent add;
        if(activity==false)
        { add = new Intent(this,SeniorJoinActivity.class);
            add.setClass(this,SeniorJoinActivity.class);
            add.putExtra("date",Date);
        }
        else
        {add = new Intent(this,StudentJoinActivity.class);
            add.setClass(this,StudentJoinActivity.class);
        }
        add.putExtra("id",ID);
        add.putExtra("pw1",Pw1);
        add.putExtra("pw2",Pw2);
        add.putExtra("name",Name);
        add.putExtra("number",Number);
        add.putExtra("gender",Gender);
        add.putExtra("ADDRESS",confirm);
        add.putExtra("ADDCOUNT",true);

        Geocoder geocoder = new Geocoder(this);
        try{
            List<android.location.Address> list = geocoder.getFromLocationName(lnglataddress, 5);
            Latitude = list.get(0).getLatitude();
            Longitude = list.get(0).getLongitude();
        }catch(Exception e){
        }

        String straddress = "lat :"+Latitude+"lng:"+Longitude;
        Toast.makeText(this,straddress,Toast.LENGTH_SHORT).show();     //나중에 없애기

        //Toast.makeText(this, confirm, Toast.LENGTH_SHORT).show();      //리턴 값을 테스트
        add.putExtra("location",Latitude+"/"+Longitude);

        if(type)
            finish();
        else
            startActivity(add);
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                    init_webView();   //초기화 안하는 경우 이상해짐 !

                    confirm=("("+ arg1+")"+ arg2+ arg3);  // String 전송

                    lnglataddress=arg2;//경도 위도 검색 할때 쓰는 주소
                }
            });
        }
    }
}
