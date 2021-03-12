package gr.uth.displayphotosv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;

import gr.uth.displayphotosv2.Adapters.GalleryAdapter;
import gr.uth.displayphotosv2.Dialogs.DateDialog;
import gr.uth.displayphotosv2.Dialogs.LocationDialog;
import gr.uth.displayphotosv2.Dialogs.TagDialog;


/*Helper class which handles the functionality of selecting and managing multiple files
  simultaneously.*/
public class SelectionManager {

    //a list which stores the selected(checked) files
    private ArrayList<File> selectedFiles = new ArrayList<>();

    //counter of selected files
    private int counter = 0;

    public int position = -1;
    public boolean isActionMode = false;
    private TextView toolbarTextView;
    private Toolbar toolbar;
    private ImageButton backBtn;
    private GalleryAdapter galleryAdapter;
    LayoutInflater inflater;
    Context context;

    private ArrayList<File> files;

    public SelectionManager(Context context, TextView toolbarTextView, Toolbar toolbar,ImageButton backBtn, LayoutInflater inflater) {
        this.context = context;
        this.toolbarTextView = toolbarTextView;
        this.toolbar = toolbar;
        this.backBtn = backBtn;
        this.inflater = inflater;
    }

    //set the GalleryAdapter (RecyclerView), in which the selection mode will take place
    public void setGalleryAdapter(GalleryAdapter galleryAdapter) {
        this.galleryAdapter = galleryAdapter;
    }

    //set the list which contains all the files that are being displayed by the recyclerview
    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    /*Set Action Mode on (enables selection mode). It is triggered when a file from the
     recyclerview has been clicked and held. While action mode is on, user can select
     multiple files and perform certain operations on them. This function displays a toolbar
     with the proper menu and handles its functionality.*/
    public void startSelection(int index){
        if(!isActionMode){
            isActionMode=true;
            selectedFiles.add(files.get(index));
            files.get(index).setSelected(true);
            counter++;
            updateToolbarText(counter);
            toolbar.setVisibility(View.VISIBLE);
            toolbar.inflateMenu(R.menu.bottom_navigation_menu);

            //a listener which responds to menu item click events
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if(item.getItemId()==R.id.tag && selectedFiles.size()>0){
                        if(selectedFiles.size()==1){
                            TagDialog tagDialog = new TagDialog(context,inflater);
                            tagDialog.displayTagDialog(selectedFiles.get(0).getPath());
                        }else {
                            TagDialog tagDialog = new TagDialog(context,inflater);
                            tagDialog.displayTagDialog(selectedFiles);
                        }

                    }else if(item.getItemId()==R.id.date && selectedFiles.size()>0){
                        if(selectedFiles.size()==1){
                            DateDialog dateDialog = new DateDialog(context,inflater);
                            dateDialog.displayDateDialog(selectedFiles.get(0).getPath());
                        }else {
                            DateDialog dateDialog = new DateDialog(context,inflater);
                            dateDialog.displayDateDialog(selectedFiles);
                        }

                    }else if(item.getItemId()==R.id.location && selectedFiles.size()>0){
                        if(selectedFiles.size()==1){
                            LocationDialog locationDialog = new LocationDialog(context,inflater);
                            locationDialog.displayLocationDialog(selectedFiles.get(0).getPath());
                        }else {
                            LocationDialog locationDialog = new LocationDialog(context,inflater);
                            locationDialog.displayLocationDialog(selectedFiles);
                        }

                    }
                    return true;
                }
            });

            //toolbar's back button  listener
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearActionMode();
                }
            });

            /*keep the position of the first checked item to prevent unexpected behavior
            by notifyDataSetChanged() call.*/
            position=index;
            galleryAdapter.notifyDataSetChanged();
        }
    }

    //update toolbar's TextView dynamically based on how many items are selected.
    private void updateToolbarText(int counter) {
        if (counter == 0){
            toolbarTextView.setText("0 items selected");
        }else if(counter == 1){
            toolbarTextView.setText("1 item selected");
        }else {
            toolbarTextView.setText(counter+" items selected");
        }
    }


    //add/remove files from the list depending on their checked state
    public void check(View v, int position) {
        if(((CheckBox)v).isChecked()){
            selectedFiles.add(files.get(position));
            counter++;
        }else {
            selectedFiles.remove(files.get(position));
            counter--;
        }
        updateToolbarText(counter);
    }

    /*Sets action mode off (disables selection mode). Removes the toolbar from the screen,
    * sets all Files to unselected state and updates the adapter.*/
    public void clearActionMode() {

        isActionMode = false;
        toolbarTextView.setText("0 item selected");
        toolbar.setVisibility(View.GONE);
        counter=0;
        for(File sf: selectedFiles){
            sf.setSelected(false);
        }
        selectedFiles.clear();
        toolbar.getMenu().clear();
        galleryAdapter.notifyDataSetChanged();

    }

}
