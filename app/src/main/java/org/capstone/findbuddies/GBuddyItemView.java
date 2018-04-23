package org.capstone.findbuddies;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by user on 2018-03-07.
 */

public class GBuddyItemView extends LinearLayout implements Checkable{
    TextView Name;
    TextView Id;
    ImageView imageView;
    CheckBox CB;

    public GBuddyItemView(Context context) {
        super(context);
        init(context);
    }

    public GBuddyItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void setChecked(boolean checked) {

        if (CB.isChecked() != checked) {
            CB.setChecked(checked);
        }


    }

    @Override
    public boolean isChecked() {

        return CB.isChecked() ;
    }

    @Override
    public void toggle() {

        setChecked(!CB.isChecked()) ;

    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.g_buddy_item,this, true);

        Name = findViewById(R.id.name);
        Id = findViewById(R.id.id);
        imageView = findViewById(R.id.imageView);
        CB = findViewById(R.id.checkBox);
    }

    public void setName(String name){
        Name.setText(name);
    }

    public void setId(String id){
        Id.setText(id);
    }

    public void setImage(int resId){
        imageView.setImageResource(resId);
    }
}
