package org.capstone.findbuddies;

import android.Manifest;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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

import static android.app.Activity.RESULT_OK;

public class MemoEditFragment extends Fragment{
    FloatingActionButton PictureBut;
    private static final int GALLERY_CODE= 1000;
    private static final int LONG_GALLERY_CODE = 1001;
    private static final int CHOOSE_DETEC_IMAGE = 1002;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    ImageView PictureView;
    TextView PictureViewURI;
    Double ReadLatitude;
    Double ReadLongitude;
    String PicturePath;
    String myEmail;
    int GroupNo;
    String date;
    EditText Title;
    EditText Memo;
    int AddedPicture;
    int UploadedPic = 0;
    int READ = 0;


    Toolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_memo_edit,container,false);
        toolbar = rootView.findViewById(R.id.MemoToolbar);
//        toolbar.setTitle("Memo");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        myEmail = getArguments().getString("myEmail");
        GroupNo = getArguments().getInt("GroupNo");
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
        Title = view.findViewById(R.id.title_edit);
        Memo = view.findViewById(R.id.contents_edit);
        READ = getArguments().getInt("READ",0);
        if(READ==1){
            SettingMemoEdit();
        }


        PictureBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

                startActivityForResult(intent,GALLERY_CODE);
            }
        });

        PictureBut.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,LONG_GALLERY_CODE);
                return true;
            }
        });

        PictureView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PictureView.setImageURI(null);
                PictureView.setVisibility(View.GONE);
                PictureViewURI.setText(null);
                UploadedPic = 0;
                return false;
            }
        });
        PictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PictureViewURI.getText()!=null){
                    Intent intent = new Intent(getContext(),FullScreenImageView.class);
                    if(UploadedPic==1){
                        intent.putExtra("Uploaded",1);
                    }
                    intent.putExtra("imageUri",PictureViewURI.getText().toString());
                    startActivity(intent);
                }
            }
        });

    }

    private void SettingMemoEdit() {
        Title.setText(getArguments().getString("ReadTitle"));
        Memo.setText(getArguments().getString("ReadContent"));
        if(getArguments().getString("ReadPictureURI",null)!=null){
            StorageReference storageReference = storage.getReferenceFromUrl(getArguments().getString("ReadPictureURI"));
            PictureView.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(PictureView);
//            PictureView.setImageDrawabl(Uri.parse(getArguments().getString("ReadPictureURI")));
            PictureViewURI.setText(getArguments().getString("ReadPictureURI"));
            UploadedPic=1;
        }
        ReadLatitude = getArguments().getDouble("ReadLatitude",0);
        ReadLongitude = getArguments().getDouble("ReadLongitude",0);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE){
            if(data!=null){
                PicturePath = getPath(data.getData());
                Toast.makeText(getContext(), ""+PicturePath, Toast.LENGTH_SHORT).show();
                Uri file = Uri.fromFile(new File(getPath(data.getData())));
                PictureView.setImageURI(file);
                PictureView.setVisibility(View.VISIBLE);
//                UploadUri(file);
                PictureViewURI.setText(file.toString());
                AddedPicture = 1;
                UploadedPic =0;
            }

        }
        else if(requestCode == LONG_GALLERY_CODE){
            if(data!=null){
                PicturePath = getPath(data.getData());
                Uri file = Uri.fromFile(new File(getPath(data.getData())));
                PictureView.setImageURI(file);
                PictureViewURI.setText(file.toString());
                Intent intent = new Intent(getContext(),ImageTextDetect.class);
                intent.putExtra("imageUri",file.toString());
                startActivityForResult(intent,CHOOSE_DETEC_IMAGE);
            }

        }
        else if(requestCode == CHOOSE_DETEC_IMAGE){
            if(resultCode == RESULT_OK){
                String resultText = data.getStringExtra("resultText");
                if(resultText.equals("NOTHING")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("읽을 수 있는 \n텍스트가 없습니다.");
                    builder.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else {
                    String resultMemo;
                    if (Memo.getText() == null) {
                        resultMemo = "";
                    } else {
                        resultMemo = Memo.getText().toString();
                    }
                    resultMemo += resultText;
                    Memo.setText(resultMemo);
                }

            }
            else {
                Toast.makeText(getContext(), "텍스트 불러오기 실패", Toast.LENGTH_SHORT).show();
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
    public void Upload(){
        if(PictureViewURI.getText()!=null){
            Log.d("ParsingTest","PIC: "+UploadedPic);
            if(UploadedPic==1){
                mofidifyMemo(PictureViewURI.getText().toString());
            }
            else {
                StorageReference storageRef = storage.getReferenceFromUrl("gs://map-api-187214.appspot.com");
                Uri file = Uri.parse(PictureViewURI.getText().toString());
                StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(file);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        mofidifyMemo(downloadUrl.toString());

                    }
                });
            }
        }
        else {
            mofidifyMemo(null);
        }
    }
    private void mofidifyMemo(String URI){
        String uidKey = getArguments().getString("UidKey");
        SaveMemo saveMemo = new SaveMemo();
        saveMemo.setUploaderEmail(getArguments().getString("ReadUploader"));
        saveMemo.setTitle(Title.getText().toString());
        saveMemo.setMemo(Memo.getText().toString());
        if(URI!=null){
            saveMemo.setImageUrl(URI);
        }
        saveMemo.setLatitude(ReadLatitude);
        saveMemo.setLatitude(ReadLongitude);
        saveMemo.setCheckGroupNo(GroupNo);

        long Now = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss", Locale.KOREA);
        String date = simpleDateFormat.format(new Date(Now));

        saveMemo.setLastEditDate(date);
        saveMemo.setEditSystemTime(Now);
        saveMemo.setYear(getArguments().getInt("ReadYear",0));
        saveMemo.setMonth(getArguments().getInt("ReadMonth",0));
        saveMemo.setDate(getArguments().getInt("ReadDay",0));
        saveMemo.setHour(getArguments().getInt("ReadHour",0));
        saveMemo.setMinute(getArguments().getInt("ReadMinute",0));

        database.getReference().child("MemoList").child(uidKey).setValue(saveMemo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Bundle bundle = new Bundle();
                bundle.putString("myEmail",myEmail);
                MemoList memoList = new MemoList();
                memoList.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_main,memoList)
                        .commit();
            }
        });
    }

}
