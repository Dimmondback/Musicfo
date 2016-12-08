/*
package musicfo;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class MusicPlayer extends Activity {
  static final String AUDIO_PATH = "http://yourHost/play.mp3";
  private MediaPlayer mediaPlayer;
  private int playbackPosition = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_results);
  }

  public void doClick(View view) {
    switch (view.getId()) {
      case R.id.startPlayerBtn:
        try {
          playAudio(AUDIO_PATH);
        } catch (Exception e) {
          e.printStackTrace();
        }
        break;
      case R.id.stopPlayerBtn:
        if (mediaPlayer != null) {
          mediaPlayer.stop();
          playbackPosition = 0;
        }
        break;
    }
  }

  private void playAudio(String url) throws Exception {
    killMediaPlayer();

    mediaPlayer = new MediaPlayer();
    mediaPlayer.setDataSource(url);
    mediaPlayer.prepare();
    mediaPlayer.start();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    killMediaPlayer();
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
*/
