package com.project.markpollution;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AboutUsActivity extends AppCompatActivity {
TextView rateus,curentver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        rateus = (TextView) findViewById(R.id.rateus);
        curentver = (TextView) findViewById(R.id.currentver);
        String versionName = BuildConfig.VERSION_NAME;
        curentver.setText(getResources().getString(R.string.currentver)+" "+versionName);
        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Comming soon",Toast.LENGTH_LONG).show();
            }
        });
    }

}
