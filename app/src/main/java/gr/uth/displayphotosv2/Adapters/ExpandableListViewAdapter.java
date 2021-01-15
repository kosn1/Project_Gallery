package gr.uth.displayphotosv2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.Calendar;

import gr.uth.displayphotosv2.R;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private final Activity context;
    private final ArrayList<String>  groupList;
    private com.shawnlin.numberpicker.NumberPicker numPicker;

    private int currentYear;
    private int currentMonth;
    private int currentDay;

    private final TextView[] groupValue = new TextView[2];


    private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};

    /*A list with NumberPicker Objects. The 3 NumberPicker Objects represent the days,
    * the months and the years of the calendar.*/
    ArrayList<NumberPicker> childGroup = new ArrayList<>();


    public ExpandableListViewAdapter(Activity context, ArrayList<String> groupList, int currentYear, int currentDay, int currentMonth){
        this.context = context;
        this.groupList = groupList;
        this.currentYear = currentYear;
        this.currentMonth = currentMonth;
        this.currentDay = currentDay;
    }

    //return group list size
    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        /*The number of children in each group is fixed to 3, as we need 3 NumberPickers
          (Days,Months,Years)*/
        return 3;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {

        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        //Initialize view
        convertView = LayoutInflater.from(parent.getContext()).
                inflate(android.R.layout.simple_expandable_list_item_1,parent,false);

        //A 2-size array of textviews (one for each of our groups) which displays group name/value(date)
        groupValue[groupPosition] = convertView.findViewById(android.R.id.text1);

        //the string which holds the group's name
        String sGroup = String.valueOf(getGroup(groupPosition));

        //set the text to be displayed in the proper TextViews
        if(groupPosition == 0){
            groupValue[groupPosition].setText(String.format(context.getResources().getString(R.string.from),sGroup));
        }else {
            groupValue[groupPosition].setText(String.format(context.getResources().getString(R.string.to),sGroup));
        }

        groupValue[groupPosition].setTypeface(null, Typeface.BOLD);
        groupValue[groupPosition].setTextColor(Color.BLACK);


        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {

        /*Reset childGroup in order to have always 3 NumberPicker Objects. Each time this method is
          called, adds 3 new children to the list, so the old ones must be replaced*/
        if(childGroup.size()>=3){
            childGroup.clear();
        }

        //Initialize view
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.expandable_list_item, null);



        /*parse the date from the group's value (name) and update the variables currentDay,
        currentYear and currentMonth from it*/
        String currentSettedDate = getGroup(groupPosition).toString();
        String[] dateSplitted = currentSettedDate.split(" ");
        currentDay = Integer.parseInt(dateSplitted[0]);
        currentYear = Integer.parseInt(dateSplitted[2]);

        int k=0;
        for (String s: months){
            if(s.equals(dateSplitted[1])){
                break;
            }
            k++;
        }
        currentMonth = k+1;


        numPicker = convertView.findViewById(R.id.number_picker);
        setNumPickerProperties(childPosition,groupPosition);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        
        return true;
    }

    /*This method is called on every change of the current value of a NumberPicker.
    * It updates the date that is displayed in the proper textview dynamically.*/
    public void updateDateValue(int day,int year, String month , int groupPosition){
        groupList.set(groupPosition,day+" "+month+" "+year);
        String dateValue = (groupPosition == 0) ? String.format(context.getResources().getString(R.string.from),groupList.get(groupPosition))
                : String.format(context.getResources().getString(R.string.to),groupList.get(groupPosition));
        groupValue[groupPosition].setText(dateValue);
    }

    /*based on childPosition set up the NumberPickers.
           0 is days NumberPicker
           1 is months NumberPicker
           2 is years NumberPicker

       This function formats each NumberPicker properly in order to have calendar functionality.
       Sets the proper number of days of each month, checks for leap years etc...
           */
    public void setNumPickerProperties(int childPosition, final int groupPosition){

        //Create a calendar object to get the current day,month,year
        final Calendar cal = Calendar.getInstance();

        if(childPosition==0){

            ////set days NumberPicker
            numPicker.setMinValue(1);
            if(currentMonth==1 || currentMonth==3 || currentMonth==5 || currentMonth==7
                    || currentMonth==8 || currentMonth==10|| currentMonth==12){
                numPicker.setMaxValue(31);
            }else if(currentMonth==2){
                cal.set(Calendar.YEAR, currentYear);
                if(cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365){
                    numPicker.setMaxValue(29);
                }else {
                    numPicker.setMaxValue(28);
                }
            }else {
                numPicker.setMaxValue(30);
            }
            numPicker.setValue(currentDay);


            childGroup.add(numPicker);

            //dynamically update the value which the group's texview is displaying
            numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    updateDateValue(picker.getValue(),currentYear,months[childGroup.get(1).getValue()-1],groupPosition);
                }
            });
        }else if (childPosition==1){

            //set months NumberPicker

            numPicker.setMinValue(1);
            numPicker.setMaxValue(months.length);
            numPicker.setDisplayedValues(months);
            numPicker.setValue(currentMonth);
            childGroup.add(numPicker);

            //update the days NumberPicker on month value change
            numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    if(newVal==2){


                        cal.set(Calendar.YEAR, currentYear);
                        if(cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365){
                            childGroup.get(0).setMaxValue(29);
                        }else {
                            childGroup.get(0).setMaxValue(28);
                        }
                    }else if(newVal==1 || newVal==3 || newVal==5 || newVal==7
                            || newVal==8 || newVal==10|| newVal==12){
                        childGroup.get(0).setMaxValue(31);

                    }else {
                        childGroup.get(0).setMaxValue(30);

                    }

                    //dynamically update the value which the group's texview is displaying
                    updateDateValue(childGroup.get(0).getValue(),currentYear,months[newVal-1],groupPosition);
                }
            });
        }else {
            //set years NumberPicker
            final String[] years = new String[201];
            int i=1900;
            for(int j=0;j<years.length;j++){
                years[j] = String.valueOf(i);
                i++;

            }
            numPicker.setMinValue(1);
            numPicker.setMaxValue(years.length);
            numPicker.setDisplayedValues(years);
            numPicker.setValue(currentYear-1899);

            currentYear = Integer.parseInt(years[numPicker.getValue()-1]);

            childGroup.add(numPicker);

            //check for leap year, on year value change and update days of February accordingly
            numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    currentYear = Integer.parseInt(years[newVal-1]);
                    if(childGroup.get(1).getValue()==2){
                        cal.set(Calendar.YEAR, currentYear);
                        if(cal.getActualMaximum(Calendar.DAY_OF_YEAR) > 365){
                            childGroup.get(0).setMaxValue(29);
                        }else {
                            childGroup.get(0).setMaxValue(28);
                        }
                    }

                    //dynamically update the value which the group's texview is displaying
                    updateDateValue(childGroup.get(0).getValue(),currentYear,months[childGroup.get(1).getValue()-1],groupPosition);

                }
            });

        }
    }

}