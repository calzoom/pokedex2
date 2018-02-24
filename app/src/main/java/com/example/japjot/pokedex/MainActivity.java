package com.example.japjot.pokedex;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    final static String BASEURL= "https://pokeapi.co/api/v2/pokemon/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                (new FetchPokemonTest(query.toLowerCase())).execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    class FetchPokemonTest extends AsyncTask<Void, Void, JSONObject>{
        String pokemonName;

        FetchPokemonTest(String pokemonName){
            this.pokemonName = pokemonName;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL(BASEURL+pokemonName);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream in = new BufferedInputStream(conn.getInputStream());
                String response = convertStreamToString(in);
                return new JSONObject(response);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(jsonObject != null){
                TextView name, attack, defense, spatk, spdef, hp, speed;
                ImageView imageView;

                name = findViewById(R.id.textView3);
                attack = findViewById(R.id.textView9);
                defense = findViewById(R.id.textView);
                hp = findViewById(R.id.textView7);
                spatk = findViewById(R.id.textView5);
                spdef = findViewById(R.id.textView6);
                speed = findViewById(R.id.textView8);
                imageView = findViewById(R.id.imageView);

                try {
                    name.setText((jsonObject.getString("name")).toUpperCase());

                    JSONArray stats = jsonObject.getJSONArray("stats");

                    speed.setText("Speed: " + stats.getJSONObject(0).getInt("base_stat"));
                    spdef.setText("Special Defense: " + stats.getJSONObject(1).getInt("base_stat"));
                    spatk.setText("Special Attack: " + stats.getJSONObject(2).getInt("base_stat"));
                    defense.setText("Defense: " + stats.getJSONObject(3).getInt("base_stat"));
                    attack.setText("Attack: " + stats.getJSONObject(4).getInt("base_stat"));
                    hp.setText("HP: " + stats.getJSONObject(5).getInt("base_stat"));
                    String imageUrl = jsonObject.getJSONObject("sprites").getString("front_default");
                    Glide.with(MainActivity.this).load(imageUrl).into(imageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("http connection", e.getMessage());
                }
            }
            else{
                Toast.makeText(MainActivity.this, "not a pokemon", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
