package net.stegochat;

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
import android.widget.Toast;

public class EncryptActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /*
        Encrypts the message to be embedded into the image
        with the provided password making it impossible to
        read the message after it is extracted without the
        same password.

        @author Nicky Duvieilh, Thomas Wilson

        @param string message the main message text
        @param string password the password to encrypt the message with

        @return string encrypted message
     */
    public String encryptMessage(String message, String password) {
        // TODO: create the encryption code - Thomas
        return "[" + password + "] " + message; // Temporary
    }

    /*
        Checks if the EditText object has text or not for
        use in conditionals.

        @author Nicky Duvieilh

        @param EditText etText the text object to check

        @return Boolean return true if EditText is empty, false otherwise
     */
    public Boolean isEmptyText(EditText etText) {
        if(etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
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

//                encode(imgDecodableString);

//                Bitmap mBitmap = BitmapFactory.decodeFile(imgDecodableString);
//                int mPhotoWidth = mBitmap.getWidth();
//                int mPhotoHeight = mBitmap.getHeight();
//
//                int[] pix = new int[mPhotoWidth * mPhotoHeight];
//                mBitmap.getPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);

                ImageView imgView = (ImageView) findViewById(R.id.imagePreview);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

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
        if (isEmptyText(messageText)) {
            // Create snackbar message to say that a message is required
            Snackbar.make(view, "A message is required", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            // End the sendMessage() function before it can continue
            return;
        }

        // Check if image exists in global scope before trying
        // embed the message into it
        if(true) { // Todo: Write conditional to ensure image exists
            // Check global path/image object to ensure it exists
            // and is of the correct type

            // Check that image is of correct type
            if(true) {
                // Todo: Write function to ensure image is of correct type
            }

            // Check that image is of suitable size
            if(true) {
                // Todo: Write function to check size
            }
        } else {
            // Quits sendMessage() if no image exists
            return;
        }

        // Checks to see if password exists to encrypt message before embedding
        if (!isEmptyText(passwordText)) {
            message = encryptMessage(message, password);
        }

        // Todo: Remove temporary snackbar
        Snackbar.make(view, message + " " + password, Snackbar.LENGTH_LONG).setAction("Action", null).show();

        //embedImage()
    }


    //FIXME actually make these work
    //The below three functions are used to embed the text in the image, check out that link
    //on steganography in java for the source code to this

    //This is the one I planned on using once I figured out how all of this worked
    private void encode(String imgDecodableString, int offset) {
        Bitmap mBitmap = BitmapFactory.decodeFile(imgDecodableString);
        int mPhotoWidth = mBitmap.getWidth();
        int mPhotoHeight = mBitmap.getHeight();

        int[] pix = new int[mPhotoWidth * mPhotoHeight];
        mBitmap.getPixels(pix, 0, mPhotoWidth, 0, 0, mPhotoWidth, mPhotoHeight);

        int index;
        for (int y = 0; y < mPhotoHeight; y++) {
            for (int x = 0; x < mPhotoWidth; x++) {
                index = y * mPhotoWidth + x;
            }
        }
    }

    //These two aren't majorly useful yet, but I'm working them out.
    private byte[] bit_conversion(int i)
    {
        byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
        byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
        byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
        byte byte0 = (byte)((i & 0x000000FF)     );
        //{0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
        return(new byte[]{byte3,byte2,byte1,byte0});
    }

    private byte[] encode_text(byte[] image, byte[] addition, int offset)
    {
        //check that the data + offset will fit in the image
        if(addition.length + offset > image.length)
        {
            throw new IllegalArgumentException("File not long enough!");
        }
        //loop through each addition byte
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
                image[offset] = (byte)((image[offset] & 0xFE) | b );
            }
        }
        return image;
    }
}
