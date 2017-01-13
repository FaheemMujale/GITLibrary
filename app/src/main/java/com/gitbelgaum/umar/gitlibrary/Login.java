package com.gitbelgaum.umar.gitlibrary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.InputStream;

import java.net.HttpURLConnection;

import java.net.URL;
import java.util.Date;

import android.net.ConnectivityManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.vision.barcode.Barcode;


public class Login extends AppCompatActivity {

    GoogleCloudMessaging gcm;
    Context context;
    String regid;
    String msg;
    ProgressDialog pd;
    EditText username,password;
    Button b1,b2,b3;
    User user;
    UserLocalStore user_sp;
    String USN;


    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //login user directly
        user_sp = new UserLocalStore(this);
        if(user_sp.isuserloggedin() == true){
            loginUser();
        }
        context = getApplicationContext();
        setContentView(R.layout.activity_login);
        startanimation();
        username = (EditText) findViewById((R.id.editText));
        password = (EditText) findViewById((R.id.editText2));
        b1 = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.scan);
        b3 = (Button) findViewById(R.id.guest);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            b1.setBackgroundResource(R.drawable.shape);
            b2.setBackgroundResource(R.drawable.shape);
            b3.setBackgroundResource(R.drawable.shape2);
        }
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }


        } else {
            Log.i("pavan", "No valid Google Play Services APK found.");
        }
    }

    private void startanimation(){
       /* Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);*/
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate2);
        anim.reset();
        anim.setStartOffset(2000);
        ImageView img = (ImageView) findViewById(R.id.login_logo);
        img.clearAnimation();
        img.startAnimation(anim);


        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        RelativeLayout l2 = (RelativeLayout) findViewById(R.id.login_layout);
        l2.setVisibility(View.VISIBLE);
        l2.clearAnimation();
        l2.startAnimation(anim);
            }

    public void guest_search(View v){
        startActivity(new Intent(this, GUEST.class));
    }
    public void hideKeybord(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        Util.PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }



    private String getRegistrationId(Context context) {
        String registrationId = user_sp.getRegId();
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = user_sp.registeredVersion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }



    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        user_sp.storeRegId(regId, appVersion);

    }


    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                try {

                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(Login.this);
                    }
                    regid = gcm.register(Util.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;



            }
        }.execute();

    }

    /////////////////////////////////////////////////////////////////////////////////////////////


    public  void login_button(View view){
        USN= username.getText().toString().toUpperCase().trim();
        String Pass = password.getText().toString().trim();


        if(USN.equals("")|| Pass.equals("") )
        {
            Toast.makeText(getApplicationContext(), "Please enter all the fileds", Toast.LENGTH_SHORT).show();
            return;
        }

        String URL = Util.SERVER_ADDRESS+"login.php?email="+USN+"&password="+Pass;
        //10.0.2.2:80

        URL = URL.trim();

        boolean conexists = checkInternetConenction();
        if(conexists==true)
        {
            new DownloadWebpageTask().execute(URL);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }

    public void click_barcode(View v){


            // launch barcode activity.
            Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                   // Toast.makeText(this,barcode.displayValue,Toast.LENGTH_SHORT).show();
                    String url = Util.SERVER_ADDRESS+"barcode_login.php?usn="+barcode.displayValue;
                    url = url.trim();
                    if(checkInternetConenction()){
                    new DownloadWebpageTask().execute(url);
                    }else{
                        Toast.makeText(getApplicationContext(),"No Internet Connection...",Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Toast.makeText(this,"error",Toast.LENGTH_SHORT).show();
               // statusMessage.setText(String.format(getString(R.string.barcode_error),
                      //  CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(Login.this);
            pd.setMessage("Logging In...");
            pd.setCancelable(false);
            pd.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            result = result.trim();
            if(result.isEmpty()){
                Toast.makeText(getApplicationContext(),"Connection Problem",Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }else {
                if (result.equals("ERROR")) {
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else {
                    String[] input = result.split("@@");
                    User user = new User(input[0].trim(), input[1].trim(), input[2].trim(),USN);
                    user_sp.storeUserData(user);
                    new SendGcmToServer().execute();

                }
            }
        }
    }
    public void loginUser(){
        Intent in = new Intent(getApplicationContext(),MainActivity.class);
        user = user_sp.getuserlogedin();
        Toast.makeText(getApplicationContext(),"WELCOME "+user.name.toUpperCase().trim(),Toast.LENGTH_SHORT).show();
        startActivity(in);
    }
    private String downloadUrl(String myurl) throws IOException {
        StringBuffer result = new StringBuffer("");

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return result.toString();


    }
    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            Toast.makeText(this, " Connected ", Toast.LENGTH_SHORT).show();
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    private class SendGcmToServer extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                String group;
                String usn = user_sp.getuserlogedin().getusn().trim();
                if(usn.contains("ST")||usn.contains("st")||usn.contains("FC")||usn.contains("fc")){
                    group = "STAFF";
                }else if(usn.contains("MCA")||usn.contains("mca")){
                    group = "MCA";
                }else if(usn.contains("MBA")||usn.contains("mba")){
                    group = "MBA";
                }else{
                    group = "STUDENT";
                }
                String gcurl = Util.register_url+"?regId="+regid+"&bnumber="+group;
                //http://210.212.207.8/library_android/lib_gcm/register.php?regId=abcdefg&bnumber=ST
                return downloadUrl(gcurl);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }

        }

        @Override
        protected void onPostExecute(String s) {

                if (s.isEmpty()) {
                    Log.i("gcm","fail reg");
                    Toast.makeText(getApplicationContext(),"Notifaction service Failed to registor",Toast.LENGTH_SHORT).show();
                    loginUser();
                }else{
                    loginUser();
                }
           pd.dismiss();
            }
        }
    }



