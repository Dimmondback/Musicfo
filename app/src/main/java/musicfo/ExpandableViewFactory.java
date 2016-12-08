package musicfo;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import android.media.MediaPlayer;

/**
 * This class is designed to create ExpandableViews that are used in the SearchResultsActivity.
 * There are two components, the event's layout and the artist's layout. The event's layout holds
 * many artists' layouts and these can be viewed when the event's layout is expanded.
 */
public final class ExpandableViewFactory {

  private AppCompatActivity activity;
  private ViewGroup parentView;
  private String previewURL = "";
  private boolean isURLLoading = true;

  /**
   * @param activity The activity that will use ExpandableViewFactory.
   * @param parentView The view that the activity will be adding to.
   */
  public ExpandableViewFactory(AppCompatActivity activity, ViewGroup parentView) {
    this.activity = activity;
    this.parentView = parentView;
  }

  /**
   * @param events A list of events and artists contained in a HashMap.
   * This method does not return a view, instead, acts directly upon parentView.
   */
  public void createExpandableView(HashMap<String, HashSet<String>> events) {
    parentView.removeAllViews();
    for (Map.Entry<String, HashSet<String>> entry : events.entrySet()) {
      String artist = entry.getKey();
      HashSet<String> event = entry.getValue();

      parentView.addView(addArtistView(artist, event));
    }
  }

  /**
   * @param event The name of the event.
   * @param  artistList The list of artists played at 'event'.
   * @return Returns an expandable_artist_layout with appropriate name and Spotify link.
   */
  private View addArtistView(String event, HashSet<String> artistList) {
    // Create the view from the xml file.
    LayoutInflater inflater = LayoutInflater.from(activity.getBaseContext());
    LinearLayout eventView =
        (LinearLayout) inflater.inflate(R.layout.expandable_event_layout, parentView, false);

    // Set up the various parts of the expandable view for use.
    TextView eventTitle = (TextView) eventView.findViewById(R.id.event_title);
    final LinearLayout expandableArtistList =
        (LinearLayout) eventView.findViewById(R.id.expandable_artist_list);
    final ImageButton toggleable = (ImageButton) eventView.findViewById(R.id.toggleable);

    // Set the title's text
    eventTitle.setText(event.replace("(", System.getProperty("line.separator") + "("));

    // Add a listener for the toggleable button and title.
    View.OnClickListener listener = new View.OnClickListener() {
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
    };
    eventTitle.setOnClickListener(listener);
    toggleable.setOnClickListener(listener);

    // Add artist names to the expandable section.
    for (String artist : artistList) {
      LinearLayout artistView = (LinearLayout) inflater.inflate(
          R.layout.expandable_artist_layout, expandableArtistList, false);

      // TODO(edao): Integrate playback functionality with this button.
      ImageButton playButton = (ImageButton) artistView.findViewById(R.id.playButton);

      final TextView artistTextView = (TextView) artistView.findViewById(R.id.artist_name);
      artistTextView.setText(artist);

      // TODO(nsaric): What is this click listener for?

      final MediaPlayer mediaPlayer = new MediaPlayer();

      playButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String a = ((TextView)((LinearLayout)v.getParent()).findViewById(R.id.artist_name)).getText().toString();
          getSpotifyJSON(a);

          if(!mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            new Thread(new Runnable() {
              public void run() {
                try {
                  Thread.sleep(1000);
                  System.out.println("url:" + previewURL);

                  mediaPlayer.setDataSource(previewURL);
                  mediaPlayer.prepare();
                  mediaPlayer.start();

                } catch (Exception e) {
                  e.printStackTrace();
                }

              }
            }).start();
          }else{
            mediaPlayer.stop();
          }
        }
      });

      expandableArtistList.addView(artistView);
    }
    return eventView;
  }

  /**
   * @param artist An artist's name.
   * This method will return a url to a 30s preview of the top track of 'artist'.
   */
  public void getSpotifyJSON(String artist) {
    // Searches Spotify's API for the artist
    String url = "https://api.spotify.com/v1/search?q=" + artist.replaceAll(" ", "%20") + "&type=artist";
    Log.v("spot","first:"+url);

    Ion.with(activity)
        .load(url)
        .asString()
        .setCallback(new FutureCallback<String>() {
          @Override
          public void onCompleted(Exception e, String result) {
            Log.v("spot1","result"+result+":"+e);
            if (result != null) {
              getPreviewURL(result);
            }
          }
        })
    ;
  }

  /**
   * @param json JSON result from Spotify API.
   * This method will retrieve the preview URL gathered from the Spotify API.
   */
  public void getPreviewURL(String json) {

    try {
      JSONObject spotify_ret1 = new JSONObject(json);
      JSONObject artists = spotify_ret1.getJSONObject("artists");
      JSONArray items = artists.getJSONArray("items");
      JSONObject mostPopularArtist = items.getJSONObject(0);
      String artistID = mostPopularArtist.getString("id");

      Ion.with(activity)
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
    }
  }

  /**
   * @param json A JSON string to be parsed from the Spotify API.
   * This method will retrieve the parsed preview URL from the Spotify API.
   */
  public void parsePreviewURL(String json) {

    try {
      JSONObject spotify_ret2 = new JSONObject(json);
      JSONArray tracks = spotify_ret2.getJSONArray("tracks");
      JSONObject randomTrack = tracks.getJSONObject(1);

      previewURL = randomTrack.getString("preview_url");

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
        .setName("SearchResults Page") // TODO(edao): Define a title for the content shown.
        // TODO(edao): Make sure this auto-generated URL is correct.
        .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
        .build();

    return new Action.Builder(Action.TYPE_VIEW)
        .setObject(object)
        .setActionStatus(Action.STATUS_TYPE_COMPLETED)
        .build();
  }
}
