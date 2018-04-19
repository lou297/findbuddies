package org.capstone.findbuddies;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MemoItemView extends LinearLayout{
    TextView title;
    TextView contents;
    TextView date;
    ImageView picture;


    public MemoItemView(Context context) {
        super(context);
    }

    public MemoItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memo_item,this,true);

        title= findViewById(R.id.title);
        contents = findViewById(R.id.contents);
        date = findViewById(R.id.date);
        picture = findViewById(R.id.picture);
    }

    public void setTitle(String Title) {
        title.setText(Title);
    }

    public void setContents(String Contents) {
        contents.setText(Contents);
    }

    public void setDate(String Date) {
        date.setText(Date);
    }

    public void setPicture(Uri Picture) {
        picture.setImageURI(Picture);
    }
}
