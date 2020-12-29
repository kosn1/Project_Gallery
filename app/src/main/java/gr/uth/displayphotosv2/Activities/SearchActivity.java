package gr.uth.displayphotosv2.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import gr.uth.displayphotosv2.Adapters.GalleryAdapter;
import gr.uth.displayphotosv2.DatabaseHelper;
import gr.uth.displayphotosv2.File;
import gr.uth.displayphotosv2.Interfaces.MediaListener;
import gr.uth.displayphotosv2.R;
import gr.uth.displayphotosv2.Type;

/*An activity that provides a search box for the user to enter a search query.
  Displays a list of files suggestions based on the user's input. The search results,
  are based on the tags and the locations of the files.*/

public class SearchActivity extends AppCompatActivity {

    private RecyclerView searchList;
    private GalleryAdapter adapterSearchList;

    private ImageView searchNoResultsImage;
    private TextView searchNoResultsText,numberOfFiles;
    private TabLayout tabLayout;
    EditText searchText;
    String searchString="";

    ArrayList<File> imageResultFiles;
    ArrayList<File> videoResultFiles;
    ArrayList<File> allResultFiles;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchList = findViewById(R.id.search_list);
        searchNoResultsImage = findViewById(R.id.image_magn_glass);
        searchNoResultsText = findViewById(R.id.search_no_results_text);
        numberOfFiles = findViewById(R.id.files_number);
        tabLayout = findViewById(R.id.tabLayout);

        /*separate lists in order to filter the results to 3 categories bases on the file type.
         all,image only(if any) and video only(if any)*/
        allResultFiles = new ArrayList<>();
        imageResultFiles = new ArrayList<>();
        videoResultFiles = new ArrayList<>();

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

        //if the text from the search box is not empty
        if (!queryText.isEmpty()) {

            //get the tags and the locations of all files
            databaseHelper = DatabaseHelper.getInstance(this);

            Cursor cursorTag = databaseHelper.getTagsOfFiles();
            ArrayList<String> tags = new ArrayList<>();
            while (cursorTag.moveToNext()) {
                tags.add(databaseHelper.getTagName(cursorTag.getInt(0)));
            }

            Cursor cursorLocation = databaseHelper.getLocations();
            ArrayList<String> locations = new ArrayList<>();
            while (cursorLocation.moveToNext()){
                locations.add(cursorLocation.getString(0));
            }

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
                     * so the same files are not added twice in those lists.*/
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
                                fileListResults.add(file);
                                if(file.getType()==Type.IMAGE){
                                    imageResultFiles.add(file);
                                }else {
                                    videoResultFiles.add(file);
                                }
                            }
                        }
                    }
                }
            }
            //search for files by matching locations
            for(String str: locations){
                for(String strSplit : searchStringSplit) {
                    /*if a matching location is found, get from database the files which have this
                     *location. Add the files to the list with the search results. Also add each file to
                     * the appropriate list based on their type(image/video). Check for duplicates,
                     * so the same files are not added twice in those lists.*/
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
                                fileListResults.add(file);
                                if(file.getType()==Type.IMAGE){
                                    imageResultFiles.add(file);
                                }else {
                                    videoResultFiles.add(file);
                                }
                            }
                        }
                    }
                }
            }
            displayResults(fileListResults);
        }else {
            displayNoResultsLayout();
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
        };

        //create the adapter of RecyclerView
        adapterSearchList = new GalleryAdapter(this, fileListResults, listener);

        //if search has no results display the appropriate layout
        if (adapterSearchList.getItemCount()==0) {
            displayNoResultsLayout();
            return;
        }

        //change the layout to display the results
        searchNoResultsText.setVisibility(View.GONE);
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
        numberOfFiles.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
    }
}