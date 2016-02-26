package com.compscitutorials.basigarcia.webalbum;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by luxy on 10/12/15.
 */

public class GalleryFragment extends Fragment {

    private static final String SERVER_ADDRESS ="http://i.cs.hku.hk/~xyyang/";
    public static ArrayList<String> pictureNames = new ArrayList<String>();

    private GridView gridView;
    private GridViewAdapter gridViewAdapter;

    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gridView = (GridView) getActivity().findViewById(R.id.gridView_gallery);
        gridViewAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, getData());
        gridView.setAdapter(gridViewAdapter);


        // FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.floatButton_addMore);
        // fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "Add more pictures", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
        //});

    }

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        int j = 0;
        int count = PicturesName.pictures.size();
        while (PicturesName.tag == false){
            j= j + 1;
        }

        count = PicturesName.pictures.size();
        System.out.print("%%%"+count+PicturesName.tag);
        for(int i = 0 ; i < count ;i++){
            imageItems.add(new ImageItem(PicturesName.pictures.get(i),PicturesName.picture_name.get(i)));
        }
        return imageItems;
    }

    public String getJsonPage(String url) {
        HttpURLConnection conn_cshomepage = null;
        final int HTML_BUFFER_SIZE = 2 * 1024 * 1024;
        char htmlBuffer[] = new char[HTML_BUFFER_SIZE];
        try {

            URL url_cshomepage = new URL(url);
            conn_cshomepage = (HttpURLConnection) url_cshomepage.openConnection();
            conn_cshomepage.setInstanceFollowRedirects(true);
            BufferedReader reader_moodle = new BufferedReader(new InputStreamReader(conn_cshomepage.getInputStream()));
            String HTMLSource = ReadBufferedHTML(reader_moodle, htmlBuffer, HTML_BUFFER_SIZE);
            reader_moodle.close();
            System.out.print("###"+HTMLSource);
            return HTMLSource;

        }catch (Exception e){
            return "fail to connection";

        } finally {
            // When HttpClient instance is no longer needed
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            if (conn_cshomepage != null) {
                conn_cshomepage.disconnect();
            }
        }
    }


    public String ReadBufferedHTML(BufferedReader reader, char [] htmlBuffer, int bufSz) throws java.io.IOException {
        htmlBuffer[0] = '\0'; int offset = 0;
        do {
            int cnt = reader.read(htmlBuffer, offset, bufSz - offset); if (cnt > 0) {
                offset += cnt; } else {
                break; }
        } while (true);
        return new String(htmlBuffer);
    }


    private class connect extends AsyncTask<Void,Void,String> {
        boolean success;
        String jsonString;

        @Override
        protected String doInBackground(Void... params) {
            String url = SERVER_ADDRESS + "get_image.php";

            success = true;
            jsonString = getJsonPage(url);
            if (jsonString.equals("Fail to connection")) success = false;
            return null;


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (success){
                parse_JSON_String_and_Switch_Activity(jsonString);
            }else{
                alert( "Error", "Fail to login" );
            }
        }
    }


    protected void alert(String title, String mymessage){ new android.app.AlertDialog.Builder(getContext())
            .setMessage(mymessage)
            .setTitle(title)
            .setCancelable(true) .setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton){}
            } )
            .show();
    }

    public  void parse_JSON_String_and_Switch_Activity(String JSONString) {
        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);
            JSONArray jsonArray = rootJSONObj.optJSONArray("pictures");
            for (int i=0; i<jsonArray.length(); ++i) {
                String studentName = jsonArray.getString(i);
                pictureNames.add(studentName);
                System.out.print("@@@"+studentName+"\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ;

    }









}



