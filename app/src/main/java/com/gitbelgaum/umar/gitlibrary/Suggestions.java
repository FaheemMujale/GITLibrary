package com.gitbelgaum.umar.gitlibrary;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class Suggestions extends Fragment {

    ListView suggestion_list;
    ArrayAdapter<SuggestionData> adapter;
    Button submit;
    List<SuggestionData> sug_list = new ArrayList<SuggestionData>();
    View rootview;
    EditText text;
    String name;
    User user;
    UserLocalStore userLocalStore;
    public Suggestions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userLocalStore = new UserLocalStore(getActivity().getApplicationContext());
        user = userLocalStore.getuserlogedin();
        rootview = inflater.inflate(R.layout.fragment_suggestions, container, false);
        suggestion_list = (ListView) rootview.findViewById(R.id.suggestion_list);
        sug_list.clear();
        name = user.getname() +" ("+ user.getusn()+")";
        String url = "http://210.212.207.8/libraryapp/suggestions.php?name="+name; // changed ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
        url = url.trim();
        url = url.replace(" ","%20");
        if(checkinternet(getActivity().getApplicationContext())){
        new GetSuggestions().execute(url);
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        submit = (Button) rootview.findViewById(R.id.submit_sug);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                text = (EditText) rootview.findViewById(R.id.suggestion_text);
                if (text.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Type your suggestion first", Toast.LENGTH_SHORT).show();
                } else {
                    sug_list.clear();
                    name = name.toLowerCase();
                    String message = text.getText().toString().trim();
                    message = message.toLowerCase();
                    String url = "http://210.212.207.8/libraryapp/suggestions.php?name="+name+"&text="+message;
                    url = url.trim();
                    url = url.replace(" ","%20");
                    sug_list.clear();
                    if(checkinternet(getActivity().getApplicationContext())){
                        sug_list.clear();
                        new GetSuggestions().execute(url);
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }
                text.setText("");
            }
        });
        return rootview;
    }




    private void populatearrayadapter(){
        adapter = new SuggestionList();
        suggestion_list.setAdapter(adapter);
    }
    private class SuggestionList extends ArrayAdapter {

        View itemview;

        public SuggestionList() {
            super(getActivity().getApplicationContext(), R.layout.suggestion_item, sug_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            itemview = convertView;
            if (itemview == null) {
                itemview = getActivity().getLayoutInflater().inflate(R.layout.suggestion_item, parent, false);
            }
            SuggestionData suggestionData = sug_list.get(position);
            TextView name = (TextView) itemview.findViewById(R.id.user_name_sugg);
            TextView text = (TextView) itemview.findViewById(R.id.suggestion_text);
            TextView date = (TextView) itemview.findViewById(R.id.suggestion_date);
            ImageView img = (ImageView) itemview.findViewById(R.id.sugg_image);
            name.setText(suggestionData.getname().trim());
            text.setText(suggestionData.gettext().trim());
            date.setText(suggestionData.getdate().trim());

            if(userLocalStore.getpropic() == null) {
            String firstletter = String.valueOf(suggestionData.getname().toUpperCase().charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            TextDrawable drawable = TextDrawable.builder().buildRound(firstletter,color);
            img.setBackground(drawable);
            }else{
            img.setImageBitmap(userLocalStore.getpropic());
            }
            return itemview;
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
    private class GetSuggestions extends AsyncTask<String,Void,String>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setCancelable(true);
            pd.setMessage("Loading...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                return downloadUrl(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String data) {
            data.trim();
            if(!data.isEmpty()){
                String[] result = data.split("#");
                int i = result.length/3;
                int j= i;
                while(i>0){
                    int k=(j-i)*3;
                    //String[] name = result[k].split("\\s+");
                 String name = result[k].substring(0,1)+result[k].substring(1);
                    sug_list.add(new SuggestionData(name,result[k+1],result[k+2]));
                    i--;
                }
                populatearrayadapter();
                pd.dismiss();
            }else{
                Toast.makeText(getActivity().getApplicationContext(),"You have not made any suggestions..",Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        }
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