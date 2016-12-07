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

import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    // Create an EventFinder to perform the searching and parsing.
    EventFinder eventFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create EventFinder from previous material if it exists.
        if (savedInstanceState != null || this.getIntent().getExtras() != null) {
            if (savedInstanceState != null && savedInstanceState.get("search_results") != null) {
                eventFinder = new EventFinder(this,
                    (HashMap<String, HashSet<String>>) savedInstanceState.get("search_results"));
            } else if (getIntent().getExtras().get("search_results") != null) {
                eventFinder = new EventFinder(this, (HashMap<String, HashSet<String>>) getIntent()
                    .getExtras().get("search_results"));
            } else {
                eventFinder = new EventFinder(this, new HashMap<String, HashSet<String>>());
            }
        } else {
            eventFinder = new EventFinder(this, new HashMap<String, HashSet<String>>());
        }

        SearchView searchbar = (SearchView) findViewById(R.id.search_bar);
        if (searchbar != null) {
            searchbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search(v);
                }
            });
        }
    }

    public void search(View v) {
        SearchView s = (SearchView) v;
        String searchValue = s.getQuery().toString();

        // Parse artist name into correct format.
        String artist = searchValue.replaceAll(" ", "+");

        // Make api query through EventFinder.
        eventFinder.search(artist);
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