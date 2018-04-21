package org.capstone.findbuddies;

import android.Manifest;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MemoEditFragment extends Fragment{
    FloatingActionButton PictureBut;
    private static final int GALLERY_CODE= 1000;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    ImageView PictureView;
    TextView PictureViewURI;
    String PicturePath;
    String myEmail;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss", Locale.KOREA);
    String date;
    EditText Title;
    EditText Memo;
    int AddedPicture;

    Toolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_memo_edit,container,false);
        toolbar = rootView.findViewById(R.id.MemoToolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        myEmail = getArguments().getString("myEmail");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        FloatingActionButton fab = view.findViewById(R.id.fab);
//        fab.setVisibility(View.GONE);
        /*권한*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        AddedPicture = 0;
        PictureBut = view.findViewById(R.id.AddPictureBut);
        PictureView = view.findViewById(R.id.PictureView);
        PictureViewURI = view.findViewById(R.id.PictureViewURI);


        PictureBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

                startActivityForResult(intent,GALLERY_CODE);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == GALLERY_CODE){
            if(data!=null){
                PicturePath = getPath(data.getData());
                Uri file = Uri.fromFile(new File(getPath(data.getData())));

                PictureView.setImageURI(file);
//                UploadUri(file);
                PictureViewURI.setText(file.toString());
                AddedPicture = 1;
            }

        }
    }


    public String getPath(Uri uri){
        String [] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getContext(),uri,proj,null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }

    public void UploadUri(Uri file){
        StorageReference storageRef = storage.getReferenceFromUrl("gs://map-api-187214.appspot.com");
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    downloadUrl.toString();//>>이미지 path


            }
        });
    }

    public void UploadMemo(String uri){
        long Now = System.currentTimeMillis();
        date = simpleDateFormat.format(new Date(Now));

        SaveMemo saveMemo = new SaveMemo();
        Title = getView().findViewById(R.id.title_edit);
        Memo = getView().findViewById(R.id.contents_edit);

        saveMemo.setImageUrl(uri.toString());
        saveMemo.setUploaderEmail(myEmail);
        saveMemo.setLastEditDate(date);
        saveMemo.setEditSystemTime(Now);
        saveMemo.setCheckGroupMemo(false);
        saveMemo.setTitle(Title.toString());
        saveMemo.setMemo(Memo.toString());
        saveMemo.setYear(0);
        saveMemo.setMonth(0);
        saveMemo.setDay(0);

        database.getReference().child("MemoList").push().setValue(saveMemo);
    }
}
