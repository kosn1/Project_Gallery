package gr.uth.displayphotosv2.Dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gr.uth.displayphotosv2.DatabaseHelper;

public class DateDialog {

    Context context;
    LayoutInflater inflater;

    DatabaseHelper databaseHelper;
    DatePickerDialog.OnDateSetListener dateSetListener;

    public DateDialog(@NonNull Context context, LayoutInflater inflater) {
        this.context = context;
        this.inflater = inflater;

        /*Initializing a helper object using DatabaseHelper.getInstance(context),
        guarantees that only one database helper will exist
        across the entire application's lifecycle*/
        databaseHelper = DatabaseHelper.getInstance(context);

    }

    public void displayDateDialog(final String filePath){
        //Set Date listener
        setDate(filePath);

        //retrieve the current date of the photo/video
        Cursor result = databaseHelper.getDateOfFile(databaseHelper.getFileID(filePath));
        String currentDate="";
        while (result.moveToNext()){
            currentDate = result.getString(0);
        }
        result.close();

        /*Define and display the date dialog window.
         If the selected photo/video has a date already stored,
         set the date picker dialog window to the specified date,
         otherwise set the date picker dialog window to current date*/
        if(currentDate!=null){
            String[] dateArray = currentDate.split("-");
            int currentYear = Integer.parseInt(dateArray[0]);
            int currentMonth = Integer.parseInt(dateArray[1])-1;
            int currentDay = Integer.parseInt(dateArray[2]);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    android.R.style.Theme_Holo_Light_Dialog,
                    dateSetListener,
                    currentYear,currentMonth,currentDay);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();

        }else {

            //Create a calendar object to get the current day,month,year
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    android.R.style.Theme_Holo_Light_Dialog,
                    dateSetListener,
                    year,month,day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }
    }


    //The listener used to indicate the user has finished selecting a date
    public void setDate(final String filePath){
        /*MUST BE DECLARED BEFORE CREATING A NEW DatePickerDialog OBJECT,
        OTHERWISE IT WILL NEVER GET TRIGGERED!!*/
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                //String monthName = new DateFormatSymbols(Locale.US).getMonths()[month];
                //String date = monthName +" "+dayOfMonth+", "+year;
                //Toast.makeText(context, date, Toast.LENGTH_SHORT).show();
                try {
                    //convert the selected date in yyyy-MM-dd format
                    String date = year + "-" + month + "-" + dayOfMonth;
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date finalDate = format.parse(date);
                    //add date to File
                    databaseHelper.insertDateToFile(databaseHelper.getFileID(filePath),finalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to add date", Toast.LENGTH_SHORT).show();
                }

            }
        };
    }

}
