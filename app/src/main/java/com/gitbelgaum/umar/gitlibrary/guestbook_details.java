package com.gitbelgaum.umar.gitlibrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.AddressConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class guestbook_details extends AppCompatActivity {


    String biblio_number;
    ImageView book_cover;
    TextView title,author,pages,price,available,discription;
    Button map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guestbook_details);

        Bundle extra = getIntent().getExtras();
        if(!extra.isEmpty()){
            biblio_number = extra.getString("b_num");
        }
        title = (TextView) findViewById(R.id.details_guest_title);
        author = (TextView) findViewById(R.id.details_author);
        pages = (TextView) findViewById(R.id.details_pages);
        price = (TextView) findViewById(R.id.details_price);
        available = (TextView) findViewById(R.id.details_no_of_books);
        discription = (TextView) findViewById(R.id.detail_discription);
        book_cover = (ImageView) findViewById(R.id.book_cover_image);
        map = (Button) findViewById(R.id.map);
        if(checkInternetConenction()) {
            new BookDetailsFetch().execute(biblio_number);
        }else {
            Toast.makeText(getApplicationContext(),"No Internet Connection...",Toast.LENGTH_SHORT).show();
        }
    }


    private class BookDetailsFetch extends AsyncTask<String,Void,String> {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(guestbook_details.this);
            pd.setMessage("Loading....");
            pd.setCancelable(true);
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(Util.SERVER_ADDRESS+"book_details.php?bibitem=" + urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()) {
                result = result.trim();
                final String[] book_details = result.split("@@");
                title.setText(book_details[0]);
                author.setText(book_details[1]);
                pages.setText(book_details[2]);
                price.setText(book_details[3]);
                available.setText(book_details[4]);
                discription.setText(book_details[5]);
                final String isbn = book_details[7];
                new ImageUrl().execute(isbn);
                // Toast.maFkeText(getActivity().getApplicationContext(),isbn,Toast.LENGTH_SHORT).show();
                // new LoadImage().execute(imgurl);
                pd.dismiss();
                map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(getApplicationContext(), Map.class);
                        in.putExtra("location", book_details[6]);
                        startActivity(in);
                    }
                });
            }else{
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"connection problem",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class ImageUrl extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadUrl("https://www.googleapis.com/books/v1/volumes?q=isbn:"+params[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }

        }

        @Override
        protected void onPostExecute(String urldata) {

            if(urldata.contains("smallThumbnail")){
                int start = urldata.indexOf("smallThumbnail")+18;
                int end = urldata.indexOf("thumbnail")-9;
                Log.i("" + start, "" + end);
                String imageurl = urldata.substring(start,end);
                new LoadImage().execute(imageurl);
            }else{
                Drawable img = getResources().getDrawable(R.mipmap.current_red);
                book_cover.setImageDrawable(img);
            }
        }
    }
    private String downloadUrl(String myurl) throws IOException {

        StringBuffer result = new StringBuffer("");
        try {
            URL url = new URL(myurl);
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


    private class LoadImage extends AsyncTask<String, Void, Bitmap> {///////////////////////////////////////////////////////////////////

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(input);

                return image;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                book_cover.setImageBitmap(bitmap);
            }
        }
    }
    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =(ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(getApplicationContext(), "No Internet GIT'n....", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

}
