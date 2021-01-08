package com.example.matchinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class ScoreActivity extends AppCompatActivity {
    private Button mback;

    private ListView listing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        ScoreAdapter adp = new ScoreAdapter(this, 0);

            RequestQueue queue = Volley.newRequestQueue(this);
            String url="http://10.0.2.2:8080/api/leaderboard/top";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println("Response is: "+ response.toString());
                            JSONArray result= null;
                            try {
                                result = new JSONArray(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            adp.setData(result);
                            Toast.makeText(ScoreActivity.this,"retrieved successfully",Toast.LENGTH_SHORT).show();
                        }
                    },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.getMessage());
                    Toast.makeText(ScoreActivity.this, "Something wrong happens", Toast.LENGTH_SHORT).show();
                }

            });
            queue.add(stringRequest);

        listing = findViewById(R.id.toplist);
        if (listing != null) {
            listing.setAdapter(adp);
        }


        mback = findViewById(R.id.back);
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}





