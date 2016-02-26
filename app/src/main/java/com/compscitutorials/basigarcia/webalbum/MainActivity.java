package com.compscitutorials.basigarcia.webalbum;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView = null;
    Toolbar toolbar = null;
    public static ArrayList<String> pictureNames;

    private static final String SERVER_ADDRESS ="http://i.cs.hku.hk/~xyyang/";

    Dialog contactUsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PicturesName.tag = false;
        new connect().execute();

        //Set the fragment initially
        UploadImgFragment fragment = new UploadImgFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // useless floating action button
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
        //});

        contactUsDialog = new AlertDialog.Builder(this)
                .setTitle("Web Photo Album v1.0")
                .setIcon(R.drawable.ic_launcher)
                .setMessage("Author: \n" +
                        "Xiaoyu Lu (lxyu0405@connect.hku.hk)\n" +
                        "XinYong Yang (young13@connect.hku.hk)\n" +
                        "Lu Liu (lulau@connect.hku.hk)\n" +
                        "\n" +
                        "Many thanks to Dr. T.W. Chim and Mr. Niels Y.H. Tsang," +
                        " we have learnt a lot from this course!")
                .setPositiveButton("I like it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            //Set the fragment initially
            UploadImgFragment fragment = new UploadImgFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            //Set the fragment initially
            GalleryFragment fragment = new GalleryFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_send) {
            contactUsDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            System.out.print("###"+HTMLSource+"\n");
            return HTMLSource;

        }catch (Exception e){
            return "fail to connection";

        } finally {
            // When HttpClient instance is no longer needed, // shut down the connection manager to ensure
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


    protected void alert(String title, String mymessage){ new android.app.AlertDialog.Builder(getBaseContext())
            .setMessage(mymessage)
            .setTitle(title)
            .setCancelable(true) .setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton){}
            } )
            .show();
    }

    public  void parse_JSON_String_and_Switch_Activity(String JSONString) {
        String url;
        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);
            JSONArray jsonArray = rootJSONObj.optJSONArray("pictures");
            PicturesName.length = jsonArray.length();
            for (int i=0; i<jsonArray.length(); ++i) {
                String studentName = jsonArray.getString(i);
                PicturesName.picture_name.add(studentName);
                System.out.print("^^^" + studentName + "\n");
                new downLoadImage(studentName,i).execute();
            }

            PicturesName.tag = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ;

    }



    private class downLoadImage extends AsyncTask<Void,Void,Bitmap>{
        String name;
        int i = 0;
        public downLoadImage(String name, int i){
            this.name = name;
            this.i = i;
        }


        @Override
        protected Bitmap doInBackground(Void... params) {
            String url = SERVER_ADDRESS + "pictures/" + name ;

            try{
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream)connection.getContent());

            }catch (Exception e){
                e.printStackTrace();
            }





            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null ){

                PicturesName.pictures.add(bitmap);
                //saveMyBitmap(this.name,bitmap);
                if ( i == PicturesName.length){
                    PicturesName.tag = true;
                }
            }
        }
    }





}
