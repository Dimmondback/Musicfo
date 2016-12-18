package musicfo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This activity is where we display the search results of a search. We use an ExpandableView
 * in order to save space while still allowing the user to view all of the events and artists they
 * would be interested in.
 */
public class SavedEventsActivity extends AppCompatActivity {

  ExpandableViewFactory expandableViewFactory;
  LinearLayout searchResultsView;
  private SQLiteDatabase db = null;

  EventFinder eventFinder;

  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  private GoogleApiClient client;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    eventFinder = new EventFinder(this, new HashMap<String, HashSet<String>>());

    setContentView(R.layout.activity_search_results);

    db = openOrCreateDatabase("events", MODE_PRIVATE, null);


    searchResultsView = (LinearLayout) findViewById(R.id.search_results);
    expandableViewFactory = new ExpandableViewFactory(this, searchResultsView);

    HashMap<String, HashSet<String>> saved_content = new HashMap<>();
    HashSet artists = new HashSet<String>();

    db.execSQL("create table if not exists saved(url text)");
    Cursor cursor = db.rawQuery("select * from saved", null);

    cursor.moveToNext();
    while (!cursor.isAfterLast()) {

      artists.add( cursor.getString(0) );

      cursor.moveToNext();

    }

    cursor.close();

    saved_content.put("Saved Artists",artists);

    expandableViewFactory.createExpandableView(saved_content);
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    final SearchView sv = (SearchView) findViewById(R.id.search_bar_top);
    if (sv != null) {
      sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
          return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
          if (expandableViewFactory.mediaPlayer != null && expandableViewFactory.mediaPlayer.isPlaying()) {
            expandableViewFactory.mediaPlayer.pause();
            expandableViewFactory.mediaPlayer.stop();
            expandableViewFactory.mediaPlayer.release();
            expandableViewFactory.mediaPlayer = null;
          }

          search(findViewById(R.id.search_bar_top));
          sv.clearFocus();
          return true;
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
    eventFinder.search(artist, true);
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
  public void onResume() {
    super.onResume();
    expandableViewFactory.resumeMediaPlayer();
  }

  @Override
  public void onPause() {
    expandableViewFactory.stopMediaPlayer();
    super.onStop();
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

  @Override
  public void onBackPressed() {
    if (expandableViewFactory.mediaPlayer != null && expandableViewFactory.mediaPlayer.isPlaying()) {
      expandableViewFactory.mediaPlayer.pause();
      expandableViewFactory.mediaPlayer.stop();
      expandableViewFactory.mediaPlayer.release();
      expandableViewFactory.mediaPlayer = null;
    }
    super.onBackPressed();
  }
}
