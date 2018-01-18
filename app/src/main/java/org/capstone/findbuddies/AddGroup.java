package org.capstone.findbuddies;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class AddGroup extends AppCompatActivity {
    Buddy.BuddyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        ListView listview = (ListView)findViewById(R.id.listView);

        adapter = new Buddy.BuddyAdapter();
        adapter.addbuddy(new BuddyItem("아들","010-1234-1234",R.drawable.boy));
        adapter.addbuddy(new BuddyItem("딸","010-2345-1234",R.drawable.girl));
        adapter.addbuddy(new BuddyItem("친구1","010-5324-1234",R.drawable.friend));
        adapter.addbuddy(new BuddyItem("친구2","010-3424-1234",R.drawable.friend2));
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                BuddyItem item = (BuddyItem)adapter.getItem(position);
//                view.setBackgroundColor(Color.BLUE);
                if(view.getDrawingCacheBackgroundColor()==Color.BLUE){
                    view.setBackgroundColor(Color.GRAY);
                }
                Toast.makeText(getApplicationContext(),view.getBackground().toString(),Toast.LENGTH_LONG).show();
                if(view.getDrawingCacheBackgroundColor()==Color.GRAY){
                    view.setBackgroundColor(Color.BLUE);
                }
            }
        });
//        listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(view.getDrawingCacheBackgroundColor()==Color.BLUE){
//                    view.setBackgroundColor(Color.GRAY);
//                }
//                if(view.getDrawingCacheBackgroundColor()==Color.GRAY){
//                    view.setBackgroundColor(Color.BLUE);
//                }
//                view.setBackgroundColor(Color.BLUE);
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }


}
