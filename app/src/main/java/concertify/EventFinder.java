package concertify;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class is designed to save code space and keep searching functions together.
 */
class EventFinder {

  public HashMap<String, HashSet<String>> allEvents;
  private AppCompatActivity activity;

  /**
   * @param activity The activity that will use the EventFinder.
   * @param allEvents The list of events that the EventFinder will use.
   * The primary use of the EventFinder is to provide a tool that will search for events and
   * artists using the Songkick API. Searching will always lead to a SearchResultsActivity screen.
   */
  public EventFinder(AppCompatActivity activity, HashMap<String, HashSet<String>> allEvents) {
    this.activity = activity;
    this.allEvents = allEvents;
  }

  /**
   *
   * @param artist The name of the artist to search for.
   * @param closeActivity Flags whether or not to finish the activity that called search.
   * This method will gather data using the Songkick API and then start a SearchResultActivity (and
   * will close the calling activity if appropriate).
   */
  public void search(String artist, final boolean closeActivity) {
    allEvents = new HashMap<>();
    Ion.with(activity)
        .load("http://api.songkick.com/api/3.0/events.json?apikey=kWvqvn4PIBVxIuqH&artist_name=" + artist)
        .asString()
        .setCallback(new FutureCallback<String>() {
          @Override
          public void onCompleted(Exception e, String result) {
            if (result != null) {
              processSearchData(result);

              // Start the search results activity.
              Intent searchResults = new Intent(activity.getApplicationContext(), SearchResultsActivity.class);
              searchResults.putExtra("search_results", allEvents);
              activity.startActivity(searchResults);
              if (closeActivity) {
                activity.finish();
              }
            }
          }
        });
  }

  /**
   * @param result A result string gathered from the Songkick API to be parsed into a JSON object.
   * This is a behind-the-scenes function that helps process the result string into a JSON object.
   */
  private void processSearchData(String result) {
    try {
      JSONObject json = new JSONObject(result);

      // Prevents duplicate artists with a set.
      if (json.has("resultsPage")) {
        JSONObject resultsPage = json.getJSONObject("resultsPage");
        JSONObject results = resultsPage.getJSONObject("results");
        JSONArray events = results.getJSONArray("event");

        for (int i = 0; i < events.length(); i++) {
          JSONObject event = events.getJSONObject(i);
          JSONArray performers = event.getJSONArray("performance");
          String eventName = event.getString("displayName");

          HashSet<String> artists = new HashSet<>();

          for (int j = 0; j < performers.length(); j++) {
            JSONObject anArtist = performers.getJSONObject(j);
           // Log.v("artist", anArtist.getString("displayName"));

            artists.add(anArtist.getString("displayName"));
          }

          // Add event to hashmap with artists.
          allEvents.put(eventName, artists);
        }
      } else {
        Toast.makeText(activity, "Can't access Songkick", Toast.LENGTH_LONG).show();
      }
     // Log.v("arti", allEvents.toString());
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
