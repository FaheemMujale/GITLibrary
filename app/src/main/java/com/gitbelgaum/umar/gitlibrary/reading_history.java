package com.gitbelgaum.umar.gitlibrary;


import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class reading_history extends Fragment {

    private List<Hbooks> books = new ArrayList<Hbooks>();
    ListView list;
    String b_num;
    View rootview;
    UserLocalStore userLocalStore;
    ImageView coverimage;
    public reading_history() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_reading_history, container, false);
        list = (ListView) rootview.findViewById(R.id.books_history);
        userLocalStore = new UserLocalStore(getActivity().getApplicationContext());
        b_num = getArguments().getString("b_num");
        int bnumber = Integer.parseInt(b_num);
        if(userLocalStore.getbookhistory().isEmpty() || userLocalStore.isupdated()){
             String URL = Util.SERVER_ADDRESS+"reading_history.php?bnum="+bnumber;
             URL = URL.trim();
            if(checkInternetConenction() == true){
             new BookDetailsFetch().execute(URL);
            userLocalStore.setupdate(false);
            }else{
                Toast.makeText(getActivity().getApplicationContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }else{
            populatelist(userLocalStore.getbookhistory().toString());
        }


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book_details frag = new Book_details();
                Bundle bundle = new Bundle();
                bundle.putString("bib_num", books.get(position).getBibnum().toString());
                frag.setArguments(bundle);
                android.support.v4.app.FragmentTransaction frag_trans = getActivity().getSupportFragmentManager().beginTransaction();
                frag_trans.replace(R.id.frag_contaner, frag);
                frag_trans.addToBackStack("tag");
                frag_trans.commit();
            }
        });

        return rootview;
    }


    private void populatearrayadapter(){
        ArrayAdapter<Hbooks> adapter = new MyListAdapter();
        list.setAdapter(adapter);
    }
    private class MyListAdapter extends ArrayAdapter<Hbooks>{
        public MyListAdapter(){
            super(getActivity().getApplicationContext(),R.layout.item_view2,books);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView==null){

                itemView = getActivity().getLayoutInflater().inflate(R.layout.item_view2,parent,false);
            }
            //find book
            Hbooks currentbooks = books.get(position);

            //fill view
            TextView title = (TextView) itemView.findViewById(R.id.book_title1);
            title.setText(currentbooks.getTitle());
            TextView author = (TextView) itemView.findViewById(R.id.author2);
            author.setText(currentbooks.getAuthor());
            TextView issue_date = (TextView) itemView.findViewById(R.id.issue_date2);
            issue_date.setText(currentbooks.getIssue_date());
            TextView return_date = (TextView) itemView.findViewById(R.id.return_date2);
            return_date.setText(currentbooks.getReturn_date());
            coverimage  = (ImageView) itemView.findViewById(R.id.history_cover_img);

            //awesome idea
            String firstletter = String.valueOf(currentbooks.getTitle().charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            TextDrawable drawable = TextDrawable.builder().buildRound(firstletter,color);
            coverimage.setImageDrawable(drawable);

            return itemView;
        }

    }

    //////////////////////////
    private class BookDetailsFetch extends AsyncTask<String,Void,String> {


        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading....");
            pd.setCancelable(true);
            pd.show();
            super.onPreExecute();
        }
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
            if(result.isEmpty()){
                Toast.makeText(getActivity().getApplicationContext(), "NO BOOKS", Toast.LENGTH_LONG).show();
            }else{
                userLocalStore.storebookhistory(result);
                populatelist(result);
            }
           pd.dismiss();


        }
    }



    private void populatelist(String result){
        String[] book_data = result.split("@@");
        int i = book_data.length/6;
        int j= i;
        while(i>0){
            int k=(j-i)*6;
            books.add(new Hbooks(book_data[k],book_data[k+1],book_data[k+2],book_data[k+3],book_data[k+4],book_data[k+5]));
            i--;}
        populatearrayadapter();
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
    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =(ConnectivityManager)getActivity().getSystemService(getActivity().getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(getActivity().getApplicationContext(), "No Internet GIT'n....", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

}
