package org.capstone.findbuddies;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by user on 2018-03-13.
 */

public class NoticeItemView extends LinearLayout {
    TextView groupOwner;
    TextView groupName;
    TextView groupList;


    public NoticeItemView(Context context) {
        super(context);
        init(context);
    }

    public NoticeItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.notice_item,this,true);
        groupOwner = findViewById(R.id.groupOwner);
        groupName = findViewById(R.id.groupName);
        groupList = findViewById(R.id.groupList);
    }

    public void setGroupOwner(String groupowner) {
        groupOwner.setText(groupowner);
    }

    public void setGroupName(String groupname) {
        groupName.setText(groupname);
    }

    public void setGroupList(String grouplist) {
        groupList.setText(grouplist);
    }
}
