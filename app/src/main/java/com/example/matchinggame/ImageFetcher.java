package com.example.matchinggame;

import java.io.BufferedReader;
import java.io.IOException;

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

    public ImageFetcher() {

    }


    public List<String> extractImage(){
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
                    .filter(x->x.contains("<img src=\"http"))
                    .map(y->clearing(y))
                    .limit(20)
                    .collect(Collectors.toList());

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
}
