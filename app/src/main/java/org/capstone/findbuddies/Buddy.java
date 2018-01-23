package org.capstone.findbuddies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by user on 2017-12-02.
 */


public class Buddy extends Fragment {
    @Nullable
//    @Override
    private static Context context;
    CheckBox addcheck;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Buddy.context = getContext();
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.buddy,container,false);

        Button addmate = (Button)rootView.findViewById(R.id.addbuddy);
        addcheck = (CheckBox)rootView.findViewById(R.id.checked);
        addmate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),AddMate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,101);
            }
        });

        ListView listview = (ListView) rootView.findViewById(R.id.listView);

        BuddyAdapter adapter = new BuddyAdapter();
        adapter.addbuddy(new BuddyItem("아들","010-1234-1234",R.drawable.boy));
        adapter.addbuddy(new BuddyItem("딸","010-2345-1234",R.drawable.girl));
        adapter.addbuddy(new BuddyItem("친구1","010-5324-1234",R.drawable.friend));
        adapter.addbuddy(new BuddyItem("친구2","010-3424-1234",R.drawable.friend2));
        listview.setAdapter(adapter);

        return rootView;
    }
    public static Context getAppContext() {
        return Buddy.context;
    }

    static class BuddyAdapter extends BaseAdapter {
        ArrayList<BuddyItem> buddies = new ArrayList<BuddyItem>();
        public void addch(){

        }

        @Override
        public int getCount() {
            return buddies.size();
        }

        public void addbuddy(BuddyItem buddy){
            buddies.add(buddy);
        }

        @Override
        public Object getItem(int i) {
            return buddies.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            BuddyItemView itemview = new BuddyItemView(Buddy.getAppContext());
            BuddyItem buddy = buddies.get(i);
            itemview.setName(buddy.getName());
            itemview.setMobile(buddy.getMobile());
            itemview.setImage(buddy.getResId());

            return itemview;
        }
    }
}
