package com.gitbelgaum.umar.gitlibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    UserLocalStore userLocalStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userLocalStore = new UserLocalStore(getApplicationContext());
        final CompoundButton notification = (CompoundButton) findViewById(R.id.notifications);
        notification.setChecked(userLocalStore.isnotification());
        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    userLocalStore.setnotification(true);
                }else{
                    userLocalStore.setnotification(false);
                }
            }
        });
    }
}
