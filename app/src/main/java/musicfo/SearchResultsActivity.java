package musicfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {

  LinearLayout searchResultsView;
  EventFinder eventFinder;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_results);

    searchResultsView = (LinearLayout) findViewById(R.id.search_results);

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

    addResultsToScreen(eventFinder.allEvents);
  }

  private void addResultsToScreen(HashMap<String, HashSet<String>> results) {
    for (Map.Entry<String, HashSet<String>> entry : results.entrySet()) {
      String artist = entry.getKey();
      HashSet<String> event = entry.getValue();

      searchResultsView.addView(addEventView(artist, event));
    }
  }

  private View addEventView(String event, HashSet<String> artist) {
    TextView textView = new TextView(getBaseContext());

    String eventText = event + ":";
    for (String anArtist : artist) {
      eventText += "\n" + anArtist;
    }

    textView.setText(eventText);
    textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    textView.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    return textView;
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
