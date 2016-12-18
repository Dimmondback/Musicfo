
package musicfo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

public class MusicPlayer extends Activity {
  static final String AUDIO_PATH = "http://yourHost/play.mp3";
  private MediaPlayer mediaPlayer;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_results);
  }


  public void playAudio(String url) throws Exception {
    killMediaPlayer();

    mediaPlayer = new MediaPlayer();
    mediaPlayer.setDataSource(url);
    mediaPlayer.prepare();
    mediaPlayer.start();
  }

  @Override
  protected void onDestroy() {
    killMediaPlayer();
    super.onDestroy();
  }

  private void killMediaPlayer() {
    if (mediaPlayer != null) {
      try {
        mediaPlayer.release();
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
  }
}

