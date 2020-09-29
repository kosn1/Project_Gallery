package gr.uth.displayphotosv2.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import gr.uth.displayphotosv2.DatabaseHelper;
import gr.uth.displayphotosv2.R;

public class FullSizeAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> images;
    LayoutInflater inflater;

    Button addTag;
    ChipGroup chipGroup;
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

                        displayTagDialog();
                        addTags(filePath);
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


    //open the tag dialog window and load the default tags
    public void displayTagDialog(){
        //display tag dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = inflater.inflate(R.layout.tag_dialog, null);
        builder.setTitle("Set tags for photo")
                .setView(dialogView)
                .show();

        addTag = dialogView.findViewById(R.id.addTagBtn);
        chipGroup = dialogView.findViewById(R.id.chip_grp);
        //tagInput = dialogView.findViewById(R.id.tagInput);

        /*Initializing a helper object using DatabaseHelper.getInstance(context),
        guarantees that only one database helper will exist
        across the entire application's lifecycle*/
        databaseHelper = DatabaseHelper.getInstance(context);
        Cursor result = databaseHelper.getAllTags();

        //retrieve default tags from database and display them
        final ArrayList<String> tags = new ArrayList<>();
        while (result.moveToNext()){
            tags.add(result.getString(1));
        }
        result.close();
        for(String text : tags) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip, null, false);
            chip.setText(text);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //remove tags when click close
                    chipGroup.removeView(v);
                }
            });
            chipGroup.addView(chip);
        }
    }

    //addTag Button Listener
    public void addTags(final String filePath){
        //set addTag Button functionality
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //retrieve the selected tags
                ArrayList<String> selectedTags = new ArrayList<>();
                for(int i=0; i<chipGroup.getChildCount(); i++){
                    Chip chip =(Chip) chipGroup.getChildAt(i);
                    if(chip.isChecked()){
                        selectedTags.add(chip.getText().toString());
                    }
                }

                //retrieve the ID from the selected tags
                ArrayList<Integer> selectedTagsIDs = new ArrayList<>();
                for(String s: selectedTags){
                    selectedTagsIDs.add(databaseHelper.getTagId(s));
                }

                //add selected tags to the file
                for(Integer i: selectedTagsIDs){

                    if(!databaseHelper.checkTag(i,databaseHelper.getFileID(filePath))){
                        databaseHelper.addTagToFile(i,databaseHelper.getFileID(filePath));
                    }
                }
            }
        });
    }


}
