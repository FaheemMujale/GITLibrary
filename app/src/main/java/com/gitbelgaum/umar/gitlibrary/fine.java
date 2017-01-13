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
import android.widget.ListView;
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


public class fine extends Fragment {


    View rootview;
    UserLocalStore userLocalStore;
    ListView list;
    String b_num;
    TextView fine_total,fine_date,fine_amount;
   private List<FineList> fine_data = new ArrayList<FineList>();
    public fine() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_fine, container, false);
        fine_total = (TextView) rootview.findViewById(R.id.total_fine);
        list = (ListView) rootview.findViewById(R.id.fine_list);
        userLocalStore = new UserLocalStore(getActivity().getApplicationContext());


        b_num = getArguments().getString("b_num");
        int bnumber = Integer.parseInt(b_num);
        String URL = Util.SERVER_ADDRESS+"fine.php?bnum="+bnumber;
        URL = URL.trim();
        if(userLocalStore.getfinedata().isEmpty()) {
            downloaddata(URL);
        }else{
            if(userLocalStore.isfineUpdated()){
                downloaddata(URL);
            }else {
                displayresult(userLocalStore.getfinedata());
            }
        }

        return rootview;
    }


    private void downloaddata(String URL){
        if(checkinternet(getActivity().getApplicationContext())){
            new FineData().execute(URL);
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"No Internet Connection...",Toast.LENGTH_SHORT).show();
        }
    }

    private void populatearrayadapter(){
        ArrayAdapter<FineList> adapter = new MyListAdapter();
        list.setAdapter(adapter);
    }
    private class MyListAdapter extends ArrayAdapter<FineList>{
        public MyListAdapter(){
            super(getActivity().getApplicationContext(),R.layout.fine_item,fine_data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView==null){

                itemView = getActivity().getLayoutInflater().inflate(R.layout.fine_item,parent,false);
            }
            //find book
            FineList currentfine = fine_data.get(position);

            fine_date = (TextView) itemView.findViewById(R.id.fine_date);
            fine_amount = (TextView) itemView.findViewById(R.id.fine_amount);
            fine_date.setText(currentfine.getDate().toString().trim());
            fine_amount.setText(currentfine.getAmount().toString().trim()+"Rs");

            return itemView;
        }

    }




    class FineData extends AsyncTask<String,Void,String>{
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
        protected String doInBackground(String... params) {
            try {
                return downloadUrl(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            result=result.trim();
            if(result.isEmpty()){
                Toast.makeText(getActivity().getApplicationContext(),"No Data Found",Toast.LENGTH_SHORT).show();
            }else{
                userLocalStore.storefinedata(result);
                displayresult(result);
            }
            pd.dismiss();
        }
    }
    private void displayresult(String result){
        String total;
        String[] data = result.split("@");
        int i = data.length;
        total = data[i-1];
        i = i-1;
        i = i/2;
        int j = i;
        while(i>0){
            int k = (j-i)*2;
            fine_data.add(new FineList(data[k],data[k+1]));
            i--;
        }
        String[] totalsplit = total.split("\\.");
        int a = Integer.parseInt(totalsplit[0]);
        if(a>=100){
            fine_total.setTextColor(getResources().getColor(R.color.red));
            Toast.makeText(getActivity().getApplicationContext(),"You got some fine to pay",Toast.LENGTH_SHORT).show();
        }
        fine_total.setText(total+" Rs");
        populatearrayadapter();
        userLocalStore.fineUpdated(false);
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

