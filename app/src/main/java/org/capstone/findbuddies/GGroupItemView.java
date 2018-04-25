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

public class GGroupItemView extends LinearLayout implements Checkable {
    TextView Group;
    TextView Member;
    ImageView ImageView;
    CheckBox CB;

    public GGroupItemView(Context context) {
        super(context);
        init(context);
    }

    public GGroupItemView(Context context, @Nullable AttributeSet attrs) {
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
        inflater.inflate(R.layout.g_group_item,this, true);

        Group = findViewById(R.id.g_group_name);
        Member = findViewById(R.id.g_group_member);
        ImageView = findViewById(R.id.g_group_imageview);
        CB = findViewById(R.id.group_checkbox);
    }

    public void setGroup(String group){
        Group.setText(group);
    }

    public void setMember(String member){
        Member.setText(member);
    }

    public void setImage(int resId){
        ImageView.setImageResource(resId);
    }
}
