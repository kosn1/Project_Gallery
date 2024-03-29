package gr.uth.displayphotosv2.Dialogs;


import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import gr.uth.displayphotosv2.DatabaseHelper;
import gr.uth.displayphotosv2.File;
import gr.uth.displayphotosv2.PlaceApi;
import gr.uth.displayphotosv2.R;

public class LocationDialog extends AlertDialog {

    Context context;
    DatabaseHelper databaseHelper;
    LayoutInflater inflater;
    SearchView searchView;
    ImageButton closeDialogBtn;
    TextView locationTextView;
    ImageButton deleteLocationBtn;

    SimpleCursorAdapter mAdapter;

    private AlertDialog alert;

    public LocationDialog(Context context, LayoutInflater inflater) {
        super(context);
        this.context = context;
        this.inflater = inflater;

        /*Initializing a helper object using DatabaseHelper.getInstance(context),
        guarantees that only one database helper will exist
        across the entire application's lifecycle*/
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    public void displayLocationDialog(String filepath){
        //display location dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AppTheme);
        final View dialogView = inflater.inflate(R.layout.location_dialog, null);
        alert = builder.setView(dialogView)
                       .show();

        /*Allow the window to be resized when keyboard is shown,
          so that its contents are not covered by the keyboard*/
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //create an adapter for displaying location suggestions and set it to searchview widget
        mAdapter = createSuggestionsAdapter();
        searchView=dialogView.findViewById(R.id.searchview);
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setIconifiedByDefault(false);

        //load current location of file
        locationTextView = dialogView.findViewById(R.id.location_txtview);
        deleteLocationBtn = dialogView.findViewById(R.id.remove_location_btn);
        loadCurrentLocation(filepath);

        //select suggestion Listener
        selectSuggestion(filepath);

        //remove location button listener
        deleteLocation(filepath);

        //close button Listener
        closeDialogBtn = dialogView.findViewById(R.id.close_location_dialog);
        closeBtnOnClick();

    }

    /*display location dialog window for multiple selected files. It does not include the current
    * location of the files, as there are more than one.*/
    public void displayLocationDialog(ArrayList<File> selectedFiles){

        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AppTheme);
        final View dialogView = inflater.inflate(R.layout.location_dialog, null);
        alert = builder.setView(dialogView)
                .show();

        /*Allow the window to be resized when keyboard is shown,
          so that its contents are not covered by the keyboard*/
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //create an adapter for displaying location suggestions and set it to searchview widget
        mAdapter = createSuggestionsAdapter();
        searchView=dialogView.findViewById(R.id.searchview);
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setIconifiedByDefault(false);

        //select suggestion Listener
        selectSuggestion(selectedFiles);

        //close button Listener
        closeDialogBtn = dialogView.findViewById(R.id.close_location_dialog);
        closeBtnOnClick();

    }

    //get the location of the selected photo/video (if any) and display it
    public void loadCurrentLocation(String filepath){
        Cursor result = databaseHelper.getLocationOfFile(databaseHelper.getFileID(filepath));
        String currentLocation="";
        while (result.moveToNext()){
            currentLocation = result.getString(0);
        }
        result.close();

        if(currentLocation!=null){
            locationTextView.append(" "+currentLocation);
            locationTextView.setVisibility(View.VISIBLE);
            deleteLocationBtn.setVisibility(View.VISIBLE);
        }
    }

    /*returns a SimpleCursorAdapter object provided with a query filter,
    in order to display location suggestions based on user's input*/
    public SimpleCursorAdapter createSuggestionsAdapter(){
        final SimpleCursorAdapter mAdapter;

        final String[] from = new String[] {"location"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


        //sets the query filter provider used to filter the current Cursor
        mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                ArrayList<String> results = new ArrayList<>();
                if(constraint!=null){
                    PlaceApi placeApi = new PlaceApi();
                    results = placeApi.autoComplete(constraint.toString());
                    //InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    // imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                }
                return populateAdapter((String) constraint,results);
            }
        });

        return mAdapter;

    }

    // Getting selected (clicked) item suggestion
    public void selectSuggestion(final String filePath){
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                //get the selected location
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                String loc = cursor.getString(cursor.getColumnIndex("location"));
                searchView.setQuery(loc, true);

                //add location to photo/video and close the dialog window
                databaseHelper.addLocationToFile(databaseHelper.getFileID(filePath),loc);
                alert.cancel();
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }
        });
    }

    /* Getting selected (clicked) item suggestion
    (for adding location to multiple selected files simultaneously)*/
    public void selectSuggestion(final ArrayList<File> selectedFiles){
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                //get the selected location
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                String loc = cursor.getString(cursor.getColumnIndex("location"));
                searchView.setQuery(loc, true);

                try {
                    //add location to files and close the dialog window
                    for(File selectedFile:selectedFiles){
                        databaseHelper.addLocationToFile(databaseHelper.getFileID(selectedFile.getPath()),loc);
                    }
                    Toast.makeText(context, "Location added to selected files", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to add location", Toast.LENGTH_SHORT).show();
                }
                alert.cancel();
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }
        });
    }

    // Populate the suggestions in the adapter
    private Cursor populateAdapter(String query,ArrayList<String> SUGGESTIONS) {
        MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "location" });
        for (int i=0; i<SUGGESTIONS.size(); i++) {
            /*if (SUGGESTIONS.get(i).toLowerCase().startsWith(query.toLowerCase())){
                c.addRow(new Object[] {i, SUGGESTIONS.get(i)});
            }*/
            c.addRow(new Object[] {i, SUGGESTIONS.get(i)});
        }
        return c;
    }

    //deletes location from file and closes the dialog window
    public void deleteLocation(final String filepath){
        deleteLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteLocationFromFile(databaseHelper.getFileID(filepath));
                alert.cancel();
            }
        });
    }

    //close button, dismisses the dialog window without saving any changes
    public void closeBtnOnClick(){
        closeDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });
    }
}
