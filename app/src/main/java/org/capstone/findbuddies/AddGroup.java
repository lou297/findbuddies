package org.capstone.findbuddies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

public class AddGroup extends AppCompatActivity {
    Buddy.BuddyAdapter adapter;
    int checked_number=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        CheckBox checked = (CheckBox)findViewById(R.id.checked);
//        checked.setVisibility(View.VISIBLE);


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
                checked_number++;
                Toast toast = Toast.makeText(getApplicationContext(),checked_number+"명 체크됨.",Toast.LENGTH_LONG);
                toast.show();

            }
        });
//
    }


}
