package com.example.me495.lbstest;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ChangeActivity extends AppCompatActivity {

    private String mapType="normal_map", heatMap="false", trafficMap="false";
    private CheckBox heatMapButton=null, trafficMapButton=null;
    private RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            if(checkedId == R.id.normal_map) {
                mapType = "normal_map";
            } else {
                mapType = "satellite_map";
            }
        }
    };;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        heatMapButton = (CheckBox) findViewById(R.id.heat_map);
        trafficMapButton = (CheckBox) findViewById(R.id.traffic_map);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.redio_group);
        radioGroup.setOnCheckedChangeListener(radioListener);
        Button submitButton = (Button) findViewById(R.id.submit);
        heatMapButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    heatMap = "true";
                } else {
                    heatMap = "false";
                }
            }
        });
        trafficMapButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    trafficMap = "true";
                } else {
                    trafficMap = "false";
                }
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("map_type", mapType);
                intent.putExtra("heat_map",heatMap);
                intent.putExtra("traffic_map",trafficMap);
                Log.d("map_type",mapType);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}
