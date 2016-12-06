package musicfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {

  ScrollView searchResultsView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_results);

    searchResultsView = (ScrollView) findViewById(R.id.search_results);

    if (savedInstanceState != null) {
      System.out.println("savedInstanceState is not null.");
      if (savedInstanceState.get("search_results") != null) {
        System.out.println("get search_results is not null.");
        HashMap<String, HashSet<String>> results =
            (HashMap<String, HashSet<String>>) savedInstanceState.get("search_results");
        addResultsToScreen(results);
      }
    }
  }

  private void addResultsToScreen(HashMap<String, HashSet<String>> results) {
    for (Map.Entry<String, HashSet<String>> entry : results.entrySet()) {
      String artist = entry.getKey();
      HashSet<String> event = entry.getValue();

////////////////////////////
      System.out.println("artist: " + artist);
      for(String anEvent : event) {
        System.out.println("anEvent: " + anEvent);
      }
///////////////////////////

      searchResultsView.addView(addEventView(artist, event));
    }
  }

  private View addEventView(String artist, HashSet<String> event) {
    LinearLayout layout = new LinearLayout(getBaseContext());
    layout.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    layout.setPadding((int) getResources().getDimension(R.dimen.side_padding), 0,
        (int) getResources().getDimension(R.dimen.side_padding), 0);

    TextView textView = new TextView(getBaseContext());
    String eventText = artist + ":\n";

    for (String anEvent : event) {
      eventText += anEvent + "\n";
    }

    textView.setText(eventText);
    layout.addView(textView);
    return layout;
  }
}
