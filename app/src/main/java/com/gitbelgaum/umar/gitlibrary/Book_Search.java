package com.gitbelgaum.umar.gitlibrary;


import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Book_Search extends Fragment {

    List<Sbooks> sbooks = new ArrayList<Sbooks>();
    View rootview;
    ListView list;
    ArrayAdapter<Sbooks> adapter;
    Context context;

    public Book_Search() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =  inflater.inflate(R.layout.fragment_book__search, container, false);
        list = (ListView) rootview.findViewById(R.id.search_list_view);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book_details frag = new Book_details();
                Bundle bundel = new Bundle();
                context = getActivity().getApplicationContext();
                bundel.putString("bib_num",sbooks.get(position).getBook_biblio_num().toString().trim());
                frag.setArguments(bundel);
                android.support.v4.app.FragmentTransaction frag_trans = getActivity().getSupportFragmentManager().beginTransaction();
                frag_trans.replace(R.id.frag_contaner, frag);
                frag_trans.addToBackStack("tag");
                frag_trans.commit();
            }
        });
        return rootview;
    }

    public void search(String sub_string){
        sub_string = sub_string.replace(" ","%20");

            new SearchBookDetailsFetch().execute(sub_string);

    }

    private void populatearrayadapter(){
        adapter = new MyListAdapter();
        list.setAdapter(adapter);
    }
    public void cleanup(){
        sbooks.clear();

    }



    private class MyListAdapter extends ArrayAdapter<Sbooks>{
        View itemview;
        public MyListAdapter() {
            super(getActivity().getApplicationContext(), R.layout.search_item_view,sbooks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            itemview = convertView;
            if(itemview ==null){
                itemview = getActivity().getLayoutInflater().inflate(R.layout.search_item_view,parent,false);
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
                Toast.makeText(getActivity().getApplicationContext(),"No Books Found",Toast.LENGTH_SHORT).show();
            }
            int j= i;
            while(i>0){
                int k=(j-i)*3;
                sbooks.add(new Sbooks(searchresult[k],searchresult[k+1],searchresult[k+2]));
                i--;}
            populatearrayadapter();

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

}

