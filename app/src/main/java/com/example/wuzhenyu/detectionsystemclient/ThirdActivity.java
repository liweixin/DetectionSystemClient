package com.example.wuzhenyu.detectionsystemclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by dell on 2016/3/16.
 */
public class ThirdActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_information);
        Intent intent=getIntent();
        Button traceRoute = (Button) findViewById(R.id.traceroute);
        traceRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThirdActivity.this, TraceRouteActivity.class));
            }
        });
        TextView ssidView=(TextView)findViewById(R.id.ssidView);
        ssidView.setText(intent.getStringExtra("ssid"));
        TextView bssidView = (TextView) findViewById(R.id.bssidView);
        bssidView.setText(intent.getStringExtra("bssid"));
        TextView freqView = (TextView) findViewById(R.id.freqView);
        freqView.setText(String.valueOf(Math.abs(intent.getIntExtra("freq",0))));
        TextView secView = (TextView) findViewById(R.id.secView);
        secView.setText(intent.getStringExtra("sec"));
        TextView signalStrenth = (TextView)findViewById(R.id.signal_strenth);
        signalStrenth.setText(String.valueOf(Math.abs(intent.getIntExtra("strength",0))));
    }
}