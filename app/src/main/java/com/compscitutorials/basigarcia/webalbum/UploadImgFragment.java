package com.compscitutorials.basigarcia.webalbum;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadImgFragment extends Fragment {

    ImageButton btn_camera;
    ImageButton btn_gallery;
    Button btn_uploadImg;
    ImageView img_chooseImg;

    private static final int CAMERA_PIC_REQUEST = 2;
    private static final int RESULT_LOAD_IMAGE =1;
    private static final String SERVER_ADDRESS ="http://i.cs.hku.hk/~xyyang/";

    public UploadImgFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_img, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){

        super.onActivityCreated(savedInstanceState);

        btn_camera = (ImageButton)getActivity().findViewById(R.id.btn_imgfromcamera);
        btn_gallery = (ImageButton)getActivity().findViewById(R.id.btn_imgfromgallery);
        btn_uploadImg = (Button)getActivity().findViewById(R.id.btn_uploadimg);

        img_chooseImg = (ImageView)getActivity().findViewById(R.id.choosed_img);

        btn_gallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
        btn_camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.CAMERA)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_PIC_REQUEST);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_PIC_REQUEST);
            }
        });


        img_chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(new String[]{"Take a picture", "Choose from local gallery"}, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) // 拍照
                        {

                            if (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {

                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                        Manifest.permission.CAMERA)) {

                                    // Show an expanation to the user *asynchronously* -- don't block
                                    // this thread waiting for the user's response! After the user
                                    // sees the explanation, try again to request the permission.

                                } else {

                                    // No explanation needed, we can request the permission.

                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.CAMERA},
                                            CAMERA_PIC_REQUEST);

                                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                    // app-defined int constant. The callback method gets the
                                    // result of the request.
                                }
                            }

                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAMERA_PIC_REQUEST);

                        } else if (which == 1) // 从手机相册选择
                        {

                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                        }
                    }
                });
                Dialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                //Toast.makeText(getActivity(), "Camera", 0).show();

            }
        });


        btn_uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable)img_chooseImg.getDrawable()).getBitmap();
                new uploadImage(bitmap,getCurrentTime()).execute();
                //Toast.makeText(getActivity(), "Upload", 0).show();
                PicturesName.resetPicturesCache();
                new connect().execute();
            }
        });

    }

    // getCurrentTime for 2015-12-12 16:31:29 => 20151212163129
    public String getCurrentTime(){ // format yymmddhhmmss
        Calendar c = Calendar.getInstance();

        //int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        String yymmddhhmmss = Integer.toString(month)
                + Integer.toString(date)
                + Integer.toString(hour)
                + Integer.toString(minute)
                + Integer.toString(second);
        return yymmddhhmmss;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            //2
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            img_chooseImg.setImageBitmap(thumbnail);
            //3
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //4
            File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
            //File file = new File("/storage/sdcard0/Download" + File.separator + "image.jpg");
            //Log.d(TAG, "output filename: " + file.getPath());
            try {
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                //5
                fo.write(bytes.toByteArray());
                fo.close();
                //Log.d(TAG, "save succ");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null){
            //System.out.print("herer\n");
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            requestCode);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
            Uri selectedImage = data.getData();
            img_chooseImg.setImageURI(selectedImage);
        }
    }

    // convert bitmap to String
    private  String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        return encodeImage;
    }

    // set up httpRequestParams
    private HttpParams  getHttpRequestParams(){
        HttpParams httpRequestParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams, 1000 * 30);
        HttpConnectionParams.setSoTimeout(httpRequestParams, 1000 * 30);
        return  httpRequestParams;
    }

    private  class uploadImage extends AsyncTask<Void,Void,Void> {
        Bitmap bitmap;
        String name;

        public uploadImage(Bitmap bitmap, String name){
            this.bitmap = bitmap;
            this.name = name;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String encodeImage = getStringImage(this.bitmap);
            //System.out.print("####"+name);

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("name",this.name));
            dataToSend.add(new BasicNameValuePair("image", encodeImage));

            //System.out.print("####" + name);
            HttpParams httpRequestParams = getHttpRequestParams();
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+"save_image.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null ;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(),"image uploaded",Toast.LENGTH_SHORT).show();
        }


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


    protected void alert(String title, String mymessage){ new android.app.AlertDialog.Builder(getContext())
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

                return BitmapFactory.decodeStream((InputStream) connection.getContent());

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
