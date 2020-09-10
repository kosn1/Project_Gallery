package gr.uth.displayphotosv2.Activities;

import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import gr.uth.displayphotosv2.Adapters.FullSizeAdapter;
import gr.uth.displayphotosv2.R;

public class OpenImage extends Activity {

    ViewPager viewPager;
    ArrayList<String> images;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);

        if(savedInstanceState==null){

            /*get intent extras, arraylist with the absolute path of each image,
              and the position of the image which was clicked
             */
            Intent intent = getIntent();
            images = intent.getStringArrayListExtra("images");
            position = intent.getIntExtra("position",0);
        }

        //layout manager that provides slide functionality between photos
        viewPager = findViewById(R.id.viewPager);

        //create and set the adapter that will supply views for the viewpager
        FullSizeAdapter fullSizeAdapter = new FullSizeAdapter(this,images);
        viewPager.setAdapter(fullSizeAdapter);

        //set the currently selected image
        viewPager.setCurrentItem(position,true);

    }
}