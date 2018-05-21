package com.example.chintanpatel.networking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    ImageView img;
    final private int REQUEST_INTERNET = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);
        } else {
            downloadSomething();
        }
    }

    private void downloadSomething() {
        String texturl = "http://academic.csuohio.edu/zhao_w/";
        String imgurl = "http://2.bp.blogspot.com/-us_u2PBLSOI/UqgPMh7ovjI/AAAAAAAACdI/ujdyGregs6Y/s1600/amazing-butterfly-hd-image.jpg";

        new DownloadImageTask().execute(imgurl);
        new DownloadTextTask().execute(texturl);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_INTERNET:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadSomething();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode,
                        permissions, grantResults);
        }
    }

    private InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    private InputStream download(String URL) {
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            return in;
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
        return null;
    }

    private Bitmap DownloadImage(String URL) {
        Bitmap bitmap = null;
        InputStream in = download(URL);
        if (in != null) {
            bitmap = BitmapFactory.decodeStream(in);
            try {
                in.close();
            } catch (IOException e1) {
                Log.d("NetworkingActivity", e1.getLocalizedMessage());
            }
        }
        return bitmap;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            return DownloadImage(urls[0]);
        }

        protected void onPostExecute(Bitmap result) {
            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(result);
        }
    }

    private String DownloadText(String URL) {
        int BUFFER_SIZE = 2000;
        InputStream in = download(URL);
        InputStreamReader isr = new InputStreamReader(in);
        int charRead;
        String str = "";
        char[] inputBuffer = new char[BUFFER_SIZE];
        try {
            while ((charRead = isr.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                str += readString;
                inputBuffer = new char[BUFFER_SIZE];
            }
            in.close();
        } catch (IOException e) {
            Log.d("Networking", e.getLocalizedMessage());
            return "";
        }
        return str;
    }

    private class DownloadTextTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return DownloadText(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}




