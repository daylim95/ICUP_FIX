package com.example.hhj73.fix;

/**
 * Created by skrud on 2018-04-16.
 */

public class SMTPAuthenticator extends javax.mail.Authenticator {
    protected javax.mail.PasswordAuthentication getPasswordAuthentication(){
        String username = "fam.in.xy@gmail.com";
        String password = "fixpassword";
        return new javax.mail.PasswordAuthentication(username,password);
    }
}
