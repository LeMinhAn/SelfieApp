package com.example.selfie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EditActivity extends Activity {

    public static final String EXTRA_BYTES = "BMP";

    private static final int MAX_NUMBER_OF_FACES = 10;
    private static final float GLASSES_SCALE_CONSTANT = 2.5f;
    private static final float HAT_SCALE_CONSTANT = 1.5f;
    private static final float HAT_OFFSET = 2.5f;
    private static final float TIE_SCALE_CONSTANT = 1f;
    private static final float TIE_OFFSET = 2.2f;

    private int NUMBER_OF_FACE_DETECTED;

    private FaceDetector.Face[] detectedFaces;

    private Bitmap mBitmap;
    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        byte[] bytes = getIntent().getByteArrayExtra(EXTRA_BYTES);
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inPreferredConfig = Bitmap.Config.RGB_565;
        bfo.inScaled = false;
        bfo.inDither = false;
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bfo);

        // Rotate the bitmap
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bmp, 0, 0,
                bmp.getWidth(), bmp.getHeight(), matrix, true);
        bmp.recycle();

        int width = rotatedBitmap.getWidth();
        int height = rotatedBitmap.getHeight();
        detectedFaces = new FaceDetector.Face[MAX_NUMBER_OF_FACES];
        FaceDetector faceDetector = new FaceDetector(width, height, MAX_NUMBER_OF_FACES);
        NUMBER_OF_FACE_DETECTED = faceDetector.findFaces(rotatedBitmap, detectedFaces);

        decorateFacesOnBitmap(rotatedBitmap);

        mBitmap = rotatedBitmap;
        mImageView = (ImageView) findViewById(R.id.imageView1);
        mImageView.setImageDrawable(new BitmapDrawable(mBitmap));


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Helper method to scale each object (hat, glasses, tie) to the size of the face
     */
    private Bitmap scaleObjectToFace(FaceDetector.Face face, Bitmap object, float scaleConstant) {
        float newWidth = face.eyesDistance() * scaleConstant;
        float scaleFactor = newWidth / object.getWidth();
        return Bitmap.createScaledBitmap(object, Math.round(newWidth),
                Math.round(object.getHeight() * scaleFactor), false);
    }

    /**
     * Method iterates through the faces and decorates each with
     * a properly sized and placed hat, glasses, and tie
     */
    private void decorateFacesOnBitmap(Bitmap tempBitmap) {
        Canvas canvas = new Canvas(tempBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        for (int count = 0; count < NUMBER_OF_FACE_DETECTED; count++) {
            FaceDetector.Face face = detectedFaces[count];

            PointF midPoint = new PointF();
            face.getMidPoint(midPoint);

            // Put the glasses on the face
            Bitmap glasses = BitmapFactory.decodeResource(getResources(),
                    R.drawable.glasses);
            glasses = scaleObjectToFace(face, glasses, GLASSES_SCALE_CONSTANT);
            canvas.drawBitmap(glasses, midPoint.x - glasses.getWidth() / 2,
                    midPoint.y - glasses.getHeight() / 2, paint);

            // Put the hat on the head
            Bitmap hat = BitmapFactory.decodeResource(getResources(),
                    R.drawable.party_hat);
            hat = scaleObjectToFace(face, hat, HAT_SCALE_CONSTANT);
            float hatTop = midPoint.y - HAT_OFFSET * face.eyesDistance();
            canvas.drawBitmap(hat, midPoint.x - hat.getWidth() / 2,
                    hatTop - hat.getHeight() / 2, paint);

            // Put on the tie beneath the head
            Bitmap tie = BitmapFactory.decodeResource(getResources(),
                    R.drawable.tie);
            tie = scaleObjectToFace(face, tie, TIE_SCALE_CONSTANT);
            float tieTop = midPoint.y + TIE_OFFSET * face.eyesDistance();
            canvas.drawBitmap(tie, midPoint.x - tie.getWidth() / 2,
                    tieTop, paint);
        }
    }
}