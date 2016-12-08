package musicfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.HashSet;

public class SearchResultsActivity extends AppCompatActivity {

  ExpandableViewFactory expandableViewFactory;
  LinearLayout searchResultsView;
  EventFinder eventFinder;

  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  private GoogleApiClient client;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_results);

    searchResultsView = (LinearLayout) findViewById(R.id.search_results);
    expandableViewFactory = new ExpandableViewFactory(this, searchResultsView);

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

    expandableViewFactory.createExpandableView(eventFinder.allEvents);
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

  @Override
  public void onStart() {
    super.onStart();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.connect();
    AppIndex.AppIndexApi.start(client, expandableViewFactory.getIndexApiAction());
  }

  @Override
  public void onStop() {
    super.onStop();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    AppIndex.AppIndexApi.end(client, expandableViewFactory.getIndexApiAction());
    client.disconnect();
  }
}
