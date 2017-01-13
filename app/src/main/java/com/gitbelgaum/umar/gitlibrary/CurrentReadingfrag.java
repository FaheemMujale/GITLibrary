package com.gitbelgaum.umar.gitlibrary;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class CurrentReadingfrag extends Fragment{

    private List<Cbooks> books = new ArrayList<Cbooks>();
    ListView list;
    String b_num;
    TextView duedate;
    UserLocalStore userLocalStore;
    View rootview;
    ImageView cover_image;
    AlarmService alarmService;
    ArrayAdapter<Cbooks> adapter;
    String URL;
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    public String bcode,isbncode,itemnumber;

    public CurrentReadingfrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_current_readingfrag, container, false);
        b_num = getArguments().getString("b_num");
        list = (ListView) rootview.findViewById(R.id.books_history);
        int bnumber = Integer.parseInt(b_num);
        Snackbar.make(rootview, "Long Press To Renew", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        userLocalStore = new UserLocalStore(getActivity().getApplicationContext());
        if(userLocalStore.getcurrentbookdata().isEmpty()){
        URL = Util.SERVER_ADDRESS+"current_books.php?bnum="+bnumber;
        URL = URL.trim();
            if(checkInternetConenction()) {
                new BookDetailsFetch(false, getActivity().getApplicationContext()).execute(URL);
            }else {
                Toast.makeText(getActivity().getApplicationContext(),"No Internet Connection..." ,Toast.LENGTH_SHORT).show();
            }
        }else{
            populatelist(userLocalStore.getcurrentbookdata().toString());
        }
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                bcode = books.get(position).getBook_barcode().toString().trim();
                isbncode = books.get(position).getBook_isbn().toString().trim();
                itemnumber = books.get(position).getBook_itemnumber().toString().trim();
                if(books.get(position).getBook_renewals().contains("YES")){
                    Intent intent = new Intent(getActivity().getApplicationContext(),BarcodeCaptureActivity.class);
                    intent.putExtra("renew","renew");
                    startActivityForResult(intent, RC_BARCODE_CAPTURE);

                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"Renewal Not Available On This Book",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        if(checkInternetConenction() == true){
        }
        return rootview;
    }



    private void populatearrayadapter(){
        adapter = new MyListAdapter();
        list.setAdapter(adapter);
    }



    private class MyListAdapter extends ArrayAdapter<Cbooks> {
        View itemView;
        public MyListAdapter(){
            super(getActivity().getApplicationContext(),R.layout.item_view,books);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            itemView = convertView;
            if(itemView==null){

                itemView = getActivity().getLayoutInflater().inflate(R.layout.item_view,parent,false);
            }
            //find book
            final Cbooks currentbooks = books.get(position);

            //fill view
            TextView title = (TextView) itemView.findViewById(R.id.book_title1);
            TextView author = (TextView) itemView.findViewById(R.id.author2);
            TextView issuedate = (TextView) itemView.findViewById(R.id.current_issue_date);
            duedate = (TextView) itemView.findViewById(R.id.current_due_date);
            title.setText(currentbooks.getBook_title());
            author.setText(currentbooks.getBook_author());
            issuedate.setText(currentbooks.getBook_issuedate());
            if(Integer.parseInt(currentbooks.getDays_remaining())<0){
                duedate.setTextColor(getResources().getColor(R.color.red));
            }else{
                duedate.setTextColor(getResources().getColor(R.color.green));
            }
            duedate.setText(currentbooks.getBook_datedue());
            cover_image = (ImageView) itemView.findViewById(R.id.history_cover_img);
            if(userLocalStore.getbookcoverimage(position) != null){
                cover_image.setImageBitmap(userLocalStore.getbookcoverimage(position));
            }else{
            if(currentbooks.getImg()!=null){
                cover_image.setImageBitmap(currentbooks.getImg());
            }else{
                new GetImageUrl(position).execute(currentbooks.getBook_isbn());
            }
            }
            return itemView;
        }


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {

                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if(bcode.equals(barcode.displayValue.toString().trim()) || isbncode.equals(barcode.displayValue.toString().trim())){
                        String url = Util.SERVER_ADDRESS+"renewbook.php?itemnumber="+itemnumber;
                        url = url.trim();
                        if(checkInternetConenction()){
                        new BookDetailsFetch(false,getActivity().getApplicationContext()).execute(url);
                        books.clear();
                        adapter.notifyDataSetChanged();}
                        else{
                            Toast.makeText(getActivity().getApplicationContext(),"No Internet Connection..." ,Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),"Wrong Book...",Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void updatebooksdata(Context context){
        if(checkinternet(context) == true){
            userLocalStore = new UserLocalStore(context);
            new BookDetailsFetch(true,context).execute(Util.SERVER_ADDRESS+"current_books.php?bnum="+
                    userLocalStore.getuserlogedin().bnum.toString().trim());
        }
    }

    private class BookDetailsFetch extends AsyncTask<String,Void,String> {
        boolean update;
        Context context;
        BookDetailsFetch(boolean update,Context context){
            this.update = update;
            this.context = context;
        }
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            if(!update){
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading....");
            pd.setCancelable(true);
            pd.show();}
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
            if(!update){
            if(result.endsWith("$$$")){
                if(checkInternetConenction()){
                new BookDetailsFetch(false,getActivity().getApplicationContext()).execute(URL);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"No Internet Connection..." ,Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getActivity().getApplicationContext(),"Successfully Renewed",Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }else{
            if(result.isEmpty()){
                Toast.makeText(getActivity().getApplicationContext(), "NO BOOKS CURRENTLY ISSUED", Toast.LENGTH_LONG).show();
            }else{
                userLocalStore.storecurrentbookdata(result);
                populatelist(result);
            }
                pd.dismiss();
            }}else{
                userLocalStore = new UserLocalStore(context);
                if(result != userLocalStore.getcurrentbookdata()){
                userLocalStore.storecurrentbookdata(result);
                String[] book_data = result.split("@@");
                int i = book_data.length/9;
                int j= i;
                while(i>0){
                    int k=(j-i)*9;
                    userLocalStore.storeremainingdays(book_data[k+7],book_data[k+4]);
                    i--;
                }
                    userLocalStore.setupdate(true);
                    userLocalStore.fiftyfinedata(true);
              }
            }

        }
    }

    private void populatelist(String result){
        String[] book_data = result.split("@@");
        int i = book_data.length/9;
        int j= i;
        while(i>0){
            int k=(j-i)*9;
            books.add(new Cbooks(book_data[k],book_data[k+1],book_data[k+2],book_data[k+3],book_data[k+4],
                    book_data[k+5],book_data[k+6],book_data[k+7],book_data[k+8]));
            userLocalStore.storeremainingdays(book_data[k+7],book_data[k+4]);
            i--;
        }

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

    private class GetImageUrl extends AsyncTask<String,Void,String>{

        int position;
        public GetImageUrl(int pos){
            this.position = pos;
        }
        @Override
        protected String doInBackground(String... isbn) {
            try {
                return downloadUrl("https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String urldata) {
            if(urldata.contains("smallThumbnail")) {
                int start = urldata.indexOf("smallThumbnail")+18;
                int end = urldata.indexOf("thumbnail")-9;
                String imageurl = urldata.substring(start,end);
                if(imageurl.contains("http://books.google.co.in")){
                new LoadImage(position).execute(imageurl);
                }
            }
        }


    }
    private class LoadImage extends AsyncTask<String, Void, Bitmap> {///////////////////////////////////////////////////////////////////

        int position;

        public LoadImage(int pos) {
            this.position = pos;

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            final String imageURL = params[0];
            try {
                URL url = new URL(imageURL);
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
            books.get(position).setImg(bitmap);
            userLocalStore.storebookcoverimage(bitmap, position);
            adapter.notifyDataSetChanged();
            alarmService = new AlarmService(getActivity().getApplicationContext());/////////////////////////
            alarmService.startbookreminderalarm();
        }
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

