package musicfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SearchView searchbar = (SearchView) findViewById(R.id.search_bar);
        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(v);
            }
        });
    }

    public void search(View v) {
        Toast.makeText(this, "Can't access songkick", Toast.LENGTH_LONG);

        SearchView s = (SearchView) v;
        String searchValue = s.getQuery().toString();


        //parse artist name into correct format
        String artist = searchValue.replaceAll(" ", "+");


        //make api query
        Ion.with(this)
                .load("http://api.songkick.com/api/3.0/events.json?apikey=kWvqvn4PIBVxIuqH&artist_name=" + artist)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {

                        processSearchData(result);
                    }
                });

        //
        //
        //TODO: either change the app dynamically
        // and display search results or start a
        // new intent with the results
        //
        // allEvents contains the data!
        //


    }

    //Concert first parameter, followed by all artists
    HashMap<String, HashSet<String>> allEvents = new HashMap<>();


    private void processSearchData(String result) {

        try {
            JSONObject json = new JSONObject(result);

            //prevents duplicate artists with a set
            HashSet<String> artists = new HashSet<>();
            if (json.has("resultsPage")) {

                JSONObject resultsPage = json.getJSONObject("resultsPage");
                JSONObject results = resultsPage.getJSONObject("results");
                JSONArray events = results.getJSONArray("event");

                for (int i = 0; i < events.length(); i++) {

                    JSONObject event = events.getJSONObject(i);
                    JSONArray performers = event.getJSONArray("performance");
                    String eventName = event.getString("displayName");
                    for (int j = 0; j < performers.length(); j++) {

                        JSONObject anArtist = performers.getJSONObject(j);
                        artists.add(anArtist.getString("displayName"));
                    }

                    //add event to hashmap with artists
                    allEvents.put(eventName, artists);

                    //clear artists for next event
                    artists.clear();
                }
            } else {
                Toast.makeText(this, "Can't access songkick", Toast.LENGTH_LONG);
            }

            Log.v("arti", allEvents.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_saved_events:
                Intent savedEvents = new Intent(getApplicationContext(), SavedEventsActivity.class);
                startActivity(savedEvents);
                return true;
            case R.id.menu_settings:
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}