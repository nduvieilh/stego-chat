package net.stegochat;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class EncryptActivity extends AppCompatActivity implements ChoiceDialogFragment.NoticeDialogListener {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static int RESULT_LOAD_IMG = 1;
    static View viewCopy;
    String imgDecodableString;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

                TextView txtView = (TextView) findViewById(R.id.imagePreviewText);
                ImageView imgView = (ImageView) findViewById(R.id.imagePreview);

                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(Util.resizeBitmap(mBitmap));
                txtView.setVisibility(View.INVISIBLE);
            } else {
                Toast.makeText(this, "You must pick an image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }
    /*
        Encrypts the message to be embedded into the image
        with the provided password making it impossible to
        read the message after it is extracted without the
        same password.

        @author Nicky Duvieilh

        @param string message the main message text
        @param string password the password to encrypt the message with

        @return string encrypted message
     */
    public String encryptMessage(String message, String password) {
        String output = message;
        try {
            output =  AESCrypt.encrypt(password, message);
        } catch(GeneralSecurityException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return output;
    }

    /*
        Checks if the EditText object has text or not for
        use in conditionals.

        @author Nicky Duvieilh

        @param EditText etText the text object to check

        @return Boolean return true if EditText is empty, false otherwise
     */

    public void getImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    /*
        Checks that all requirements are met and calls the
        share intent if all checks pass.  If the password
        is present then it will call the encryptMessage
        function to encrypt the message before it is shared

        @author Nicky Duvieilh

        @param View view used to create the snackbar to alert the user of status
     */
    public void sendMessage(View view) {
        final EditText messageText = (EditText) findViewById(R.id.encrypt_text);
        final EditText passwordText = (EditText) findViewById(R.id.encrypt_password);
        String message = messageText.getText().toString();
        String password = passwordText.getText().toString();

        // Alert user if no text is found in message
        if (Util.isEmptyText(messageText)) {
            // Create snackbar message to say that a message is required
            Toast.makeText(this, "A message is required",
                    Toast.LENGTH_LONG).show();
            // End the sendMessage() function before it can continue
            return;
        }


        // Check if image exists in global scope before trying
        // embed the message into it
        if(imgDecodableString == null) {
            // Check global path/image object to ensure it exists
            // Quit early otherwise
            Toast.makeText(this, "You must pick an image",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Checks to see if password exists to encrypt message before embedding
        if (!Util.isEmptyText(passwordText)) {
            message = encryptMessage(message, password);
        }

        String startSigil = "$*3d";

        byte start[] = startSigil.getBytes();
        byte msg[] = message.getBytes();
        byte len[] = bit_conversion(msg.length);

        Bitmap mBitmap = BitmapFactory.decodeFile(imgDecodableString);

        Bitmap encodedImage = encode(Util.resizeBitmap(mBitmap), len, 0);
        encodedImage = encode(encodedImage, start, 32);
        encodedImage = encode(encodedImage, msg, 64);

//        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();

        // Dont need to save it anymore(?)
        save_image(encodedImage);

        File myFile = new File(fileName);
        Uri uri = Uri.fromFile(myFile);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/png");
        startActivity(Intent.createChooser(sendIntent, "Share with"));
    }


    private Bitmap encode(Bitmap mBitmap, byte[] addition, int offset) {
        int mPhotoWidth = mBitmap.getWidth();
        int mPhotoHeight = mBitmap.getHeight();

        int[] pix = new int[mPhotoWidth * mPhotoHeight];
        mBitmap.getPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);

        for(int i=0; i<addition.length; ++i)
        {
            //loop through the 8 bits of each byte
            int add = addition[i];
            for(int bit=7; bit>=0; --bit, ++offset) //ensure the new offset value carries on through both loops
            {
                //assign an integer to b, shifted by bit spaces AND 1
                //a single bit of the current byte
                int b = (add >>> bit) & 1;
                //assign the bit by taking: [(previous byte value) AND 0xfe] OR bit to add
                //changes the last bit of the byte in the image to be the bit of addition
                pix[offset] = (pix[offset] & 0xFFFFFFFE) | b;
            }
        }
        Bitmap bm = Bitmap.createBitmap(mPhotoWidth, mPhotoHeight, Bitmap.Config.ARGB_8888);
        bm.setPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);
        return bm;
    }

    private byte[] bit_conversion(int i)
    {
        byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
        byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
        byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
        byte byte0 = (byte)((i & 0x000000FF)     );
        //{0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
        return(new byte[]{byte3,byte2,byte1,byte0});
    }

    // Probably don't need this anymore
    private void save_image(Bitmap encodedImage){
        FileOutputStream fileOutputStream = null;
        try {

            // create a File object for the parent directory
            File wallpaperDirectory = new File("/sdcard/stego-chat/");
            // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();

            fileName = wallpaperDirectory.getAbsolutePath();

            fileName = String.format("/sdcard/stego-chat/" + System.currentTimeMillis() + ".png");
            //Capture is folder name and file name with date and time
            fileOutputStream = new FileOutputStream(fileName);

            // Here we Resize the Image ...
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            encodedImage.compress(Bitmap.CompressFormat.PNG, 100,
                    byteArrayOutputStream); // bm is the bitmap object
            byte[] bsResized = byteArrayOutputStream.toByteArray();


            fileOutputStream.write(bsResized);
            fileOutputStream.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void onDialogPositiveClick(DialogFragment dialog) {
        getImage();
    }

    public void onDialogNegativeClick(DialogFragment dialog) {
        dispatchTakePictureIntent();
    }

    public void showDialog(View view) {
        viewCopy = view;
        DialogFragment newFragment = new ChoiceDialogFragment();
        newFragment.show(getFragmentManager(), "missiles");;
    }
}
