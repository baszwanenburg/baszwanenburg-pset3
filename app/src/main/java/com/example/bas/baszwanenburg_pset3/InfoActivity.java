package com.example.bas.baszwanenburg_pset3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class InfoActivity extends AppCompatActivity {
    String dish = "";
    String price = "";
    Integer priceFinal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        String getDish = intent.getStringExtra("dish");
        dish = getDish;

        final TextView recipe = findViewById(R.id.recipe);
        final TextView priceView = findViewById(R.id.price);
        // final ImageView image = findViewById(R.id.image);
        final TextView description = findViewById(R.id.description);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://resto.mprog.nl/menu";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray arr = object.getJSONArray("items");

                            for (int i = 0; i < arr.length(); i++) {
                                if (arr.getJSONObject(i).getString("name").equals(dish)) {
                                    recipe.setText(dish);
                                    price = arr.getJSONObject(i).getString("price");
                                    priceFinal = arr.getJSONObject(i).getInt("price");
                                    priceView.setText("$" + price + "0");
                                    // image.setImageBitmap(arr.getJSONObject(i).getString("image_url"));
                                    description.setText(arr.getJSONObject(i).getString("description"));
                                }
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recipe.setText("That didn't work!");
            }
        });
        queue.add(stringRequest);
    }
    public void buttonClicked (View view) throws IOException {
        Intent intent = new Intent (this, OrderActivity.class);
        intent.putExtra("dish", dish);
        intent.putExtra("price", priceFinal);
        startActivity(intent);
    }
}

