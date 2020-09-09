package gr.uth.displayphotosv2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class MediaGallery {

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
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN};

        String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        //fetch the data
        cursor = context.getContentResolver().query(uri, projection, //which columns to return
                null,
                null,
                orderBy+" DESC");
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        //date to milliseconds
        //int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);

        //get folder name of image
        //column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME);

        //add the absolute path of each image to the list
        while (cursor.moveToNext()){
            absolutePathOfImage = cursor.getString(column_index_data);
            //System.out.println(cursor.getString(dateColumn));
            listOfAllImages.add(absolutePathOfImage);
        }

        return listOfAllImages;

    }

    //returns a list with the absolute path of each video
    public static ArrayList<String> listOfVideos(Context context){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllVideos = new ArrayList<>();
        String absolutePathOfVideo;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        //build the query
        String[] projection = { MediaStore.Video.VideoColumns.DATA ,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.DATE_TAKEN};


        String orderBy = MediaStore.Video.Media.DATE_TAKEN;

        //fetch the data
        cursor = context.getContentResolver().query(uri, projection, //which columns to return
                null,
                null,
                orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

        //int bucketColumn = cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

        //date to milliseconds
        int dateColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);

        //get folder name of video
        //column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME);

        //add the absolute path of each video to the list
        while (cursor.moveToNext()){
            absolutePathOfVideo = cursor.getString(column_index_data);
            //System.out.println(cursor.getString(bucketColumn));
            //System.out.println(cursor.getString(dateColumn));
            listOfAllVideos.add(absolutePathOfVideo);
        }

        return listOfAllVideos;

    }
}
