package org.capstone.findbuddies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FullScreenImageView extends AppCompatActivity {
    ImageView FullScreenImageView;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_view);
        storage = FirebaseStorage.getInstance();
        FullScreenImageView = findViewById(R.id.full_screen_image);
        String ImageURI = getIntent().getStringExtra("imageUri");
        if(getIntent().getIntExtra("Uploaded",0)==1){
            StorageReference storageReference = storage.getReferenceFromUrl(ImageURI);
            Glide.with(getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(FullScreenImageView);
        }
        else {
            Uri uri = Uri.parse(ImageURI);
            FullScreenImageView.setImageURI(uri);
        }
    }
}
