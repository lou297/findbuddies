package org.capstone.findbuddies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by user on 2017-12-02.
 */

public class Buddy extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.buddy,container,false);

        ListView listview = (ListView) rootView.findViewById(R.id.listView);

        BuddyAdapter adapter = new BuddyAdapter();
        adapter.addbuddy(new BuddyItem("아들","010-1234-1234",R.drawable.boy));
        adapter.addbuddy(new BuddyItem("딸","010-2345-1234",R.drawable.girl));
        adapter.addbuddy(new BuddyItem("친구1","010-5324-1234",R.drawable.friend));
        adapter.addbuddy(new BuddyItem("친구2","010-3424-1234",R.drawable.friend2));
        listview.setAdapter(adapter);

        return rootView;
    }

    class BuddyAdapter extends BaseAdapter {
        ArrayList<BuddyItem> buddies = new ArrayList<BuddyItem>();

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
            BuddyItemView itemview = new BuddyItemView(getContext());
            BuddyItem buddy = buddies.get(i);
            itemview.setName(buddy.getName());
            itemview.setMobile(buddy.getMobile());
            itemview.setImage(buddy.getResId());

            return itemview;
        }
    }
}
