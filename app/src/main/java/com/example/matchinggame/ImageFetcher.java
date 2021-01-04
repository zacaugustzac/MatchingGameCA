package com.example.matchinggame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImageFetcher {
    private String useragentValue="Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private String linkurl;

    public ImageFetcher(String url){
        this.linkurl=url;
    }

    //public ImageFetcher(){
        //linkurl="https://stocksnap.io/search/celebration+party+fireworks";
        //linkurl="https://stocksnap.io/search/cars";
    //}

    public List<String> extractImage() throws MalformedURLException {
        URL url;
        List<String> dirty=null;

        try {
            url = new URL(linkurl);
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent",useragentValue);
            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            List<String> htmlcontent= new ArrayList<String>();
            while ((inputLine = br.readLine()) != null) {
                htmlcontent.add(inputLine);
            }
            br.close();
            dirty=htmlcontent.stream()
                    .filter(x->x.contains("<img src="))
                    .map(y->clearing(y))
                    .limit(20)
                    .collect(Collectors.toList());
            System.out.println("size="+ dirty.size());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dirty;
    }

    public String clearing(String input){
        return input.split("\"")[1];
    }

    public Bitmap convertImage(String url) throws IOException {
        URL urlimg = new URL(url);
        URLConnection conn = urlimg.openConnection();
        conn.addRequestProperty("User-Agent", useragentValue);
        conn.connect();
        InputStream in = conn.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        in.close();
        return bitmap;

    }

}
