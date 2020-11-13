package gr.uth.displayphotosv2.Dialogs;


import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import gr.uth.displayphotosv2.DatabaseHelper;
import gr.uth.displayphotosv2.PlaceApi;
import gr.uth.displayphotosv2.R;

public class LocationDialog extends AlertDialog {

    Context context;
    DatabaseHelper databaseHelper;
    LayoutInflater inflater;
    SearchView searchView;
    ImageButton closeDialogBtn;

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
        //display tag dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View dialogView = inflater.inflate(R.layout.location_dialog, null);
        alert = builder.setView(dialogView)
                       .show();
        moveToTop();

        /*Allow the window to be resized when keyboard is shown,
          so that its contents are not covered by the keyboard*/
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //create an adapter for displaying location suggestions and set it to searchview widget
        mAdapter = createSuggestionsAdapter();
        searchView=dialogView.findViewById(R.id.searchview);
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setIconifiedByDefault(false);

        //select suggestion Listener
        selectSuggestion(filepath);

        //close button Listener
        closeDialogBtn = dialogView.findViewById(R.id.close_location_dialog);
        closeBtnOnClick();

    }

    //Push dialog to the top of the screen
    public void moveToTop(){
        Window window = alert.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        window.setAttributes(wlp);
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

    // Populate the suggestions in the adapter
    private Cursor populateAdapter(String query,ArrayList<String> SUGGESTIONS) {
        MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "location" });
        for (int i=0; i<SUGGESTIONS.size(); i++) {
            if (SUGGESTIONS.get(i).toLowerCase().startsWith(query.toLowerCase())){
                c.addRow(new Object[] {i, SUGGESTIONS.get(i)});
            }
        }
        return c;
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
