package com.example.selfie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class SelectFromAlbum extends AppCompatActivity {

    ImageView imageView;
    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_from_album);

        imageView = (ImageView) findViewById(R.id.imageView2);

        Intent intent = getIntent();

        uri = intent.getParcelableExtra("Pictrue");
        imageView.setImageURI(uri);


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }


}
