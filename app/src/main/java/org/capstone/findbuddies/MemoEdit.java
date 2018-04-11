package org.capstone.findbuddies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MemoEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contents,new MemoEditFragment())
                .commit();
    }
}
