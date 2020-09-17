package com.example.hhj73.fix;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by skrud on 2018-06-14.
 */

public class ContractAdapterSenior extends ArrayAdapter<ContractData> {
    final Context context;
    final ArrayList<ContractData> m_contractData;
    public ContractAdapterSenior(@NonNull Context context, int resource, @NonNull ArrayList<ContractData> objects) {
        super(context, resource, objects);
        this.context = context;
        m_contractData = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if(v==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.contract_row_senior,null);
        }

        TextView seniorName = (TextView) v.findViewById(R.id.seniorName_s);
        TextView studentName = (TextView) v.findViewById(R.id.studentName_s);
        TextView effectiveDate = (TextView) v.findViewById(R.id.effectiveDate_s);
        TextView periodMonth = (TextView) v.findViewById(R.id.periodMonth_s);
        TextView expirationDate = (TextView) v.findViewById(R.id.expirationDate_s);
        TextView smoking = (TextView) v.findViewById(R.id.smoking_s);
        TextView smoking_consent = (TextView) v.findViewById(R.id.smoking_consent_s);
        TextView smoking_detail = (TextView) v.findViewById(R.id.smoking_detail_s);
        TextView pet = (TextView) v.findViewById(R.id.pet_s);
        TextView pet_consent = (TextView) v.findViewById(R.id.pet_consent_s);
        TextView pet_detail = (TextView) v.findViewById(R.id.pet_detail_s);
        TextView cerfew = (TextView) v.findViewById(R.id.cerfew_s);
        TextView cerfew_consent = (TextView) v.findViewById(R.id.cerfew_consent_s);
        TextView cerfew_detail = (TextView) v.findViewById(R.id.cerfew_detail_s);
        TextView help = (TextView) v.findViewById(R.id.help_s);
        TextView help_consent = (TextView) v.findViewById(R.id.help_consent_s);
        TextView help_detail = (TextView) v.findViewById(R.id.help_detail_s);
        TextView extraspecial = (TextView) v.findViewById(R.id.extraspecial_s);
        TextView extraspecial_consent = (TextView) v.findViewById(R.id.extraspecial_consent_s);
        TextView writedate = (TextView) v.findViewById(R.id.writedate_s);
        TextView monthlyfee = (TextView) v.findViewById(R.id.monthlyFee_s);
        TextView address = (TextView)v.findViewById(R.id.address_contract_s);

        final int color_unConsent=v.getResources().getColor(R.color.colorLightRed);
        final int color_Consent=v.getResources().getColor(R.color.colorLightGreen);
        final ContractData contractData = m_contractData.get(position);

        if(contractData != null){

            seniorName.setText(contractData.getSeniorName()+" 어르신");
            studentName.setText(contractData.getStudentName()+" 학    생");
            effectiveDate.setText(contractData.getStartdate());
            periodMonth.setText(contractData.getMonthperiod()+"개월");
            expirationDate.setText(contractData.getExpirationdate());
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
            int tempFee = Integer.parseInt(contractData.getMonthlyfee());
            DecimalFormat df = new DecimalFormat("###,###,###,###");
            String formalFee = df.format(tempFee);
            monthlyfee.setText(formalFee+"원");
            address.setText(contractData.getAddress());
            extraspecial.setText(contractData.getExtraspecial());

        }

        return v;
    }
}
