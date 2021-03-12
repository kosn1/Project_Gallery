package gr.uth.displayphotosv2.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import gr.uth.displayphotosv2.Adapters.GalleryAdapter;
import gr.uth.displayphotosv2.DatabaseHelper;
import gr.uth.displayphotosv2.Dialogs.DateRangeDialog;
import gr.uth.displayphotosv2.File;
import gr.uth.displayphotosv2.Interfaces.MediaListener;
import gr.uth.displayphotosv2.R;
import gr.uth.displayphotosv2.SelectionManager;
import gr.uth.displayphotosv2.Type;

/*An activity that provides a search box for the user to enter a search query.
  Displays a list of files suggestions based on the user's input. The search results,
  are based on the tags, the date and the locations of the files.*/

public class SearchActivity extends Activity {

    private RecyclerView searchList;
    private GalleryAdapter adapterSearchList;

    private ImageView searchNoResultsImage;
    private TextView searchNoResultsText,numberOfFiles,searchNoMatchText;
    private TabLayout tabLayout;
    private SelectionManager selectionManager;
    private ImageButton backBtn;
    LayoutInflater inflater;

    //flag for date range filter
    private boolean dateRangeOn = false;

    TextView dateRangePlaceholder,reset;
    EditText searchText;
    String searchString="";

    ArrayList<File> imageResultFiles;
    ArrayList<File> videoResultFiles;
    ArrayList<File> allResultFiles;
    ArrayList<File> fileListDateRangeResults;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /*Initializing a helper object using DatabaseHelper.getInstance(context),
        guarantees that only one database helper will exist
        across the entire application's lifecycle*/
        databaseHelper = DatabaseHelper.getInstance(this);

