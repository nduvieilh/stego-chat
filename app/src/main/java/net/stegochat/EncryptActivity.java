package net.stegochat;

import android.content.Intent;
import android.database.Cursor;
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
}
