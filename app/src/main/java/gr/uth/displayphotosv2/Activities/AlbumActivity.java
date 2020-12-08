package gr.uth.displayphotosv2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import gr.uth.displayphotosv2.Adapters.AlbumViewAdapter;
import gr.uth.displayphotosv2.MediaGallery;
import gr.uth.displayphotosv2.R;

public class AlbumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private static final int READ_PERMISSION_CODE =101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);


        recyclerView = findViewById(R.id.recyclerview_gallery_albums);

        //check permission
        //if permission not granted ask for permission about internal storage, else load albums
        if(ContextCompat.checkSelfPermission(AlbumActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(AlbumActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
        }
        else {
            loadAlbums();
        }
    }

    private void loadAlbums(){

        //set the size and layout of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //album names(keys) and their thumbnails(values) are stored in a HashMap collection
        HashMap<String, String> albumsList = MediaGallery.getListOfAlbums(this);

        //get HashMap with album names(keys) and their sizes(number of items/values)
        HashMap<String, Integer> albumsSize = MediaGallery.getSizeOfAlbums(this);

        /*sort the keys of the 2 hashmaps alphabetically, so we have the same keys(album names)
        in the same order between the 2 Maps*/
        Map<String, String> sortedAlbumsList = new TreeMap<>(albumsList);
        Map<String, Integer> sortedAlbumsSize = new TreeMap<>(albumsSize);

        //get album names(keys) from sorted TreeMap and store them into an ArrayList
        Set<String> keySet = sortedAlbumsList.keySet();
        ArrayList<String> albumsNameList = new ArrayList<> (keySet);

        //get album thumbnails(values) from sorted TreeMap and store them into an ArrayList
        Collection<String> values = sortedAlbumsList.values();
        ArrayList<String> listOfThumbnails = new ArrayList<>(values);

        //get sizes(values) from sorted TreeMap and store them into an ArrayList
        Collection<Integer> valuesSize = sortedAlbumsSize.values();
        ArrayList<Integer> listOfSizes = new ArrayList<>(valuesSize);

        //set the AlbumViewAdapter to RecyclerView
        AlbumViewAdapter albumViewAdapter = new AlbumViewAdapter(this, albumsNameList, listOfThumbnails, listOfSizes);
        recyclerView.setAdapter(albumViewAdapter);


    }

    //handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Read external storage permission granted", Toast.LENGTH_SHORT).show();
                loadAlbums();
            }
            else{
                Toast.makeText(this,"Read external storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}