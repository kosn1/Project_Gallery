package gr.uth.displayphotosv2.Dialogs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AlertDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gr.uth.displayphotosv2.Adapters.ExpandableListViewAdapter;
import gr.uth.displayphotosv2.R;


/*A subclass of AlertDialog that displays a custom dialog window. Provides a layout in which the
* user can set date range filter to the search results by selecting a start_date and an end date*/
public class DateRangeDialog extends AlertDialog {


    private Context context;
    private LayoutInflater inflater;
    private AlertDialog alert;

    private Button doneBtn;

    ExpandableListViewAdapter exListAdapter;

    final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};

    public DateRangeDialog(Context context, LayoutInflater inflater) {
        super(context);
        this.context = context;
        this.inflater = inflater;
    }

    public void displayDateRangeDialog(){

        //display date range dialog window
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AnimationThemeDialog);
        final View dialogView = inflater.inflate(R.layout.datepicker_dialog, null);
        alert = builder.setView(dialogView).show();

        doneBtn = dialogView.findViewById(R.id.doneBtn);
        final ExpandableListView expandableListView = dialogView.findViewById(R.id.expandableListView);
        expandableListView.setClickable(true);

        //Create a calendar object to get the current day,month,year
        final Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth= cal.get(Calendar.MONTH) + 1;
        int currentDay= cal.get(Calendar.DAY_OF_MONTH);

        /* Create a group list which will store the items(groups) of the expandable list view.
           Add two items in group list (Start_date,End_date). Both dates are initialized
           to current date. */
        ArrayList<String> groupList=new ArrayList<>();
        groupList.add(currentDay+" " + months[currentMonth -1] + " " + currentYear);
        groupList.add(currentDay+" " + months[currentMonth -1] + " " + currentYear);

        //initialize and set the adapter to expandable list view
        exListAdapter = new ExpandableListViewAdapter((Activity) context, groupList, currentYear, currentDay, currentMonth);
        expandableListView.setAdapter(exListAdapter);

        //Done button listener
        setDateRange(expandableListView);

        /*group click listener for the ExpandableListView. On group click expand the group which
          was clicked and collapse the other one.*/
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if(groupPosition==0){
                    parent.collapseGroup(1);
                    parent.expandGroup(0);
                }else {
                    parent.collapseGroup(0);
                    parent.expandGroup(1);
                }


                return true;
            }
        });

    }


    /*Fetches the date range and checks if start date is prior to end date.
      Closes the dialog window and applies the date range selection if everything is fine,
      else displays a Toast to the user. */
    public void setDateRange(final ExpandableListView expandableListView){
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*get the two dates and split them to day/month/year in order to
                 format them properly*/
                String[] dateFrom = expandableListView.getExpandableListAdapter().getGroup(0).toString().split(" ");
                String[] dateTo = expandableListView.getExpandableListAdapter().getGroup(1).toString().split(" ");

                String dateFromFinal=dateFrom[2] + "-" + (monthStrToInt(dateFrom)) + "-" + dateFrom[0];
                String dateToFinal=dateTo[2] + "-" + (monthStrToInt(dateTo)) + "-" + dateTo[0];


                try {
                    //convert the selected dates to yyyy-MM-dd format
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date fromDateFormatted = format.parse(dateFromFinal);
                    Date toDateFormatted = format.parse(dateToFinal);

                    System.out.println(dateFromFinal);
                    System.out.println(dateToFinal);

                    //compare the two dates chronologically
                    if(fromDateFormatted.compareTo(toDateFormatted)<0){
                        System.out.println("OKKK");
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //close the dialog window
                alert.cancel();
            }
        });
    }


    //Converts month's name to an int.For example Jan=1, Feb=2, Mar=3 etc...
    public int monthStrToInt(String[] dateSplitted){

        int k=0;
        for (String s: months){
            if(s.equals(dateSplitted[1])){
                break;
            }
            k++;
        }
        return k+1;
    }
}
