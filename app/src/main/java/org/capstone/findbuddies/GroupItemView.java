package org.capstone.findbuddies;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by user on 2017-12-02.
 */

public class GroupItemView extends LinearLayout {
    TextView textView;
    TextView textView2;

    public GroupItemView(Context context) {
        super(context);

        init(context);
    }

    public GroupItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.group_item,this,true);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
    }

    public void setName(String name){
        textView.setText(name);
    }

    public void setMember(String member){
        textView2.setText(member);
    }

}
