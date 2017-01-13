package com.gitbelgaum.umar.gitlibrary;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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


public class Main_frag extends Fragment {


    ListView chat_messages;
    List<ChatObject> chat_list = new ArrayList<ChatObject>();
    BroadcastReceiver recieve_chat;
    ArrayAdapter<ChatObject> adapter;
    UserLocalStore userLocalStore;
    View rootview;
    Context thiscontext;

    public Main_frag() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userLocalStore = new UserLocalStore(getActivity().getApplicationContext());
        if(userLocalStore.getwhatsnew().isEmpty()){
            rootview =  inflater.inflate(R.layout.fragment_library_intro, container, false);
        }else {
            rootview = inflater.inflate(R.layout.fragment_main_frag, container, false);
            chat_messages = (ListView) rootview.findViewById(R.id.listView_chat_messages);
            thiscontext = getActivity().getApplicationContext();
            showmessage();
            recieve_chat = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    showmessage();


                }
            };

            LocalBroadcastManager.getInstance(getActivity().getApplicationContext())
                    .registerReceiver(recieve_chat, new IntentFilter("message_recieved"));
        }
        return rootview;
    }

    private void showmessage(){
        chat_list.clear();
        if(!userLocalStore.getwhatsnew().isEmpty()){
            String[] messages = userLocalStore.getwhatsnew().split("%&%");
            int i = messages.length;
            while(i>0){
                String[] msgs = messages[i-1].split("#");
                chat_list.add(new ChatObject(msgs[0], msgs[1]));
                i--;
            }
        }
        else{
               Toast.makeText(getActivity().getApplicationContext(),"No Messages",Toast.LENGTH_SHORT).show();
            }
        populatearrayadapter();

    }

    private void populatearrayadapter(){
        adapter = new MylistAdaptor();
        chat_messages.setAdapter(adapter);
    }

    private class MylistAdaptor extends ArrayAdapter{
        View itemview;

        public MylistAdaptor() {
            super(getActivity().getApplicationContext(), R.layout.chat_view,chat_list);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            itemview = convertView;
            if(itemview ==null){
                itemview = getActivity().getLayoutInflater().inflate(R.layout.chat_view,parent,false);
            }
            ChatObject mess = chat_list.get(position);
            TextView message = (TextView) itemview.findViewById(R.id.message);
            message.setText(mess.getMessage().trim());
            TextView date = (TextView) itemview.findViewById(R.id.date);
            date.setText(mess.getDate().trim()+" ");
            return itemview;
        }

    }


}



