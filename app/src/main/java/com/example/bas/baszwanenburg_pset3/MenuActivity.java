package com.example.bas.baszwanenburg_pset3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    String category = "";
    String getDish = "";
    ArrayList<String> dish = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        String getCategory = intent.getStringExtra("category");
        category = getCategory;

        final TextView textView = findViewById(R.id.textView);
        final ListView mylist = findViewById(R.id.myList);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://resto.mprog.nl/menu";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONArray arr = object.getJSONArray("items");
                        List<String> list = new ArrayList<>();

                        for (int i = 0; i < arr.length(); i++) {
                            if (arr.getJSONObject(i).getString("category").equals(category)) {
                                dish.add(arr.getJSONObject(i).getString("name"));
                                if (arr.getJSONObject(i).getString("price").length() == 3)
                                    list.add("$" + arr.getJSONObject(i).getString("price") + "0     " + arr.getJSONObject(i).getString("name"));
                                if (arr.getJSONObject(i).getString("price").length() == 4)
                                    list.add("$" + arr.getJSONObject(i).getString("price") + "0   " + arr.getJSONObject(i).getString("name"));
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MenuActivity.this, android.R.layout.simple_list_item_1, list);
                        mylist.setAdapter(adapter);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
            }
        });
        queue.add(stringRequest);

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentInfo = new Intent(MenuActivity.this, InfoActivity.class);
                getDish = dish.get(position);
                intentInfo.putExtra("dish", getDish);
                startActivity(intentInfo);
            }
        });
    }
}