package gr.uth.displayphotosv2.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import gr.uth.displayphotosv2.PlaceApi;


//adapter for displaying auto suggest Location
public class PlaceAutoSuggestAdapter extends ArrayAdapter implements Filterable {

    ArrayList<String> results;

    int resource;
    Context context;

    PlaceApi placeApi = new PlaceApi();

    public PlaceAutoSuggestAdapter(Context context, int resID){
        super(context,resID);
        this.context = context;
        this.resource = resID;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @NonNull
    @Override
    //Create a Filter which populates the results in the AutoCompleteTextView
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults  filterResults = new FilterResults();
                if(constraint!=null){
                    results = placeApi.autoComplete(constraint.toString());
                    filterResults.values = results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results!=null && results.count>0){
                    notifyDataSetChanged();
                }else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