        searchList = findViewById(R.id.search_list);
        searchNoResultsImage = findViewById(R.id.image_magn_glass);
        searchNoResultsText = findViewById(R.id.search_no_results_text);
        searchNoMatchText = findViewById(R.id.search_no_match_text);
        numberOfFiles = findViewById(R.id.files_number);
        tabLayout = findViewById(R.id.tabLayout);
        dateRangePlaceholder = findViewById(R.id.daterange_txtview);
        reset = findViewById(R.id.resetBtn);
        backBtn =findViewById(R.id.btnBack);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTextView = findViewById(R.id.text_toolbar);
        setActionBar(toolbar);
        inflater =  (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectionManager = new SelectionManager(this, toolbarTextView, toolbar, backBtn, inflater);

        /*separate lists in order to filter the results to 3 categories bases on the file type.
         all,image only(if any) and video only(if any)*/
        allResultFiles = new ArrayList<>();
        imageResultFiles = new ArrayList<>();
        videoResultFiles = new ArrayList<>();

        //this list will store all the files within the date range filter(if has been set)
        fileListDateRangeResults = new ArrayList<>();

        //Search EditText
        searchText = findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchString = s.toString().trim();
                onQueryTextChange(searchString);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    /*This method is called when the text in the search box is changed.
      Takes as parameter the text from the search box and based on that,
      fetches the appropriate results from the database. */
    public void onQueryTextChange(String queryText) {

        imageResultFiles.clear();
        videoResultFiles.clear();
        //hide toolbar (if ActionMode is on)
        try {
            selectionManager.clearActionMode();
        }catch (Exception e){
            e.printStackTrace();
        }
        //if the text from the search box is not empty
        if (!queryText.isEmpty()) {

            //get the tags and the locations of all files
            Cursor cursorTag = databaseHelper.getTagsOfFiles();
            ArrayList<String> tags = new ArrayList<>();
            while (cursorTag.moveToNext()) {
                tags.add(databaseHelper.getTagName(cursorTag.getInt(0)));
            }
            cursorTag.close();

            Cursor cursorLocation = databaseHelper.getLocations();
            ArrayList<String> locations = new ArrayList<>();
            while (cursorLocation.moveToNext()){
                locations.add(cursorLocation.getString(0));
            }
            cursorLocation.close();

            //a list which will store the results of the search
            ArrayList<File> fileListResults = new ArrayList<>();

            //search for files by matching tags
            /*split the search box text by space(" ") to get all the words of the query
             and search them for matches*/
            String[] searchStringSplit = queryText.split("\\s+");
            for (String str : tags) {
                for(String strSplit : searchStringSplit) {
                    /*if a matching tag is found, get from database the files which have this tag.
                     * Add the files to the list with the search results. Also add each file to
                     * the appropriate list based on their type(image/video). Check for duplicates,
                     * so the same files are not added twice in those lists. If the user has set
                     * a date range filter to the search results, fetch only the files that apply
                     * to the filter.*/
                    if (str.toLowerCase().contains(strSplit.toLowerCase())) {
                        Cursor cursor = databaseHelper.getFilesFromTag(databaseHelper.getTagId(str));
                        while (cursor.moveToNext()) {
                            boolean flagFile = false;
                            File file = new File(cursor.getString(1), Type.valueOf(cursor.getString(2)));
                            for (File f : fileListResults) {
                                if (f.getPath().equals(file.getPath())) {
                                    flagFile = true;
                                    break;
                                }
                            }
                            if (!flagFile) {
                                //check for date range filter
                                if(dateRangeOn){
                                    for(File f:fileListDateRangeResults){
                                        if (f.getPath().equals(file.getPath())) {
                                            fileListResults.add(file);
                                            checkFileType(file);
                                            break;
                                        }
                                    }
                                }else {
                                    fileListResults.add(file);
                                    checkFileType(file);
                                }
                            }
                        }
                        cursor.close();
                    }
                }
            }
            //search for files by matching locations
            for(String str: locations){
                for(String strSplit : searchStringSplit) {
                    /*if a matching location is found, get from database the files which have this
                     *location. Add the files to the list with the search results. Also add each file to
                     * the appropriate list based on their type(image/video). Check for duplicates,
                     * so the same files are not added twice in those lists. If the user has set
                     * a date range filter to the search results, fetch only the files that apply
                     * to the filter.*/
                    if (str.toLowerCase().contains(strSplit.toLowerCase())) {
                        Cursor cursor = databaseHelper.getFilesFromLocation(str);
                        while (cursor.moveToNext()) {
                            boolean flagFile = false;
                            File file = new File(cursor.getString(0), Type.valueOf(cursor.getString(1)));
                            for (File f : fileListResults) {
                                if (f.getPath().equals(file.getPath())) {
                                    flagFile = true;
                                    break;
                                }
                            }
                            if (!flagFile) {
                                //check for date range filter
                                if(dateRangeOn){
                                    for(File f:fileListDateRangeResults){
                                        if (f.getPath().equals(file.getPath())) {
                                            fileListResults.add(file);
                                            checkFileType(file);
                                            break;
                                        }
                                    }
                                }else {
                                    fileListResults.add(file);
                                    checkFileType(file);
                                }
                            }
                        }
                        cursor.close();
                    }
                }
            }
            displayResults(fileListResults);


        /*if the text from the search box is empty, check the list with the files within the date
          range. If the list is empty means that there is no date range filter set, so there are no
          results/files to be displayed. If it is not empty display the files from this list*/
        }else {
            if(fileListDateRangeResults.size() == 0){
                displayNoResultsLayout();
            }else {
               // add each file to the appropriate list based on their type(image/video)
                for(File f:fileListDateRangeResults){
                    checkFileType(f);
                }
                displayResults(fileListDateRangeResults);
            }

        }
    }

     /*display the results to the user.Takes as parameter a list which contains the results
       of the search*/
    public void displayResults(final ArrayList<File> fileListResults){
        //copy fileListResults to allResultFiles
        allResultFiles.clear();
        allResultFiles.addAll(fileListResults);

        //set the size and layout of the RecyclerView
        searchList.setHasFixedSize(true);
        searchList.setLayoutManager(new GridLayoutManager(this,3));

        //onClick file listener
        final MediaListener listener = new MediaListener() {
            @Override
            public void onClick(View view, int position) {

                //prepare the Intent for OpenFile activity
                Intent intent = new Intent(getApplicationContext(), OpenFile.class);
                intent.putExtra("files", fileListResults);
                intent.putExtra("position",position);
                //start new Activity
                startActivity(intent);
            }

            //on long click set the SelectionManager properly
            @Override
            public void onItemLongClick(View view, int position) {
                selectionManager.setGalleryAdapter(adapterSearchList);
                selectionManager.setFiles(fileListResults);
                selectionManager.startSelection(position);
            }
        };

        //create the adapter of RecyclerView
        adapterSearchList = new GalleryAdapter(this, fileListResults, listener,selectionManager);

        //if search has no results display the appropriate layout
        if (adapterSearchList.getItemCount()==0) {
            displayNoMatchLayout();
            return;
        }

        //change the layout to display the results
        searchNoResultsText.setVisibility(View.GONE);
        searchNoMatchText.setVisibility(View.GONE);
        searchNoResultsImage.setVisibility(View.GONE);

        //set the GalleryAdapter to RecyclerView
        searchList.setAdapter(adapterSearchList);
        searchList.setVisibility(View.VISIBLE);

        //Number of files textview
        numberOfFiles.setVisibility(View.VISIBLE);
        numberOfFiles.setText(adapterSearchList.getItemCount()+" Files");

        setTabLayout();

    }


    /*tab layout functionality
     * 3 tabs("All", "Images", "Videos") which act as filters for the files
     * Set "All" tab as default selected tab
     * "Images" and "Videos" tabs are clickable only if they have files to display*/
    public void setTabLayout(){
        tabLayout.setVisibility(View.VISIBLE);
        tabLayout.getTabAt(0).select();
        tabLayout.getTabAt(1).view.setClickable(true);
        tabLayout.getTabAt(2).view.setClickable(true);
        if(imageResultFiles.isEmpty()){
            tabLayout.getTabAt(1).view.setClickable(false);
        }else if(videoResultFiles.isEmpty()){
            tabLayout.getTabAt(2).view.setClickable(false);
        }

        //on tab selected, update the adapter accordingly in order to display the proper files
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                //hide toolbar (if ActionMode is on) when tab selection changes
                try {
                    selectionManager.clearActionMode();
                }catch (Exception e){
                    e.printStackTrace();
                }

                //"All" tab
                if(position==0){
                    adapterSearchList.setFiles(allResultFiles);
                    final MediaListener listener = new MediaListener() {
                        @Override
                        public void onClick(View view, int position) {

                            //prepare the Intent for OpenFile activity
                            Intent intent = new Intent(getApplicationContext(), OpenFile.class);
                            intent.putExtra("files", allResultFiles);
                            intent.putExtra("position",position);
                            //start new Activity
                            startActivity(intent);
                        }

                        //on long click set the SelectionManager properly
                        @Override
                        public void onItemLongClick(View view, int position) {
                            selectionManager.setGalleryAdapter(adapterSearchList);
                            selectionManager.setFiles(allResultFiles);
                            selectionManager.startSelection(position);
                        }
                    };
                    adapterSearchList.setClickListener(listener);
                    adapterSearchList.notifyDataSetChanged();
                    numberOfFiles.setText(adapterSearchList.getItemCount()+" Files");

                //"Images" tab
                }else if(position==1){
                    adapterSearchList.setFiles(imageResultFiles);
                    final MediaListener listener = new MediaListener() {
                        @Override
                        public void onClick(View view, int position) {

                            //prepare the Intent for OpenFile activity
                            Intent intent = new Intent(getApplicationContext(), OpenFile.class);
                            intent.putExtra("files", imageResultFiles);
                            intent.putExtra("position",position);
                            //start new Activity
                            startActivity(intent);
                        }

                        //on long click set the SelectionManager properly
                        @Override
                        public void onItemLongClick(View view, int position) {
                            selectionManager.setGalleryAdapter(adapterSearchList);
                            selectionManager.setFiles(imageResultFiles);
                            selectionManager.startSelection(position);
                        }
                    };
                    adapterSearchList.setClickListener(listener);
                    adapterSearchList.notifyDataSetChanged();
                    numberOfFiles.setText(adapterSearchList.getItemCount()+" Files");

                //"Videos" tab
                }else {
                    adapterSearchList.setFiles(videoResultFiles);
                    final MediaListener listener = new MediaListener() {
                        @Override
                        public void onClick(View view, int position) {

                            //prepare the Intent for OpenFile activity
                            Intent intent = new Intent(getApplicationContext(), OpenFile.class);
                            intent.putExtra("files", videoResultFiles);
                            intent.putExtra("position",position);
                            //start new Activity
                            startActivity(intent);
                        }

                        //on long click set the SelectionManager properly
                        @Override
                        public void onItemLongClick(View view, int position) {
                            selectionManager.setGalleryAdapter(adapterSearchList);
                            selectionManager.setFiles(videoResultFiles);
                            selectionManager.startSelection(position);
                        }
                    };
                    adapterSearchList.setClickListener(listener);
                    adapterSearchList.notifyDataSetChanged();
                    numberOfFiles.setText(adapterSearchList.getItemCount()+" Files");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    //Layout if no results
    public void displayNoResultsLayout(){
        searchNoResultsText.setVisibility(View.VISIBLE);
        searchNoResultsImage.setVisibility(View.VISIBLE);
        searchList.setVisibility(View.GONE);
        searchNoMatchText.setVisibility(View.GONE);
        numberOfFiles.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        //hide toolbar (if ActionMode is on)
        try {
            selectionManager.clearActionMode();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    //Layout if search did not return any results
    public void displayNoMatchLayout(){
        searchNoMatchText.setVisibility(View.VISIBLE);
        searchNoResultsImage.setVisibility(View.VISIBLE);
        searchList.setVisibility(View.GONE);
        searchNoResultsText.setVisibility(View.GONE);
        numberOfFiles.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        //hide toolbar (if ActionMode is on)
        try {
            selectionManager.clearActionMode();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //edit date range on click listener. Opens the date range dialog window
    public void editDateRange(View view) {

        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final DateRangeDialog dateRangeDialog = new DateRangeDialog(this,inflater,null,null);

        final AlertDialog alertDialog = dateRangeDialog.displayDateRangeDialog();
        dialogOnClose(alertDialog,dateRangeDialog);

    }

    //Set a listener to be invoked when the date range dialog is closed
    public void dialogOnClose(AlertDialog alertDialog,final DateRangeDialog dateRangeDialog){
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //check if the user has set a date range
                if(dateRangeDialog.getFromDate()!=null && dateRangeDialog.getToDate()!=null){

                    //parse and convert the selected date in dd-MM-yyyy format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String fd = dateFormat.format(dateRangeDialog.getFromDate());
                    String td = dateFormat.format(dateRangeDialog.getToDate());

                    //modify the layout properly
                    dateRangePlaceholder.setText(String.format(getResources().getString(R.string.date_range_placeholder),
                            fd,td));
                    dateRangePlaceholder.setVisibility(View.VISIBLE);
                    reset.setVisibility(View.VISIBLE);
                    resetOnClick();

                    //set date range filter on
                    dateRangeOn = true;
                    //fetch from database the files that apply to the date range filter
                    Cursor results = databaseHelper.getFilesByDateRange(dateRangeDialog.getFromDate(),dateRangeDialog.getToDate());
                    fileListDateRangeResults.clear();
                    while (results.moveToNext()){
                        File file = new File(results.getString(1), Type.valueOf(results.getString(2)));
                        fileListDateRangeResults.add(file);
                        checkFileType(file);
                    }
                    results.close();

                    /*If the text from the search box is empty, display as results the files
                      that apply to the date range filter. If search box is not empty call
                      onQueryTextChange() in order to display the results that match both to the
                      user's input and the date range filter*/
                    if(searchText.getText().toString().trim().equals("")){
                        displayResults(fileListDateRangeResults);
                    }else {
                        onQueryTextChange(searchText.getText().toString().trim());
                    }

                }

            }
        });
    }

    //reset button on click listener
    public void resetOnClick(){
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*set date range filter off, modify the layout, clear the list which
                 stores the results/files from the date range filter and update the
                 displaying results */
                dateRangeOn = false;
                reset.setVisibility(View.GONE);
                dateRangePlaceholder.setVisibility(View.GONE);
                fileListDateRangeResults.clear();
                onQueryTextChange((searchText.getText().toString().trim()));
            }
        });
    }

    //checks the type of the file(image/video) and then adds it in the appropriate list
    public void checkFileType(File file){
        if(file.getType()==Type.IMAGE){
            imageResultFiles.add(file);
        }else {
            videoResultFiles.add(file);
        }
    }
}