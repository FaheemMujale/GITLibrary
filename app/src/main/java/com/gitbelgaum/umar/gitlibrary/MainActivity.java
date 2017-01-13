package com.gitbelgaum.umar.gitlibrary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar = null;
    NavigationView navigationView = null;
    TextView name, email;
    ImageView propic;
    String nm, em, bnumber;
    UserLocalStore userLocalStore;
    Main_frag frag;
    Book_Search booksearchfrag;
    boolean backbutton = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userLocalStore = new UserLocalStore(getApplicationContext());
        //setting fragment
        frag = new Main_frag();
        main_frag_open();
        booksearchfrag = new Book_Search();

        this.nm = userLocalStore.getuserlogedin().name;
        this.em = userLocalStore.getuserlogedin().email;
        this.bnumber = userLocalStore.getuserlogedin().bnum;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        name = (TextView) header.findViewById(R.id.name);
        email = (TextView) header.findViewById(R.id.email);
        propic = (ImageView) header.findViewById(R.id.propic);//////////////////////////////////////////////////////////////////////////
        name.setText(nm);
        email.setText(em);
        if(userLocalStore.getpropic() != null) {
            propic.setImageBitmap(userLocalStore.getpropic());
        }else{
            new LoadImage().execute(bnumber);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
          // moveTaskToBack(true);
            if(getSupportFragmentManager().getBackStackEntryCount()>0) {
                getSupportFragmentManager().popBackStack();
            }else{
                if(backbutton){
                    moveTaskToBack(true);
                    backbutton = false;
                }
                Toast.makeText(this,"Press one more time to exit",Toast.LENGTH_SHORT).show();
                backbutton = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backbutton = false;
                    }
                },2000);

            }
        }

    }

    //////////////////

    private class LoadImage extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            result = result.trim();
            try{
            byte[] decodedString = Base64.decode(result.getBytes(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (decodedByte != null) {
                propic.setImageBitmap(decodedByte);
                userLocalStore.storeuserpropic(result);
            } else {
                Drawable img = getResources().getDrawable(R.drawable.default_image);
                propic.setBackground(img);
                }
            }catch (Exception e){
                Drawable img = getResources().getDrawable(R.drawable.default_image);
                propic.setBackground(img);
            }
        }
    }


    private String downloadUrl(String myurl) throws IOException {
        StringBuffer result = new StringBuffer("");
        try {
            URL url = new URL(Util.SERVER_ADDRESS+"propic.php?id="+myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem bookitem = menu.findItem(R.id.search_books);
        final SearchView booksearch = (SearchView) MenuItemCompat.getActionView(bookitem);
        booksearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                book_search_open();
                booksearchfrag.cleanup();
                if(checkinternet(getApplicationContext()))
                {
                booksearchfrag.search(query);
                }else{
                    Toast.makeText(getApplicationContext(),"No Internet connection",Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                book_search_open();
                booksearchfrag.cleanup();
                if (newText.length() > 2) {
                    //fetch data
                    if(checkinternet(getApplicationContext()))
                    {
                        booksearchfrag.search(newText);
                    }else{
                        Toast.makeText(getApplicationContext(),"No Internet connection",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(checkinternet(getApplicationContext()))
                    {
                        booksearchfrag.search("");
                    }else{
                        Toast.makeText(getApplicationContext(),"No Internet connection",Toast.LENGTH_SHORT).show();
                    }
                }


                return false;

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_books) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_whatsnew) {
            main_frag_open();
        } else if (id == R.id.nav_current_reading) {
            CurrentReadingfrag frag = new CurrentReadingfrag();
            android.support.v4.app.FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
            frag_trans.replace(R.id.frag_contaner, frag);
            frag_trans.addToBackStack("tag");
            Bundle bundle = new Bundle();
            bundle.putString("b_num", this.bnumber.toString());
            frag.setArguments(bundle);
            frag_trans.commit();
        } else if (id == R.id.nav_reading_history) {
            reading_history frag = new reading_history();
            android.support.v4.app.FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
            frag_trans.addToBackStack("tag");
            frag_trans.replace(R.id.frag_contaner, frag);
            Bundle bundle = new Bundle();
            bundle.putString("b_num", this.bnumber.toString());
            frag.setArguments(bundle);
            frag_trans.commit();

        } else if (id == R.id.nav_fine) {
            fine frag = new fine();
            android.support.v4.app.FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
            frag_trans.replace(R.id.frag_contaner, frag);
            frag_trans.addToBackStack("tag");
            Bundle bundle = new Bundle();
            bundle.putString("b_num", this.bnumber.toString());
            frag.setArguments(bundle);
            frag_trans.commit();

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(),Settings.class));
        }else if(id == R.id.nav_map){
            startActivity(new Intent(getApplicationContext(),Fullmap.class));
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            UserLocalStore userLocalStore = new UserLocalStore(getApplicationContext());
            userLocalStore.clearuserdata();
            startActivity(intent);

        }else if(id == R.id.nav_suggestions){
            Suggestions frag = new Suggestions();
            android.support.v4.app.FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
            frag_trans.replace(R.id.frag_contaner, frag);
            frag_trans.addToBackStack("tag");
            frag_trans.commit();
        }else if(id == R.id.nav_developer){
            startActivity(new Intent(getApplicationContext(),Developer.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void main_frag_open(){
        android.support.v4.app.FragmentTransaction frag_trans = getSupportFragmentManager().beginTransaction();
        frag_trans.replace(R.id.frag_contaner, frag);
        frag_trans.addToBackStack("tag");
        frag_trans.commit();
    }
    public void book_search_open(){
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_contaner,booksearchfrag);
        fragmentTransaction.addToBackStack("tag");
        fragmentTransaction.commit();
    }

    private boolean checkinternet(Context context){
        ConnectivityManager connec =(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            return false;
        }
        return false;
    }
}



