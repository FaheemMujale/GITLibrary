package com.gitbelgaum.umar.gitlibrary;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GUEST extends AppCompatActivity {
    SearchView title;
    String sub_string;
    List<Sbooks> sbooks = new ArrayList<Sbooks>();
    ListView list;
    boolean update = true;
    ArrayAdapter<Sbooks> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        list = (ListView) findViewById(R.id.guest_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getApplicationContext(),guestbook_details.class);
                in.putExtra("b_num",sbooks.get(position).getBook_biblio_num().toString().trim());
                startActivity(in);
            }
        });

        title = (SearchView) findViewById(R.id.guest_search);
        title.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cleanup();
                if(query.length()>2){

                    sub_string = query.replace(" ","%20");
                    if(checkInternetConenction()) {
                        new SearchBookDetailsFetch().execute(sub_string);
                    }else{
                        Toast.makeText(getApplicationContext(),"No Internet Connection...",Toast.LENGTH_SHORT).show();

                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cleanup();
                if(newText.length()>2){
                    sub_string = newText.replace(" ", "%20");
                    if(checkInternetConenction()) {
                        new SearchBookDetailsFetch().execute(sub_string);
                    }else{
                        Toast.makeText(getApplicationContext(),"No Internet Connection...",Toast.LENGTH_SHORT).show();

                    }
                }else{
                    if(checkInternetConenction()) {
                        new SearchBookDetailsFetch().execute("");
                    }else{
                        Toast.makeText(getApplicationContext(),"No Internet Connection...",Toast.LENGTH_SHORT).show();

                    }
                }
                return false;
            }
        });
    }


    private void populatearrayadapter(){
        adapter = new MyListAdapter();
        list.setAdapter(adapter);
    }
    public void cleanup(){
        sbooks.clear();

    }



    private class MyListAdapter extends ArrayAdapter<Sbooks> {
        View itemview;
        public MyListAdapter() {
            super(getApplicationContext(), R.layout.search_item_view,sbooks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            itemview = convertView;
            if(itemview ==null){
                itemview = getLayoutInflater().inflate(R.layout.search_item_view,parent,false);
            }
            if(sbooks.size()>=position){
                Sbooks search_books = sbooks.get(position);
                TextView book_name = (TextView) itemview.findViewById(R.id.book_name_search);
                book_name.setText(search_books.getBook_name().trim());
                TextView book_author = (TextView) itemview.findViewById(R.id.author_name_search);
                book_author.setText(search_books.getBook_author().trim());}
            return itemview;
        }
    }



    public class SearchBookDetailsFetch extends AsyncTask<String,Void,String> {

        String substring;
        @Override
        protected String doInBackground(String... urls) {
            try {
                substring = urls[0];
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            result = result.trim();
            String[] searchresult = result.split("@@");
            int i = searchresult.length/3;
            if(i==0 && !substring.isEmpty()){
                Toast.makeText(getApplicationContext(), "No Books Found", Toast.LENGTH_SHORT).show();
            }
            int j= i;
            while(i>0){
                int k=(j-i)*3;
                sbooks.add(new Sbooks(searchresult[k],searchresult[k+1],searchresult[k+2]));
                i--;}
            if(update) {
                populatearrayadapter();
                update = false;
            }else{
                adapter.notifyDataSetChanged();
            }

        }
    }
    private String downloadUrl(String searchstring) throws IOException {
        StringBuffer result = new StringBuffer("");

        try {
            URL url = new URL(Util.SERVER_ADDRESS+"search.php?string="+searchstring);
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

    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

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
