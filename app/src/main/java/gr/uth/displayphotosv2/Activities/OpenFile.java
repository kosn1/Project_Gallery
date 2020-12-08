package gr.uth.displayphotosv2.Activities;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import gr.uth.displayphotosv2.Adapters.FullSizeAdapter;
import gr.uth.displayphotosv2.File;
import gr.uth.displayphotosv2.R;

public class OpenFile extends Activity {

    ViewPager viewPager;
    ArrayList<File> files;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);

        if(savedInstanceState==null){

            /*get intent extras, arraylist with File items and the position of the file
            which was clicked*/
            Intent intent = getIntent();
            files = (ArrayList<File>) intent.getSerializableExtra("files");
            position = intent.getIntExtra("position",0);


        //restore data on screen rotation
        }else {
            files = (ArrayList<File>)savedInstanceState.getSerializable("files");
            position = savedInstanceState.getInt("position");
        }

        //layout manager that provides slide functionality between photos
        viewPager = findViewById(R.id.viewPager);

        //create and set the adapter that will supply views for the viewpager
        FullSizeAdapter fullSizeAdapter = new FullSizeAdapter(this, files);
        viewPager.setAdapter(fullSizeAdapter);

        //set the currently selected image
        viewPager.setCurrentItem(position,true);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("files", files);
        outState.putInt("position",position);
    }
}