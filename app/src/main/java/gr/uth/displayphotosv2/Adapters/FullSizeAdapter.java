package gr.uth.displayphotosv2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import gr.uth.displayphotosv2.DatabaseHelper;
import gr.uth.displayphotosv2.R;
import gr.uth.displayphotosv2.Dialogs.TagDialog;

public class FullSizeAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> images;
    LayoutInflater inflater;

    Button saveTags;
    Button cancel;
    ChipGroup chipGroup;
    ChipGroup currentTagsChipGroup;
    TextView currentTagsTextView;
    EditText tagInput;

    DatabaseHelper databaseHelper;

    public FullSizeAdapter(Context context, ArrayList<String> images){
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
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

        //display image in full screen
        ImageView imageView = v.findViewById(R.id.img);
        Glide.with(context)
                .load(images.get(position))
                .apply(new RequestOptions().centerInside())
                .into(imageView);

        /*Initializing a helper object using DatabaseHelper.getInstance(context),
        guarantees that only one database helper will exist
        across the entire application's lifecycle*/
        databaseHelper = DatabaseHelper.getInstance(context);
        //add file path to database if not exists already
        if(!databaseHelper.checkIfPathExistsAlready(images.get(position))){
            databaseHelper.insertNewFile(images.get(position));
        }

        //set Listener for BottomNavigationView
        BottomNavigationView bottomNavigationView = v.findViewById(R.id.bottom_navigation);
        onNavigationItemClicked(bottomNavigationView,images.get(position));

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

                        System.out.println("Date");
                        break;

                    case R.id.location:

                        System.out.println("Location");
                        break;
                }
                return true;
            }
        });
    }

}
