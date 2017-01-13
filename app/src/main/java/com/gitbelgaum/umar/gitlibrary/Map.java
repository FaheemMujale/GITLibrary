package com.gitbelgaum.umar.gitlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class Map extends AppCompatActivity {



    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Random r = new Random();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toast.makeText(getApplicationContext(),"Books position may change",Toast.LENGTH_SHORT).show();
        ImageView other = (ImageView) findViewById(R.id.other);
        ImageView csis = (ImageView) findViewById(R.id.imgcsis);
        ImageView elka = (ImageView) findViewById(R.id.img1);
        ImageView dum = (ImageView) findViewById(R.id.dummi);
        other.setVisibility(View.INVISIBLE);
        elka.setVisibility(View.INVISIBLE);
        csis.setVisibility(View.INVISIBLE);
        dum.setVisibility(View.INVISIBLE);
        Bundle extra = getIntent().getExtras();
        if(!extra.isEmpty()){
            String location = extra.getString("location");
            if(location.contains("CS") || location.contains("cs") || location.contains("IS") || location.contains("is")){
                int a = r.nextInt(400 - 80) + 90;
                csis.setVisibility(View.VISIBLE);
                csis.setPadding(0,20,a,0);
            }else if(location.contains("EE") || location.contains("ee")){
                elka.setVisibility(View.VISIBLE);
                int a = r.nextInt(240 - 185) +185;
                other.setVisibility(View.VISIBLE);
                other.setPadding(a, 0, 0, 70);
            }else if(location.contains("EC") || location.contains("ec")){
                int a = r.nextInt(240 - 20) + 20;
                other.setVisibility(View.VISIBLE);
                other.setPadding(a, 90, 0, 0);

            }else if(location.contains("ME") || location.contains("me") || location.contains("IP") || location.contains("ip")){
                int a = r.nextInt(160 - 60 ) +60;
                other.setVisibility(View.VISIBLE);
                other.setPadding(a,0,0,70);
            }else if(location.contains("CV") || location.contains("cv")){
                int a = r.nextInt(2);
                int i = 30;
                other.setVisibility(View.VISIBLE);
                if(a == 1){ i= 15;}
                other.setPadding(i,0,0,70);
            }else if(location.contains("OR")|| location.contains("or") ||location.contains("GEN") || location.contains("gen")){
                other.setVisibility(View.VISIBLE);
            }else{
                dum.setVisibility(View.VISIBLE);
            }

        }


    }


}
