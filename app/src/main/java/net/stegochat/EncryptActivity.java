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

//                String text = "This is a long test string so I can see how long I can really make this thing by god this line is long";
//                byte msg[] = text.getBytes();
//                byte len[] = bit_conversion(msg.length);

                Bitmap mBitmap = BitmapFactory.decodeFile(imgDecodableString);

//                Bitmap encodedImage = encode(mBitmap, len, 0);
//                encodedImage = encode(encodedImage, msg, 32);

                ImageView imgView = (ImageView) findViewById(R.id.imagePreview);
//                imgView.setImageBitmap(encodedImage);

//                 Set the Image in ImageView after decoding the String
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

//        String text = "This is a long test string so I can see how long I can really make this thing by god this line is long";
        byte msg[] = message.getBytes();
        byte len[] = bit_conversion(msg.length);

        Bitmap mBitmap = BitmapFactory.decodeFile(imgDecodableString);

        Bitmap encodedImage = encode(mBitmap, len, 0);
        encodedImage = encode(encodedImage, msg, 32);

        Snackbar.make(view, decode(encodedImage) + " " + password, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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


    private byte[] bit_conversion(int i)
    {
        byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
        byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
        byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
        byte byte0 = (byte)((i & 0x000000FF)     );
        //{0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
        return(new byte[]{byte3,byte2,byte1,byte0});
    }
}
