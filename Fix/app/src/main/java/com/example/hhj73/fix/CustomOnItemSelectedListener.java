package com.example.hhj73.fix;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by skrud on 2018-06-10.
 */
public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : "+parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Todo Auto-generated method stub
    }
}