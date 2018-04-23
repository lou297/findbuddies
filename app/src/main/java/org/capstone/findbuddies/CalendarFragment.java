package org.capstone.findbuddies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

public class CalendarFragment extends Fragment {
    ArrayList<MemoItem> Memos = new ArrayList<>();
    CalendarView calendarView;
    String myEmail;
    FirebaseDatabase database;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calendar_view,container,false);
        myEmail = getArguments().getString("myEmail");
        database = FirebaseDatabase.getInstance();
        calendarView = rootView.findViewById(R.id.calendarView);

        final ListView listview = (ListView) rootView.findViewById(R.id.SpecificDateMemoList);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SpecificDateMemoAdapter adapter = new SpecificDateMemoAdapter(year,month,dayOfMonth);

                listview.setAdapter(adapter);
            }
        });

        SpecificDateMemoAdapter adapter = new SpecificDateMemoAdapter(0,0,0);

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

        private SpecificDateMemoAdapter(final int year, final int month, final int dayOfMonth) {
            database.getReference().child("MemoList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Memos.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        SaveMemo value = snapshot.getValue(SaveMemo.class);
                        if(value !=null){
                            if(value.getUploaderEmail().equals(myEmail)){
                                if(year==0){
                                    addMemo(new MemoItem(value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl()));
                                }
                                else if(year==value.getYear()&&month==value.getMonth()&&dayOfMonth==value.getDay()){
                                    addMemo(new MemoItem(value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl()));
                                }
                            }

                        }

                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void addMemo(MemoItem memoItem){
            Memos.add(memoItem);
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
            File f = new File(Memo.getPictureURI());
            itemview.setPicture(Uri.fromFile(f));

            return itemview;
        }
    }
}
