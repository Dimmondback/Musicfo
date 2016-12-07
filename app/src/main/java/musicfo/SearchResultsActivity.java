package musicfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {

  LinearLayout searchResultsView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_results);

    searchResultsView = (LinearLayout) findViewById(R.id.search_results);

    if (this.getIntent().getExtras() != null) {
      if (this.getIntent().getExtras().get("search_results") != null) {
        HashMap<String, HashSet<String>> results =
            (HashMap<String, HashSet<String>>) this.getIntent().getExtras().get("search_results");
        addResultsToScreen(results);
      }
    }
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
    String eventText = event + ":\n";
    System.out.println();

    for (String anArtist : artist) {
      eventText += anArtist + "\n";
    }

    textView.setText(eventText);
    textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    textView.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    return textView;
  }
}
