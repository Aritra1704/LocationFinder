package com.arpaul.locationfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.arpaul.utilitieslib.StringUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StringUtils.getInt("0");
    }
}
