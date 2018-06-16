package org.capstone.findbuddies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowImageView extends AppCompatActivity {
    ImageView ShowImage;
    TextView UploadBut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_view);
        ShowImage = findViewById(R.id.show_image);
        UploadBut = findViewById(R.id.upload_but);
        String ImageURI = getIntent().getStringExtra("imageUri");
        Uri uri = Uri.parse(ImageURI);
        ShowImage.setImageURI(uri);

        UploadBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK,getIntent());
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED,getIntent());
        finish();
    }
}
