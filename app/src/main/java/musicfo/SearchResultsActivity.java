package musicfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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

  private View addEventView(String event, HashSet<String> artistList) {
    // Create the view from the xml file.
    LayoutInflater inflater = LayoutInflater.from(getBaseContext());
    LinearLayout eventView =
        (LinearLayout) inflater.inflate(R.layout.expandable_layout, searchResultsView, false);

    // Set up the various parts of the expandable view for use.
    TextView eventTitle = (TextView) eventView.findViewById(R.id.event_title);
    final LinearLayout expandableArtistList =
        (LinearLayout) eventView.findViewById(R.id.expandable_artist_list);
    final ImageButton toggleable = (ImageButton) eventView.findViewById(R.id.toggleable);

    // Set the title's text
    eventTitle.setText(event);
    eventTitle.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

    // Add a listener for the toggle
    toggleable.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (expandableArtistList.getVisibility() == View.VISIBLE) {
          toggleable.setBackgroundResource(R.drawable.downarrow);
          expandableArtistList.setVisibility(View.GONE);
          ((View) v.getParent()).invalidate();
        } else {
          toggleable.setBackgroundResource(R.drawable.uparrow);
          expandableArtistList.setVisibility(View.VISIBLE);
          ((View) v.getParent()).invalidate();
        }
      }
    });

    // Add artist names to the expandable section.
    for (String artist : artistList) {
      TextView artistTextView = new TextView(eventView.getContext());
      artistTextView.setText(artist);
      expandableArtistList.addView(artistTextView);
    }

    return eventView;
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
