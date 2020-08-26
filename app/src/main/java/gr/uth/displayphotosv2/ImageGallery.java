package gr.uth.displayphotosv2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class ImageGallery {

    //returns a list with the absolute path of each image
    public static ArrayList<String> listOfImages(Context context){

        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        //build the query
        String [] projection = {MediaStore.MediaColumns.DATA,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        //fetch the data
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy+" DESC");
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        //get folder name of image
        //column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME);

        //add the absolute path of each image to the list
        while (cursor.moveToNext()){
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }

        return listOfAllImages;

    }
}
