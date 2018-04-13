package org.capstone.findbuddies;

import android.content.Intent;
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

public class CalendarFragment extends Fragment {
    ArrayList<MemoItem> Memos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_view,container,false);
        ListView listview = (ListView) rootView.findViewById(R.id.SpecificDateMemoList);

        SpecificDateMemoAdapter adapter = new SpecificDateMemoAdapter();

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),MemoEdit.class);

                startActivity(intent);
            }
        });
        return rootView;
    }

    class SpecificDateMemoAdapter extends BaseAdapter {

        private SpecificDateMemoAdapter() {


        }

        @Override
        public int getCount() {
            return Memos.size();
        }

        @Override
        public Object getItem(int position) {
            return Memos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MemoItemView itemview = new MemoItemView(getContext());
            MemoItem Memo = Memos.get(position);
            itemview.setTitle(Memo.getTitle());
            itemview.setContents(Memo.getContents());
            itemview.setDate(Memo.getDate());
            itemview.setPicture(Memo.getPicture());

            return itemview;
        }
    }
}
