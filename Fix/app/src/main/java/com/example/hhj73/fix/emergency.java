package com.example.hhj73.fix;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class emergency extends Activity {
String urNumber;
final int callRequest = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_emergency);
        Intent intent = getIntent();
        urNumber = intent.getStringExtra("urPhone");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    public void call_partner(View view) {
        // urNumber 으로 전화
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+urNumber));
        if(checkAppPermission(new String[]{android.Manifest.permission.CALL_PHONE}))//call phone 체크
            startActivity(callIntent);
        else
            askPermission(new String[]{android.Manifest.permission.CALL_PHONE}, callRequest);//요구
    }

    public void call_119(View view) {
        // 119로 전화
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+119));
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

}
