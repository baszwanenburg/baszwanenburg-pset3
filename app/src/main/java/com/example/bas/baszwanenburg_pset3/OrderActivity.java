package com.example.bas.baszwanenburg_pset3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderActivity extends AppCompatActivity {
    List<String> list = new ArrayList<>();
    Set<String> set = new HashSet<>();
    Integer totalPrice = 0;
    String totalPriceFinal = "";
    TextView textView;
    ListView myList = findViewById(R.id.myList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        final TextView textView = findViewById(R.id.textView);

        loadFromSharedPrefs();

        Intent intent = getIntent();
        String getDish = intent.getStringExtra("dish");
        Integer getPrice = intent.getIntExtra("price", 0);

        list.add("$" + getPrice +  ".00" + "   " + getDish);
        set.addAll(list);
        totalPrice += getPrice;
        totalPriceFinal = "$" + totalPrice + ".00";
        textView.setText("Total price: " + totalPriceFinal);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://resto.mprog.nl/order";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            String responseText = "ETA: " + res.getString("preparation_time") + " minutes";
                            Toast waitingTime = Toast.makeText(getApplicationContext(), responseText, Toast.LENGTH_LONG);
                            waitingTime.show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(postRequest);
    }
    public void buttonClicked (View view) throws IOException {
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }

    public void saveToSharedPrefs(){
        SharedPreferences prefs = this.getSharedPreferences("settings", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putStringSet("set", set);
        editor.putInt("total", totalPrice);
        editor.commit();
    }

    public void loadFromSharedPrefs(){
        SharedPreferences prefs = this.getSharedPreferences("settings", this.MODE_PRIVATE);

        Set setValueRestored = prefs.getStringSet("set", null);
        Integer totalPriceValueRestored = prefs.getInt("total", 0);

        if (setValueRestored != null){
            list = new ArrayList<>(set);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(OrderActivity.this, android.R.layout.simple_list_item_1, list);
            myList.setAdapter(adapter);

            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    list.remove(position);
                    textView.setText("Total price: " + totalPriceFinal);
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetInvalidated();
                    saveToSharedPrefs();
                }
            });
        }

        if (totalPriceValueRestored != null){
            textView.setText(totalPriceValueRestored);
        }
    }
}
