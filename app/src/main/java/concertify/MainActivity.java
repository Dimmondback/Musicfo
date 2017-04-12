package concertify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import java.util.HashMap;
import java.util.HashSet;

import musicfo.R;

public class MainActivity extends AppCompatActivity {

  // Create an EventFinder to perform the searching and parsing.
  EventFinder eventFinder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Create the SearchView with "enter to search" enabled.
    final SearchView sv = (SearchView) findViewById(R.id.search_bar);
    if (sv != null) {
      sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
          return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {

          search(findViewById(R.id.search_bar));
          sv.clearFocus();
          return true;
        }
      });
    }

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
  }

  /**
   * @param v The SearchView that calls this method.
   * This method will use EventFinder to search for artists and start the SearchResultActivity.
   */
  public void search(View v) {
    SearchView s = (SearchView) v;
    String searchValue = s.getQuery().toString();

    // Parse artist name into correct format.
    String artist = searchValue.replaceAll(" ", "+");

    // Make api query through EventFinder.
    eventFinder.search(artist, false);
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