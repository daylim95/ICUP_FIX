package com.example.hhj73.fix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class ContractMatchedActivity extends AppCompatActivity {


    private DatabaseReference databaseReference_contract;
    private ContractData contractData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_matched);
        init();
    }
    public void init(){
        Intent intent = getIntent();
        final String room = intent.getStringExtra("room");

        databaseReference_contract = FirebaseDatabase.getInstance().getReference("contracts");
        DatabaseReference db = databaseReference_contract.child(room);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contractData= dataSnapshot.getValue(ContractData.class);
                if (contractData==null){
                    Toast.makeText(getApplicationContext(),"null",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),contractData.getSeniorName(),Toast.LENGTH_SHORT).show();
                }
                TextView seniorName = (TextView)findViewById(R.id.seniorName_m);
                TextView studentName = (TextView)findViewById(R.id.studentName_m);
                TextView effectiveDate = (TextView)findViewById(R.id.effectiveDate_m);
                TextView periodMonth = (TextView)findViewById(R.id.periodMonth_m);
                TextView expirationDate = (TextView)findViewById(R.id.expirationDate_m);
                TextView address = (TextView)findViewById(R.id.address_contract_m);
                TextView monthlyFee = (TextView)findViewById(R.id.monthlyFee_m);
                TextView smoking = (TextView)findViewById(R.id.smoking_m);
                TextView smoking_consent = (TextView)findViewById(R.id.smoking_consent_m);
                TextView smoking_detail = (TextView)findViewById(R.id.smoking_detail_m);
                TextView pet = (TextView)findViewById(R.id.pet_m);
                TextView pet_consent = (TextView)findViewById(R.id.pet_consent_m);
                TextView pet_detail = (TextView)findViewById(R.id.pet_detail_m);
                TextView cerfew = (TextView)findViewById(R.id.cerfew_m);
                TextView cerfew_consent = (TextView)findViewById(R.id.cerfew_consent_m);
                TextView cerfew_detail = (TextView)findViewById(R.id.cerfew_detail_m);
                TextView help = (TextView)findViewById(R.id.help_m);
                TextView help_consent = (TextView)findViewById(R.id.help_consent_m);
                TextView help_detail = (TextView)findViewById(R.id.help_detail_m);
                TextView extraspecial = (TextView)findViewById(R.id.extraspecial_m);
                TextView extraspecial_consent = (TextView)findViewById(R.id.extraspecial_consent_m);
                TextView writedate = (TextView)findViewById(R.id.writedate_m);

                final int color_unConsent=getResources().getColor(R.color.colorLightRed);
                final int color_Consent=getResources().getColor(R.color.colorLightGreen);

                if(contractData!=null) {
                    seniorName.setText(contractData.getSeniorName() + " 어르신");
                    studentName.setText(contractData.getStudentName() + " 학    생");
                    effectiveDate.setText(contractData.getStartdate());
                    periodMonth.setText(contractData.getMonthperiod() + "개월");
                    expirationDate.setText(contractData.getExpirationdate());
                    int tempFee = Integer.parseInt(contractData.getMonthlyfee());
                    DecimalFormat df = new DecimalFormat("###,###,###,###");
                    String formalFee = df.format(tempFee);
                    monthlyFee.setText(formalFee+"원");
                    address.setText(contractData.getAddress());

                    if (contractData.isSmokingConsent()) {
                        smoking.setBackgroundColor(color_Consent);
                        smoking_consent.setText("합의");
                        smoking_detail.setText(contractData.getSmoke_detail());
                    }
                    else {
                        smoking.setBackgroundColor(color_unConsent);
                        smoking_consent.setText("미합의");
                    }
                    if (contractData.isPetConsent()) {
                        pet.setBackgroundColor(color_Consent);
                        pet_consent.setText("합의");
                        pet_detail.setText(contractData.getPet_detail());
                    }
                    else {
                        pet.setBackgroundColor(color_unConsent);
                        pet_consent.setText("미합의");
                    }
                    if (contractData.isCerfewConsent()) {
                        cerfew.setBackgroundColor(color_Consent);
                        cerfew_consent.setText("합의");
                        cerfew_detail.setText(contractData.getCerfew_detail());
                    }
                    else {
                        cerfew.setBackgroundColor(color_unConsent);
                        cerfew_consent.setText("미합의");
                    }
                    if (contractData.isHelpConsent()) {
                        help.setBackgroundColor(color_Consent);
                        help_consent.setText("합의");
                        help_detail.setText(contractData.getHelp_detail());
                    }
                    else {
                        help.setBackgroundColor(color_unConsent);
                        help_consent.setText("미합의");
                    }
                    if (contractData.isExtraConsent()) {
                        extraspecial.setBackgroundColor(color_Consent);
                        extraspecial_consent.setText("합의");
                    }
                    else {
                        extraspecial.setBackgroundColor(color_unConsent);
                        extraspecial_consent.setText("미합의");
                    }
                    writedate.setText(contractData.getContractwritedate());
                    extraspecial.setText(contractData.getExtraspecial());

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

}
