package musicfo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SearchResultsActivity extends AppCompatActivity {

  LinearLayout searchResultsView;
  EventFinder eventFinder;
  String previewURL = "";
  boolean isURLLoading = true;
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
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    // Adds a left margin to the list of artists
    ViewGroup.MarginLayoutParams params =
        (ViewGroup.MarginLayoutParams) expandableArtistList.getLayoutParams();
    params.leftMargin = 100;
    expandableArtistList.setLayoutParams(params);

    final ImageButton toggleable = (ImageButton) eventView.findViewById(R.id.toggleable);

    // Sets the size of the toggle button
    ViewGroup.LayoutParams arrowParams = toggleable.getLayoutParams();
    arrowParams.width = 60;
    arrowParams.height = 60;
    toggleable.setLayoutParams(arrowParams);

    // Set the title's text
    eventTitle.setText(event.replace("(", System.getProperty("line.separator") + "("));
    eventTitle.setTextColor(Color.WHITE);
    eventTitle.setTextSize(18);

    // Add a listener for the toggle
    eventTitle.setOnClickListener(new View.OnClickListener() {
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

      final TextView artistTextView = new TextView(eventView.getContext());

      artistTextView.setText(artist);
      artistTextView.setTextSize(18);
      artistTextView.setTextColor(Color.WHITE);

      artistTextView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String a = ((TextView) v).getText().toString();
          getSpotifyJSON(a);

            System.out.println("uurrl:" + previewURL);



        }
      });

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

  public void toggleViewExpansion(final View view) {
    LinearLayout layout = (LinearLayout) view.getParent();

  }

  //
  // Given an Artist's Name
  // Return a url to a 30s preview of their
  // top track
  //
  public void getSpotifyJSON(String artist) {

    //Searches Spotify's API for the artist

    Ion.with(this)
        .load("https://api.spotify.com/v1/search?q=" + artist.replaceAll(" ", "%20") + "&type=artist")
        .asString()
        .setCallback(new FutureCallback<String>() {
          @Override
          public void onCompleted(Exception e, String result) {

            if (result != null) {
              getPreviewURL(result);
            }
          }

        });
  }

  public void getPreviewURL(String json) {

    try {

      JSONObject spotify_ret1 = new JSONObject(json);
      JSONObject artists = spotify_ret1.getJSONObject("artists");
      JSONArray items = artists.getJSONArray("items");
      JSONObject mostPopularArtist = items.getJSONObject(0);
      String artistID = mostPopularArtist.getString("id");


      Ion.with(this)
          .load("https://api.spotify.com/v1/artists/" + artistID + "/top-tracks?country=US")
          .asString()
          .setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
              if (result != null) {
                parsePreviewURL(result);
              }
            }
          });

    } catch (JSONException e) {
      e.printStackTrace();
      Log.v("vvv", json);
    }
  }

  public void parsePreviewURL(String json) {

    try {

      JSONObject spotify_ret2 = new JSONObject(json);

      JSONArray tracks = spotify_ret2.getJSONArray("tracks");

      JSONObject randomTrack = tracks.getJSONObject(1);

      previewURL = randomTrack.getString("preview_url");
      System.out.println(previewURL);
      isURLLoading = false;

    } catch (JSONException e) {
      System.err.println(e.toString());
    }
  }

  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  public Action getIndexApiAction() {
    Thing object = new Thing.Builder()
        .setName("SearchResults Page") // TODO: Define a title for the content shown.
        // TODO: Make sure this auto-generated URL is correct.
        .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
        .build();
    return new Action.Builder(Action.TYPE_VIEW)
        .setObject(object)
        .setActionStatus(Action.STATUS_TYPE_COMPLETED)
        .build();
  }

  @Override
  public void onStart() {
    super.onStart();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.connect();
    AppIndex.AppIndexApi.start(client, getIndexApiAction());
  }

  @Override
  public void onStop() {
    super.onStop();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    AppIndex.AppIndexApi.end(client, getIndexApiAction());
    client.disconnect();
  }
}
