package gr.uth.displayphotosv2;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class LauncherActivity extends AppCompatActivity {

    List<HomeButton> homeButtonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //create Homescreen buttons and store them into an arraylist
        homeButtonList = new ArrayList<>();
        homeButtonList.add(new HomeButton("Photos",R.drawable.photo2));
        homeButtonList.add(new HomeButton("Videos",R.drawable.video3));
        homeButtonList.add(new HomeButton("Albums",R.drawable.album));
        homeButtonList.add(new HomeButton("Search",R.drawable.search));

        RecyclerView rv = findViewById(R.id.buttons_list);

        HomeScreenViewAdapter myAdapter = new HomeScreenViewAdapter(this, homeButtonList);

        //set the layout of the RecyclerView
        rv.setLayoutManager(new GridLayoutManager(this,2));
        //set the HomeScreenViewAdapter to RecyclerView
        rv.setAdapter(myAdapter);
    }
}