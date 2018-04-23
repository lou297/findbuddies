package org.capstone.findbuddies;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by user on 2017-12-02.
 */

public class BuddyItemView extends LinearLayout {

    TextView textView;
    TextView textView2;
    ImageView imageView;
    

    public BuddyItemView(Context context) {
        super(context);

        init(context);
    }

    public BuddyItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //??
        assert inflater != null;
        //??
        inflater.inflate(R.layout.buddy_item,this,true);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        imageView = findViewById(R.id.imageView);
    }

    public void setName(String name){
        textView.setText(name);
    }
    public void setID(String ID) {
        textView2.setText(ID);
    }

    public void setImage(int resId){
        imageView.setImageResource(resId);
    }
}
