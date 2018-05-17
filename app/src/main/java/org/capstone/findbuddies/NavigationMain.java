package org.capstone.findbuddies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NavigationMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String myEmail;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    Bundle bundle;
    String parsingContent;
    EditText titleEdit;
    EditText contentEdit;
    TextView UserID;
    TextView UserName;
    int isEdit = 0;
    NavigationView navigationView;
    MainMapFragment mainMapFragment = new MainMapFragment();
    MemoEditFragment memoEditFragment = new MemoEditFragment();
    MemoList memoList = new MemoList();
    CalendarFragment calendarFragment = new CalendarFragment();
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private static final int GROUP_MEMO_NO = 3000;
    public static final int RETURN_SPECIAL = 3001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);
        Toolbar toolbar = findViewById(R.id.MemoToolbar);
        navigationView = findViewById(R.id.nav_view);
//        setSupportActionBar(toolbar);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        myEmail = getIntent().getStringExtra("myEmail");
        setIDName(myEmail);
        bundle = new Bundle();
        bundle.putString("myEmail",myEmail);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_main,new MainMapFragment())
                .commit();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setIDName(String myEmail) {
        View view = navigationView.getHeaderView(0);
        UserID = view.findViewById(R.id.userID);
        UserName = view.findViewById(R.id.userName);
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if (value != null && (value.getSavedEmail()).equals(myEmail)) {
                        UserID.setText(value.getSavedID());
                        UserName.setText(value.getSavedName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }else if(isEdit==1) {
            memoList.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,memoList)
                    .commit();
            isEdit=0;
        } else if(isEdit==2) {
            calendarFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,calendarFragment)
                    .commit();
            isEdit = 0;
        }
        else if(0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
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
            MemoEditFragment memoEditFragment = (MemoEditFragment)getSupportFragmentManager().findFragmentById(R.id.nav_main);
            if(memoEditFragment.READ==0) {
                Intent intent = new Intent(this, ParsingMemo.class);
                titleEdit = findViewById(R.id.title_edit);
                contentEdit = findViewById(R.id.contents_edit);
                TextView PictureViewURI = findViewById(R.id.PictureViewURI);
                intent.putExtra("myEmail", myEmail);
                Log.d("ParsingTest","No3 : "+memoEditFragment.GroupNo);
                intent.putExtra("GroupNo", memoEditFragment.GroupNo);
                intent.putExtra("title", titleEdit.getText().toString());
                intent.putExtra("content", contentEdit.getText().toString());
                intent.putExtra("pictureViewURI", PictureViewURI.getText().toString());
                intent.putExtra("CalendarAdd",memoEditFragment.CalendarAdd);
                if(memoEditFragment.CalendarAdd==1){
                    intent.putExtra("Year",memoEditFragment.Year);
                    intent.putExtra("Month",memoEditFragment.Month);
                    intent.putExtra("Date",memoEditFragment.Date);
                }
                startActivityForResult(intent, RETURN_SPECIAL);
            }
            else {
                memoEditFragment.MoveToSpecial();
            }
            return true;
        }

        if (id == R.id.save) {
            MemoEditFragment memoEditFragment = (MemoEditFragment)getSupportFragmentManager().findFragmentById(R.id.nav_main);
            if(memoEditFragment.READ==0) {
                TextView PictureViewURI = findViewById(R.id.PictureViewURI);
                Toast.makeText(this, PictureViewURI.getText(), Toast.LENGTH_SHORT).show();
                if (!PictureViewURI.getText().toString().equals("")) {
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://map-api-187214.appspot.com");
                    Uri file = Uri.parse(PictureViewURI.getText().toString());
                    StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
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
                } else {
                    UploadMemo(null);
                }
            }
            else if(memoEditFragment.READ==1){
                memoEditFragment.Upload();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void UploadMemo(String URI){
        long Now = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss", Locale.KOREA);
        MemoEditFragment memoEditFragment = (MemoEditFragment)getSupportFragmentManager().findFragmentById(R.id.nav_main);
        String date = simpleDateFormat.format(new Date(Now));
        SaveMemo saveMemo = new SaveMemo();
        titleEdit = findViewById(R.id.title_edit);
        contentEdit = findViewById(R.id.contents_edit);

        if(URI!=null){
            saveMemo.setImageUrl(URI);
        }
        saveMemo.setUploaderEmail(myEmail);
        saveMemo.setLastEditDate(date);
        saveMemo.setEditSystemTime(Now);
        Log.d("ParsingTest","No2 : "+memoEditFragment.GroupNo);
        saveMemo.setCheckGroupNo(memoEditFragment.GroupNo);
        saveMemo.setTitle(titleEdit.getText().toString());
        saveMemo.setMemo(contentEdit.getText().toString());
        if(memoEditFragment.CalendarAdd==1){
            saveMemo.setYear(memoEditFragment.Year);
            saveMemo.setMonth(memoEditFragment.Month);
            saveMemo.setDate(memoEditFragment.Date);
            saveMemo.setHour(12);
            saveMemo.setMinute(0);
        }

        database.getReference().child("MemoList").push().setValue(saveMemo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NavigationMain.this, "업로드 완료!", Toast.LENGTH_SHORT).show();
                if(memoEditFragment.CalendarAdd==0) {
                    memoList.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_main, memoList)
                            .commit();
                    isEdit = 0;
                }
                else if(memoEditFragment.CalendarAdd==1){
                    calendarFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_main, calendarFragment)
                            .commit();
                    isEdit = 0;
                }
            }
        });
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();
        if(id == R.id.map) {

            mainMapFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,mainMapFragment)
                    .commit();
            isEdit = 0;
        }
        else if (id == R.id.note) {

            memoList.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,memoList)
                    .commit();
            isEdit = 0;
        } else if (id == R.id.calendar) {

            calendarFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,calendarFragment)
                    .commit();
            isEdit = 0;
        } else if (id == R.id.group) {
            Intent intent = new Intent(getApplicationContext(),messenger.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("myEmail",myEmail);
            isEdit = 0;
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RETURN_SPECIAL){
            if(resultCode==RESULT_OK){
                if(memoEditFragment.CalendarAdd==0) {
                    memoList.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_main, memoList)
                            .commit();
                    isEdit = 0;
                }
                else if(memoEditFragment.CalendarAdd==1){
                    calendarFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_main, calendarFragment)
                            .commit();
                    isEdit = 0;
                }
            }
        }
    }
}
