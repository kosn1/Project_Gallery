package gr.uth.displayphotosv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class FullSizeAdapter extends PagerAdapter {

    Context context;
    ArrayList<String> images;
    LayoutInflater inflater;

    public FullSizeAdapter(Context context, ArrayList<String> images){
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    /*checks whether a page View is associated with a given key object
     When you slide, the ViewPager gets view position from an array
     or instantiates it and compare this view with children of ViewPager(Object)
     The view which equals to the key object is displayed to the user*/
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }


    @NonNull
    @Override
    /*called when ViewPager needs a page to display,
      returns the current view,
      PagerAdapter is considering this view as a key value when viewpager changes a page*/
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //retrieve the LayoutInflater instance that is already hooked up to the current context
        inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //load full_item.xml
        View v = inflater.inflate(R.layout.full_item,null);

        //display image in full screen
        ImageView imageView = v.findViewById(R.id.img);
        Glide.with(context)
                .load(images.get(position))
                .apply(new RequestOptions().centerInside())
                .into(imageView);

        //Convert from ViewGroup to Viewpager the containing View in which the page will be shown
        ViewPager vp = (ViewPager) container;
        //add the View to the container
        vp.addView(v,0);
        return v;
    }

    @Override
    // Called when ViewPager no longer needs a page to display.
    // The adapter is responsible to remove the page from the container, which is usually the ViewPager itself.
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);

        ViewPager viewPager = (ViewPager)container;
        View v = (View)object;

        //remove the page(view) from the container
        viewPager.removeView(v);
    }
}
