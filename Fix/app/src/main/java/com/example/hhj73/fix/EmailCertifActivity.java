package com.example.hhj73.fix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;


public class EmailCertifActivity extends Activity {
    Properties props;
    Session mailSession;
    MimeMessage message;
    EditText editText;
    String certificationNum;
    String client_email;
    int TIMELIMIT = 180;
    private CountDownTimer countDownTimer;
    private static final int MILLISINFUTURE = 181*1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    TextView timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_email_certif);

        Intent intent = getIntent();
        client_email = intent.getStringExtra("client_email");

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
               .permitDiskReads().permitDiskWrites().permitNetwork().build());
        sendEmail(client_email);


        timer = (TextView)findViewById(R.id.timer);
        countDownTimer();
        countDownTimer.start();

    }
    public void countDownTimer(){
        countDownTimer = new CountDownTimer(MILLISINFUTURE,COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(TIMELIMIT/60+"분 "+TIMELIMIT%60+"초");
                TIMELIMIT--;
            }

            @Override
            public void onFinish() {
                timer.setText(TIMELIMIT/60+"분 "+TIMELIMIT%60+"초");
                Button certificationBtn = (Button)findViewById(R.id.certificationBtn);
                Button resendBtn = (Button)findViewById(R.id.resendBtn);
                editText = (EditText)findViewById(R.id.certificationNum);

                certificationBtn.setEnabled(false);
                resendBtn.setEnabled(false);
                editText.setEnabled(false);
                Toast.makeText(getApplicationContext(),"[시간만료]\n다시 인증해주세요.",Toast.LENGTH_SHORT).show();

            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            countDownTimer.cancel();
        } catch (Exception e){}
        countDownTimer=null;
    }

    public void sendEmail(String client_email){
        String host = "smtp.gmail.com";
        String subject = "F.I.X 가입 인증번호 전달";
        String fromName = "F.I.X 관리자";
        String from = "fam.in.xy@gmail.com";
        String to1 = client_email;
        String content = "인증번호 ["+makeSerialNum()+"]<br>알맞게 기입해주세요.";

        props = new Properties();
        props.put("mail.transport.protocol", "smtp");       //프로토콜 설정
        props.put("mail.host",host);                          //gmail SMTP 서비스 주소(호스트)
        props.put("mail.smtp.port", "465");                  //gmail SMTP 서비스 포트 설정
        props.put("mail.smtp.starttls.enable", "true");
        //gmail 인증용 Secure Socket Layer(SSL) 설정
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.user",from);
        props.put("mail.smtp.auth", "true");              //SMTP 인증 설정

        javax.mail.Authenticator auth = new SMTPAuthenticator();
        mailSession = Session.getDefaultInstance(props, auth);

        message = new MimeMessage(mailSession);

        try{
            message.setFrom(new InternetAddress(from, MimeUtility.encodeText(fromName,"UTF-8","B")));
            InternetAddress[] address1 = {new InternetAddress(to1)};
            message.setRecipients(Message.RecipientType.TO, address1);
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            message.setSentDate(new Date());

            Transport.send(message);
        }
        catch (MessagingException e){
            System.out.println("Something Wrong!");
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public String makeSerialNum(){
        Random rnd = new Random();
        certificationNum = rnd.nextInt(8999)+1000+"";
        return certificationNum;
    }

    public void certification(View view) {
        editText = (EditText)findViewById(R.id.certificationNum);
        String userInput = editText.getText().toString();
        if(userInput.equals(certificationNum)){
            Toast.makeText(this,"OK",Toast.LENGTH_SHORT).show();
            Intent intent = getIntent();
            intent.putExtra("mail_certification",true);
            setResult(RESULT_OK,intent);

            finish();
        }else{
            Toast.makeText(this,"Wrong Number. Check Again!",Toast.LENGTH_SHORT).show();
        }
    }

    public void resend(View view) {
        sendEmail(client_email);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }
}
