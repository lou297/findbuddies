package org.capstone.findbuddies;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by user on 2017-12-02.
 */

public class Group extends Fragment {

    GroupAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.group,container,false);

        ListView listview = (ListView) rootView.findViewById(R.id.listView2);

        adapter = new GroupAdapter();

        adapter.addgroup(new GroupItem("가족","아들 , 딸",R.drawable.family));

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getContext(),ShowMap.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    class GroupAdapter extends BaseAdapter{
        ArrayList<GroupItem> groups = new ArrayList<GroupItem>();

        @Override
        public int getCount() {
            return groups.size();
        }

        public void addgroup(GroupItem group){
            groups.add(group);
        }

        @Override
        public Object getItem(int i) {
            return groups.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            GroupItemView itemview = new GroupItemView(getContext());
            GroupItem group = groups.get(i);
            itemview.setName(group.getName());
            itemview.setMember(group.getMember());
            itemview.setImage(group.getResId());

            return itemview;
        }
    }


}
