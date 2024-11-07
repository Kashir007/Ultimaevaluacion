package Videos;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.niko.pruebaurgencias3.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private ExoPlayer exoPlayer;
    private StyledPlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);


        playerView = findViewById(R.id.player_view);


        int videoResourceId = getIntent().getIntExtra("videoResourceId", -1);

        if (videoResourceId != -1) {
            // Crear un ExoPlayer y configurarlo
            exoPlayer = new ExoPlayer.Builder(this).build();


            String videoPath = "android.resource://" + getPackageName() + "/" + videoResourceId;
            MediaItem mediaItem = MediaItem.fromUri(videoPath);


            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();


            playerView.setPlayer(exoPlayer);
        } else {
            Toast.makeText(this, "No video resource available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (exoPlayer != null) {
            exoPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }
}
