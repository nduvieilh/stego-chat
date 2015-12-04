package net.stegochat;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.scottyab.aescrypt.AESCrypt;
import java.security.GeneralSecurityException;

public class DecryptActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                Bitmap mBitmap = BitmapFactory.decodeFile(imgDecodableString);

                int [] sizes = new int[2];
                sizes = resizeBitmap(mBitmap, sizes);

                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        mBitmap, sizes[0], sizes[1], false);

                TextView txtView = (TextView) findViewById(R.id.imagePreviewText);
                ImageView imgView = (ImageView) findViewById(R.id.imagePreview);

                // Set the Image in TextView after decoding the String
                imgView.setImageBitmap(resizedBitmap);
                txtView.setVisibility(View.INVISIBLE);

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void getImage(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public String decryptMessage(String message, String password) {
        String output = message;
        try {
            output = AESCrypt.decrypt(password, message);
        } catch(GeneralSecurityException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return output;
    }

    public void receiveMessage(View view){
        final EditText passwordText = (EditText) findViewById(R.id.decrypt_password);
        String password = passwordText.getText().toString();
        Bitmap encodedImage = BitmapFactory.decodeFile(imgDecodableString);
        String message = new String(decode(encodedImage));

        TextView txtDecrypt = (TextView) findViewById(R.id.decrypt_text);
        // TODO: remove comments on line after moving isEmptyText to utils
        //if (isEmptyText(passwordText)) {
            message = decryptMessage(message, password);
        //}

        txtDecrypt.setText(message);
        txtDecrypt.setVisibility(View.VISIBLE);
    }

    private byte[] decode(Bitmap mBitmap)
    {
        int mPhotoWidth = mBitmap.getWidth();
        int mPhotoHeight = mBitmap.getHeight();

        int[] pix = new int[mPhotoWidth * mPhotoHeight];
        mBitmap.getPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);

        int length = 0;
        int offset  = 32;
        //loop through 32 bytes of data to determine text length
        for(int i=0; i<32; ++i) //i=24 will also work, as only the 4th byte contains real data
        {
            length = (length << 1) | (pix[i] & 1);
        }

        byte[] result = new byte[length];

        //loop through each byte of text
        for(int b=0; b<result.length; ++b )
        {
            //loop through each bit within a byte of text
            for(int i=0; i<8; ++i, ++offset)
            {
                //assign bit: [(new byte value) << 1] OR [(text byte) AND 1]
                result[b] = (byte)((result[b] << 1) | (pix[offset] & 1));
            }
        }
        return result;
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getContentResolver().query(imageUri,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            Bitmap mBitmap = BitmapFactory.decodeFile(imgDecodableString);

            int [] sizes = new int[2];
            sizes = resizeBitmap(mBitmap, sizes);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    mBitmap, sizes[0], sizes[1], false);


            ImageView imgView = (ImageView) findViewById(R.id.imagePreview);

            // Set the Image in ImageView after decoding the String
            imgView.setImageBitmap(resizedBitmap);

        }
    }

    private int[] resizeBitmap(Bitmap mBitmap, int [] sizes){

        sizes[0] = mBitmap.getWidth();
        sizes[1] = mBitmap.getHeight();
        while(sizes[0] > 2000 || sizes[1] > 2000){
            //FIXME Stop being so bad
            if ((sizes[0] > 2000 && sizes[1] < 2000) || (sizes[1] > 2000 && sizes[0] < 2000) || (sizes[1] > 2000 && sizes[0] > 2000)) {
                sizes[0] = sizes[0] / 2;
                sizes[1] = sizes[1] / 2;
            }
        }

        return sizes;
    }


}
