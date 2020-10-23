package gr.uth.displayphotosv2.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

                        displayTagDialog(filePath);

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
    public void displayTagDialog(final String filePath){
        //display tag dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = inflater.inflate(R.layout.tag_dialog, null);
        final AlertDialog alert = builder.setTitle("Set tags")
                .setView(dialogView)
                .show();

        saveTags = dialogView.findViewById(R.id.addTagBtn);
        cancel = dialogView.findViewById(R.id.cancel_btn);
        chipGroup = dialogView.findViewById(R.id.chip_grp);
        currentTagsChipGroup = dialogView.findViewById(R.id.current_tags_chipgroup);
        currentTagsTextView = dialogView.findViewById(R.id.current_tags_txtview);
        tagInput = dialogView.findViewById(R.id.tagInput);

        //load current tags of file
        loadCurrentTags(filePath);

        //set saveTags Button functionality
        saveTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //retrieve the selected and unselected tags
                ArrayList<String> selectedTags = new ArrayList<>();
                ArrayList<String> unselectedTags = new ArrayList<>();
                for(int i=0; i<chipGroup.getChildCount(); i++){
                    Chip chip =(Chip) chipGroup.getChildAt(i);
                    if(chip.isChecked()){
                        selectedTags.add(chip.getText().toString());
                    }else {
                        unselectedTags.add(chip.getText().toString());
                    }
                }

                //retrieve the ID from selected and unselected tags
                ArrayList<Integer> selectedTagsIDs = new ArrayList<>();
                ArrayList<Integer> unselectedTagsIDs = new ArrayList<>();
                for(String s: selectedTags){
                    selectedTagsIDs.add(databaseHelper.getTagId(s));
                }
                for(String s: unselectedTags){
                    unselectedTagsIDs.add(databaseHelper.getTagId(s));
                }

                //add selected tags to the file
                for(Integer i: selectedTagsIDs){

                    if(!databaseHelper.checkTag(i,databaseHelper.getFileID(filePath))){
                        databaseHelper.addTagToFile(i,databaseHelper.getFileID(filePath));
                    }
                }

                //if a tag from a file is now unchecked it is removed from the file
                for(Integer i: unselectedTagsIDs){

                    if(databaseHelper.checkTag(i,databaseHelper.getFileID(filePath))){
                        databaseHelper.deleteTagFromFile(String.valueOf(i));
                    }
                }

                //store input tags
                if(!tagInput.getText().toString().isEmpty()){
                    String[] inputTags = tagInput.getText().toString().split(" ");

                    /*For each tag given by the user, check if it is already added in the database and
                    if not add it. Also check if the tag is already added in this file, so there are
                    no duplicate/same tags in a file*/
                    for(String str : inputTags){

                        if(!databaseHelper.checkTagName(str)){
                            databaseHelper.insertNewTag(str);
                        }
                        if(!databaseHelper.checkTag(databaseHelper.getTagId(str),databaseHelper.getFileID(filePath))){
                            databaseHelper.addTagToFile(databaseHelper.getTagId(str),databaseHelper.getFileID(filePath));
                        }

                    }
                }

                //dismiss the dialog window
                alert.cancel();
            }
        });

        //cancel button, dismisses the dialog window without saving any changes
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });

        /*Initializing a helper object using DatabaseHelper.getInstance(context),
        guarantees that only one database helper will exist
        across the entire application's lifecycle*/
        databaseHelper = DatabaseHelper.getInstance(context);

        //retrieve default tags from database and display them
        Cursor result = databaseHelper.getAllTags();
        final ArrayList<String> tags = new ArrayList<>();
        while (result.moveToNext()){
            tags.add(result.getString(1));
        }
        result.close();
        for(String text : tags) {
            final Chip chip = (Chip) inflater.inflate(R.layout.chip, null, false);
            chip.setText(text);

            /*when a tag changes "check" state in "Tag Group", it should change its state
            in "Current tags" group as well*/
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    
                    /*find the matching tags in the 2 groups(Tag Group,Current tags) and change
                    the state of "Current Tag" group chip according to the state of "Tag Group" chip*/
                    for(int i=0; i<currentTagsChipGroup.getChildCount(); i++){
                        Chip currentTagGroupChip =(Chip) currentTagsChipGroup.getChildAt(i);
                        if(currentTagGroupChip.getText().toString().equals(chip.getText().toString())){
                            currentTagGroupChip.setChecked(chip.isChecked());
                        }
                    }
                }
            });
            chipGroup.addView(chip);

            //find the tags of the file(current tags) and set them checked in Tag Groups
            for(int i=0; i<currentTagsChipGroup.getChildCount(); i++){
                Chip chipTag =(Chip) currentTagsChipGroup.getChildAt(i);
                if(chipTag.getText().toString().equals(chip.getText().toString())){
                    chip.setChecked(true);
                }
            }
        }
    }

    //load the tags of the selected photo/video (current tags)
    public void loadCurrentTags(String filepath) {
        ArrayList<String> currentTagsList= new ArrayList<>();

        //get the IDs of the current tags
        Cursor result = databaseHelper.getTagsOfFile(databaseHelper.getFileID(filepath));
        while (result.moveToNext()){

            //get the name of the current tags
            Cursor tags=databaseHelper.getTag(result.getInt(0));
            while (tags.moveToNext()){
                currentTagsList.add(tags.getString(1));
            }
        }

        /*if the selected photo/video has any tags, display them as chips in currentTagsChipGroup
        and set them checked*/
        if(currentTagsList.size()>0){
            currentTagsTextView.setVisibility(View.GONE);
            currentTagsChipGroup.setVisibility(View.VISIBLE);

            for (String s:currentTagsList){

                final Chip chip = (Chip) inflater.inflate(R.layout.chip, null, false);
                chip.setText(s);
                chip.setChecked(true);
                
                /*when a tag changes "check" state in "Current tags" group, it should change its state
                in "Tag Group" as well*/
                chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    
                    /*find the matching tags in the 2 groups(Tag Group,Current tags) and change
                    the state of "Tag Group" chip according to the state of "Current tags" group chip*/
                        for(int i=0; i<chipGroup.getChildCount(); i++){
                            Chip tagGroupChip =(Chip) chipGroup.getChildAt(i);
                            if(tagGroupChip.getText().toString().equals(chip.getText().toString())){
                                tagGroupChip.setChecked(chip.isChecked());
                            }
                        }
                    }
                });

                currentTagsChipGroup.addView(chip);
            }
        }

    }

}
