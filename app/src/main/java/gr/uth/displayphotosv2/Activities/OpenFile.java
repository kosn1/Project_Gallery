package gr.uth.displayphotosv2.Activities;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import gr.uth.displayphotosv2.Adapters.FullSizeAdapter;
import gr.uth.displayphotosv2.R;

public class OpenFile extends Activity {

    ViewPager viewPager;
    ArrayList<String> images;
    private int position;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);

        if(savedInstanceState==null){

            /*get intent extras, arraylist with the absolute path of each file,
             the position of the file which was clicked and the type of the file(photo/video)
             */
            Intent intent = getIntent();
            images = intent.getStringArrayListExtra("images");
            position = intent.getIntExtra("position",0);
            type = intent.getStringExtra("type");

        //restore data on screen rotation
        }else {
            images = savedInstanceState.getStringArrayList("images");
            position = savedInstanceState.getInt("position");
            type = savedInstanceState.getString("type");
        }

        //layout manager that provides slide functionality between photos
        viewPager = findViewById(R.id.viewPager);

        //create and set the adapter that will supply views for the viewpager
        FullSizeAdapter fullSizeAdapter = new FullSizeAdapter(this,images,type);
        viewPager.setAdapter(fullSizeAdapter);

        //set the currently selected image
        viewPager.setCurrentItem(position,true);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("images",images);
        outState.putInt("position",position);
        outState.putString("type",type);
    }
}