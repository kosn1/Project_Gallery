package gr.uth.displayphotosv2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import gr.uth.displayphotosv2.Adapters.HomeScreenViewAdapter;
import gr.uth.displayphotosv2.DatabaseHelper;
import gr.uth.displayphotosv2.HomeButton;
import gr.uth.displayphotosv2.R;

public class LauncherActivity extends AppCompatActivity {

    List<HomeButton> homeButtonList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //create Homescreen buttons and store them into an arraylist
        homeButtonList = new ArrayList<>();
        homeButtonList.add(new HomeButton("Photos",R.drawable.photo2));
        homeButtonList.add(new HomeButton("Videos",R.drawable.video2));
        homeButtonList.add(new HomeButton("Albums",R.drawable.album2));
        homeButtonList.add(new HomeButton("Search",R.drawable.search));

        RecyclerView rv = findViewById(R.id.buttons_list);

        HomeScreenViewAdapter myAdapter = new HomeScreenViewAdapter(this, homeButtonList);

        //set the layout of the RecyclerView
        rv.setLayoutManager(new GridLayoutManager(this,2));
        //set the HomeScreenViewAdapter to RecyclerView
        rv.setAdapter(myAdapter);
    }

    /*When the LauncherActivity starts or is resumed, check for any changes in the user's files.
    * If a file has been deleted from the phone storage, then it should be deleted from the
    * application's database as well.*/
    @Override
    protected void onResume() {
        super.onResume();
        databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        Cursor result = databaseHelper.getAllFiles();
        while (result.moveToNext()){
            java.io.File file = new java.io.File(result.getString(1));
            if (!file.exists()){
                databaseHelper.deleteFile(String.valueOf(result.getInt(0)));
            }
        }
        result.close();
    }
}