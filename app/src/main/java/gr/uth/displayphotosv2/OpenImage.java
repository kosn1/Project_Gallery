package gr.uth.displayphotosv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class OpenImage extends Activity {

    ImageView fullImage;
    ViewPager viewPager;
    ArrayList<String> images;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);

        if(savedInstanceState==null){

            /*get intent extras, arralist with the absolute path of each image,
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



















        //fullImage = findViewById(R.id.full_image);

        //String path = getIntent().getExtras().getString("image");

        //compress and load image in full size
        //Bitmap bitmapImage = BitmapFactory.decodeFile(path);
        //int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
        //Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
        //fullImage.setImageBitmap(scaled);
    }
}