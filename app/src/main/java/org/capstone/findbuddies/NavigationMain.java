package org.capstone.findbuddies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NavigationMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FloatingActionButton fab;
    String myEmail;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    Bundle bundle;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);
        Toolbar toolbar = findViewById(R.id.MemoToolbar);
//        setSupportActionBar(toolbar);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        myEmail = getIntent().getStringExtra("myEmail");
        bundle = new Bundle();
        bundle.putString("myEmail",myEmail);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_main,new MainMapFragment())
                .commit();


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                MemoEditFragment memoEditFragment = new MemoEditFragment();
                memoEditFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_main,memoEditFragment)
                        .commit();
                fab.setVisibility(View.INVISIBLE);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else if(0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            super.onBackPressed();
        }else{
            backPressedTime = tempTime;
            Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.special){
            Intent intent = new Intent(this,ParsingMemo.class);
            EditText titleEdit = findViewById(R.id.title_edit);
            EditText contentEdit = findViewById(R.id.contents_edit);
            intent.putExtra("title",titleEdit.getText().toString());
            intent.putExtra("content",contentEdit.getText().toString());
            startActivity(intent);
            return true;
        }

        if (id == R.id.save) {

            TextView PictureViewURI = findViewById(R.id.PictureViewURI);
            Toast.makeText(this, PictureViewURI.getText(), Toast.LENGTH_SHORT).show();
            if(!PictureViewURI.getText().toString().equals("")){
                StorageReference storageRef = storage.getReferenceFromUrl("gs://map-api-187214.appspot.com");
                Uri file = Uri.parse(PictureViewURI.getText().toString());
                StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(NavigationMain.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        UploadMemo(downloadUrl.toString());

                    }
                });
            }
            else {
                UploadMemo(null);
            }


        }

        return super.onOptionsItemSelected(item);
    }

    public void UploadMemo(String URI){
        long Now = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss", Locale.KOREA);
        String date = simpleDateFormat.format(new Date(Now));
        SaveMemo saveMemo = new SaveMemo();
        EditText Title = findViewById(R.id.title_edit);
        EditText Memo = findViewById(R.id.contents_edit);

        if(URI!=null){
            saveMemo.setImageUrl(URI);
        }
        saveMemo.setUploaderEmail(myEmail);
        saveMemo.setLastEditDate(date);
        saveMemo.setEditSystemTime(Now);
        saveMemo.setCheckGroupMemo(false);
        saveMemo.setTitle(Title.getText().toString());
        saveMemo.setMemo(Memo.getText().toString());
        saveMemo.setYear(0);
        saveMemo.setMonth(0);
        saveMemo.setDay(0);

        database.getReference().child("MemoList").push().setValue(saveMemo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NavigationMain.this, "업로드 완료!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        fab.setVisibility(View.VISIBLE);

        int id = item.getItemId();
        if(id == R.id.map) {
            MainMapFragment mainMapFragment = new MainMapFragment();
            mainMapFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,mainMapFragment)
                    .commit();
        }
        else if (id == R.id.note) {
            MemoList memoList = new MemoList();
            memoList.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,memoList)
                    .commit();

        } else if (id == R.id.calendar) {
            CalendarFragment calendarFragment = new CalendarFragment();
            calendarFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,calendarFragment)
                    .commit();
        } else if (id == R.id.group) {
            Intent intent = new Intent(getApplicationContext(),messenger.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("myEmail",myEmail);
            startActivity(intent);
        } else if (id == R.id.account) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
}
