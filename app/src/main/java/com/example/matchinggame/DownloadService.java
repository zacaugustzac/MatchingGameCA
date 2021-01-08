package com.example.matchinggame;

import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class DownloadService extends Service {
    public DownloadService() {
    }
    private String useragentValue="Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         super.onStartCommand(intent, flags, startId);
         String action=intent.getAction();
         List<String>imageurl=intent.getStringArrayListExtra("url");
         int x=intent.getIntExtra("index",0);
         if(action.equals("DOWNLOAD")){

             new Thread(new Runnable() {
                 @Override
                 public void run() {
                     String imgurl = imageurl.get(x - 1);
                     imgurl = manualDecode(imgurl);
                     ContextWrapper cw = new ContextWrapper(getApplicationContext());
                     File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                     File file = new File(directory, "image" + x + ".jpg");
                     storeImageInStorage(imgurl, file);
                     Intent intent=new Intent();
                     intent.setAction("completed");
                     intent.putExtra("stringfile",file.toString());
                     intent.putExtra("index",x);
                     sendBroadcast(intent);

                 }
             }).start();

         }
        return START_NOT_STICKY;
    }

    private String manualDecode(String url) {
        if (url.contains("&#x27;")) {
            url = url.replaceAll("&#x27;", "'");
        }
        return url;
    }


    public void storeImageInStorage(String imgurl, File file) {
        Log.d("path", file.toString());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            convertImage(imgurl).compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    public Bitmap convertImage(String url) throws IOException {
        System.out.println(url);
        URL urlimg = new URL(url);
        URLConnection conn = urlimg.openConnection();
        conn.addRequestProperty("User-Agent", useragentValue);
        conn.connect();
        InputStream in = conn.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        in.close();
        return bitmap;

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}