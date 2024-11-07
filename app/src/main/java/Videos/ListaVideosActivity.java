
package Videos;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niko.pruebaurgencias3.R;

import java.util.ArrayList;
import java.util.List;

public class ListaVideosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideosAdapter adapter;
    private List<VideoItem> videoItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_videos);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoItemList = new ArrayList<>();
        videoItemList.add(new VideoItem(R.drawable.uno, "Ataque cardiaco", R.raw.corazon));
        videoItemList.add(new VideoItem(R.drawable.dos, "Atragantamiento", R.raw.atragantamiento));
        videoItemList.add(new VideoItem(R.drawable.tres, "Accidente vehicular", R.raw.accidentevehicular));

        


        adapter = new VideosAdapter(this, videoItemList);
        recyclerView.setAdapter(adapter);
    }
}



