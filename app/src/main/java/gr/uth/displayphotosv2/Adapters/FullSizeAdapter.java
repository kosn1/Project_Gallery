package gr.uth.displayphotosv2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import gr.uth.displayphotosv2.DatabaseHelper;
import gr.uth.displayphotosv2.Dialogs.DateDialog;
import gr.uth.displayphotosv2.Dialogs.LocationDialog;
import gr.uth.displayphotosv2.R;
import gr.uth.displayphotosv2.Dialogs.TagDialog;

/*The adapter which supplies views for the viewpager*/
public class FullSizeAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> files;
    private final String type;
    LayoutInflater inflater;

    ImageView playArrow;
    ImageView imageView;

    DatabaseHelper databaseHelper;

    /* The constructor of FullSizeAdapter requires 3 parameters:the current context of the app,
    * a list with the absolute paths of the files that are being displayed and the type of the
    * files(photo/video) */
    public FullSizeAdapter(Context context, ArrayList<String> files, String type){
        this.context = context;
        this.files = files;
        this.type = type;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    /*checks whether a page View is associated with a given key object
     When you slide, the ViewPager gets view position from an array
     or instantiates it and compare this view with children of ViewPager(Object)
     The view which equals to the key object is displayed to the user*/
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }


    @NonNull
    @Override
    /*called when ViewPager needs a page to display,
      returns the current view,
      PagerAdapter is considering this view as a key value when viewpager changes a page*/
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //retrieve the LayoutInflater instance that is already hooked up to the current context
        inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //load full_item.xml
        View v = inflater.inflate(R.layout.full_item,null);

        /*if this is a video file set a play button on top of its thumbnail, and a listener
        for this button as well*/
        if(type.equals("video")){
            playArrow = v.findViewById(R.id.video_play_arrow);
            playArrow.setVisibility(View.VISIBLE);
            playVideo(files.get(position));
        }

        //display image/video in full screen
        imageView = v.findViewById(R.id.img);
        Glide.with(context)
                .load(files.get(position))
                .apply(new RequestOptions().centerInside())
                .into(imageView);

        /*Initializing a helper object using DatabaseHelper.getInstance(context),
        guarantees that only one database helper will exist
        across the entire application's lifecycle*/
        databaseHelper = DatabaseHelper.getInstance(context);
        //add file path to database if not exists already
        if(!databaseHelper.checkIfPathExistsAlready(files.get(position))){
            databaseHelper.insertNewFile(files.get(position));
        }

        //set Listener for BottomNavigationView
        BottomNavigationView bottomNavigationView = v.findViewById(R.id.bottom_navigation);
        onNavigationItemClicked(bottomNavigationView, files.get(position));

        //Convert from ViewGroup to Viewpager the containing View in which the page will be shown
        ViewPager vp = (ViewPager) container;
        //add the View to the container
        vp.addView(v,0);
        return v;
    }

    @Override
    // Called when ViewPager no longer needs a page to display.
    // The adapter is responsible to remove the page from the container, which is usually the ViewPager itself.
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);

        ViewPager viewPager = (ViewPager)container;
        View v = (View)object;

        //remove the page(view) from the container
        viewPager.removeView(v);
    }

    //handler for bottom navigation menu
    public void onNavigationItemClicked(BottomNavigationView bottomNavigationView, final String filePath) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.tag:

                        TagDialog tagDialog = new TagDialog(context,inflater);
                        tagDialog.displayTagDialog(filePath);

                        break;

                    case R.id.date:

                        DateDialog dateDialog = new DateDialog(context,inflater);
                        dateDialog.displayDateDialog(filePath);

                        break;

                    case R.id.location:

                        LocationDialog locationDialog = new LocationDialog(context,inflater);
                        locationDialog.displayLocationDialog(filePath);

                        break;
                }
                return true;
            }
        });
    }

    //play the selected video by selecting one of the available apps for playing videos
    public void playVideo(final String filepath){
        playArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(filepath), "video/*");
                context.startActivity(intent);
            }
        });
    }

}
