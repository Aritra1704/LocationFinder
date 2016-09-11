package com.arpaul.locationfinder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.arpaul.locationfinder.R;

/**
 * Created by ARPaul on 10-09-2016.
 */
public class FirstActivity extends BaseActivity {

    private View llSecondActivity;
    private Button btnFirst, btnSecond, btnDetection, btnGeofence;

    private final String LOG_TAG ="LocationFinderSample";

    @Override
    public void initialize(Bundle savedInstanceState) {
        llSecondActivity = baseInflater.inflate(R.layout.activity_first,null);
        llBody.addView(llSecondActivity, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        initialiseControls();

        bindControls();
    }

    private void bindControls(){
        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstActivity.this, MainActivity.class));
            }
        });

        btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstActivity.this, SecondActivity.class));
            }
        });

        btnDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstActivity.this, RecognitionActivity.class));
            }
        });

        btnGeofence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstActivity.this, GeoFenceActivity.class));
            }
        });
    }

    private void initialiseControls(){
        btnFirst        = (Button) llSecondActivity.findViewById(R.id.btnFirst);
        btnSecond       = (Button) llSecondActivity.findViewById(R.id.btnSecond);
        btnDetection    = (Button) llSecondActivity.findViewById(R.id.btnDetection);
        btnGeofence    = (Button) llSecondActivity.findViewById(R.id.btnGeofence);
    }
}
