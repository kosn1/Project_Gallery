package gr.uth.displayphotosv2.Interfaces;

import android.view.View;

public interface MediaListener {
    void onClick(View view, int position);
    void onItemLongClick(View view, int position);
}
