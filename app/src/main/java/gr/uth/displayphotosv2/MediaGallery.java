package gr.uth.displayphotosv2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

public class MediaGallery {

    //returns a list with the absolute path of each image
    public static ArrayList<File> listOfImages(Context context){

        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<File> listOfAllImages = new ArrayList<>();
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
            //absolutePathOfImage = cursor.getString(column_index_data);
            //System.out.println(cursor.getString(dateColumn));
            File file = new File(cursor.getString(column_index_data),Type.IMAGE);
            listOfAllImages.add(file);
        }
        cursor.close();
        return listOfAllImages;

    }

    //returns a list with the absolute path of each video
    public static ArrayList<File> listOfVideos(Context context){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<File> listOfAllVideos = new ArrayList<>();
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
        //int dateColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);

        //get folder name of video
        //column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME);

        //add the absolute path of each video to the list
        while (cursor.moveToNext()){
            //absolutePathOfVideo = cursor.getString(column_index_data);
            //System.out.println(cursor.getString(bucketColumn));
            //System.out.println(cursor.getString(dateColumn));
            File file = new File(cursor.getString(column_index_data),Type.VIDEO);
            listOfAllVideos.add(file);
        }
        cursor.close();
        return listOfAllVideos;

    }

    /*returns a HashMap with album name as key,
      and the path of the most recent photo from this album as a value(thumbnail)*/
    public static HashMap<String, String> getListOfAlbums(Context context){

        //build the query
        String[] projection = new String[] {MediaStore.MediaColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME};

        String orderByImg = MediaStore.Images.Media.DATE_TAKEN;
        String orderByVid = MediaStore.Video.Media.DATE_TAKEN;

        //fetch the data, we need separate cursors for images and videos
        Cursor imageCursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null,
                        null,
                        orderByImg + " DESC");

        Cursor videoCursor = context.getContentResolver().
                query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                        null,
                        null,
                        orderByVid + " DESC");

        int imagePathColumn = imageCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int videoPathColumn = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

        //albums and thumbnails
        HashMap<String, String> albumHashMap = new HashMap<>();

        //HashMap with album name as key, and the date(timestamp) of album's thumbnail as value
        HashMap<String, String> namesAndDates = new HashMap<>();

        /*loop through each image of the storage and check if the album of the image has been added to
        albumHashMap already. If not, add album's name as key and image's path as value (thumbnail of the album)
        to albumHashMap. Then store the date of the thumbnail as value to namesAndDates HashMap
        and album's name as a key. If albumHashMap already has a value for the specific key it means that this value is
        the path of the most recently added image because imageCursor brings images in descending order
        based on their date, so we prevent albumHashMap to update the image with a less recent one*/
        while (imageCursor.moveToNext()) {


            if(!albumHashMap.containsKey(imageCursor.
                    getString((imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))) + "\n")){

                albumHashMap.put(imageCursor.getString((imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))) + "\n",
                        imageCursor.getString(imagePathColumn));

                namesAndDates.put(imageCursor.getString((imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))) + "\n",
                        imageCursor.getString((imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN))));
            }

        }

        long videoDate = 0;

        /*Loop through each video of the storage and check if the album of the video has been added to
        albumHashMap already. If albumHashMap already has a value(thumbnail) for the specific key, parse the
        timestamp of the date that video was added and compare it with the value(timestamp) that is already stored
        in namesAndDates HashMap for the same key, in order to find the most recent item(image or video) and set
        it as thumbnail of the album.
        If the album of the current video is not added to albumHashMap yet, add album's name
        as a key and video's thumbnail as value to albumHashMap*/
        while (videoCursor.moveToNext()) {


            videoDate = Long.parseLong(videoCursor.getString(imageCursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN)));

            if(albumHashMap.containsKey(videoCursor.
                    getString((videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME))) + "\n")) {

                try {
                    if (videoDate > Long.parseLong(namesAndDates.get(videoCursor.
                            getString((videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME))) + "\n"))){

                        albumHashMap.put(videoCursor.getString((videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME))) + "\n",
                                videoCursor.getString(videoPathColumn));

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }else {
                albumHashMap.put(videoCursor.getString((videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME))) + "\n",
                        videoCursor.getString(videoPathColumn));
            }


        }
        imageCursor.close();
        videoCursor.close();

        return albumHashMap;
    }

    /*returns a HashMap with album name as key, and the size (number of items) of this album as a value*/
    public static HashMap<String, Integer> getSizeOfAlbums(Context context) {

        //build the query
        String[] projection = new String[] {MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME};

        //fetch the data, we need separate cursors for images and videos
        Cursor imageCursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null,
                        null,
                        null);

        Cursor videoCursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);


        HashMap<String, Integer> albumSizeHashMap = new HashMap<>();

        /*Find the album names and count their items.
        We need separate while loops, in order to count both image and video files*/
        while (imageCursor.moveToNext()) {

            //Store album's name as key and album's size as value to albumSizeHashMap
            if(!albumSizeHashMap.containsKey(imageCursor.
                    getString((imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))) + "\n")){

                //for new albums set value/counter 1
                albumSizeHashMap.put(imageCursor.getString((imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))) + "\n", 1);

            }else {
                //for albums already in albumSizeHashMap increase their value/counter by 1
                albumSizeHashMap.put(imageCursor.getString((imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))) + "\n",
                        albumSizeHashMap.get(imageCursor.getString((imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))) + "\n")+1);
            }
        }

        while (videoCursor.moveToNext()) {

            //Store album's name as key and album's size as value to albumSizeHashMap
            if(!albumSizeHashMap.containsKey(videoCursor.
                    getString((videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME))) + "\n")){

                //for new albums set value/counter 1
                albumSizeHashMap.put(videoCursor.getString((videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME))) + "\n", 1);
            }else {
                //for albums already in albumSizeHashMap increase their value/counter by 1
                albumSizeHashMap.put(videoCursor.getString((videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME))) + "\n",
                        albumSizeHashMap.get(videoCursor.getString((videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME))) + "\n")+1);
            }
        }

        imageCursor.close();
        videoCursor.close();

        return albumSizeHashMap;

    }


    //returns a list with all the files(photos/videos) of the selected album
    public static ArrayList<File> getAlbumItems(Context context, String albumName){

        //build the query
        String[] projection = new String[] {MediaStore.MediaColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME};

        String orderByImg = MediaStore.Images.Media.DATE_TAKEN;
        String orderByVid = MediaStore.Video.Media.DATE_TAKEN;

        //fetch the data, we need separate cursors for images and videos
        Cursor imageCursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null,
                        null,
                        orderByImg + " DESC");

        Cursor videoCursor = context.getContentResolver().
                query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                        null,
                        null,
                        orderByVid + " DESC");

        int imagePathColumn = imageCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int videoPathColumn = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

        ArrayList<File> files = new ArrayList<>();

        //add the images of the album to the list
        while (imageCursor.moveToNext()) {
            if(imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)).equals(albumName)){
                File file = new File(imageCursor.getString(imagePathColumn),Type.IMAGE);
                files.add(file);
            }
        }

        //add the videos of the album to the list
        while (videoCursor.moveToNext()) {
            if(videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME)).equals(albumName)){
                File file = new File(videoCursor.getString(videoPathColumn),Type.VIDEO);
                files.add(file);
            }
        }

        imageCursor.close();
        videoCursor.close();
        return files;
    }

}
