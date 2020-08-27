package gr.uth.displayphotosv2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class OpenImage extends AppCompatActivity {

    ImageView fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);

        fullImage = findViewById(R.id.full_image);

        String path = getIntent().getExtras().getString("image");

        //compress and load image in full size
        Bitmap bitmapImage = BitmapFactory.decodeFile(path);
        int nh = (int) ( bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
        fullImage.setImageBitmap(scaled);
    }
}