package gr.uth.displayphotosv2.Dialogs;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import gr.uth.displayphotosv2.DatabaseHelper;
import gr.uth.displayphotosv2.R;

public class TagDialog extends AlertDialog {

    Context context;
    LayoutInflater inflater;

    Button saveTagsBtn;
    Button cancelBtn;
    ChipGroup chipGroup;
    ChipGroup currentTagsChipGroup;
    TextView currentTagsTextView;
    EditText tagInput;

    DatabaseHelper databaseHelper;

    private AlertDialog alert;
    private static final String TITLE = "Set tags";

    public TagDialog(@NonNull Context context, LayoutInflater inflater) {
        super(context);
        this.context = context;
        this.inflater = inflater;

        /*Initializing a helper object using DatabaseHelper.getInstance(context),
        guarantees that only one database helper will exist
        across the entire application's lifecycle*/
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    public void displayTagDialog(String filePath) {
        //display tag dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = inflater.inflate(R.layout.tag_dialog, null);
        alert = builder.setTitle(TITLE)
                .setView(dialogView)
                .show();

        saveTagsBtn = dialogView.findViewById(R.id.addTagBtn);
        cancelBtn = dialogView.findViewById(R.id.cancel_btn);
        chipGroup = dialogView.findViewById(R.id.chip_grp);
        currentTagsChipGroup = dialogView.findViewById(R.id.current_tags_chipgroup);
        currentTagsTextView = dialogView.findViewById(R.id.current_tags_txtview);
        tagInput = dialogView.findViewById(R.id.tagInput);

        //load current tags of file
        loadCurrentTags(filePath);

        //load default tags
        loadDefaultTags();

        //Save Tags Button listener
        saveTags(filePath);

        //cancel button Listener
        cancelBtnOnClick();
    }

    //load the tags of the selected photo/video (current tags)
    public void loadCurrentTags(String filepath) {
        ArrayList<String> currentTagsList= new ArrayList<>();

        //retrieve the current tags of the photo/video
        Cursor result = databaseHelper.getTagsOfFile(databaseHelper.getFileID(filepath));
        while (result.moveToNext()){
            currentTagsList.add(result.getString(1));
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

    //retrieve default tags from database and display them
    public void loadDefaultTags(){

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

    public void saveTags(final String filepath){
        //set saveTags Button functionality
        saveTagsBtn.setOnClickListener(new View.OnClickListener() {
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

                    if(!databaseHelper.checkTag(i,databaseHelper.getFileID(filepath))){
                        databaseHelper.addTagToFile(i,databaseHelper.getFileID(filepath));
                    }
                }

                //if a tag from a file is now unchecked it is removed from the file
                for(Integer i: unselectedTagsIDs){

                    if(databaseHelper.checkTag(i,databaseHelper.getFileID(filepath))){
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
                        if(!databaseHelper.checkTag(databaseHelper.getTagId(str),databaseHelper.getFileID(filepath))){
                            databaseHelper.addTagToFile(databaseHelper.getTagId(str),databaseHelper.getFileID(filepath));
                        }

                    }
                }

                //dismiss the dialog window
                alert.cancel();
            }
        });
    }

    //cancel button, dismisses the dialog window without saving any changes
    public void cancelBtnOnClick(){
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });
    }
}
