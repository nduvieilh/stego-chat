package net.stegochat;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class ChoiceDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Choose an image")
                .setPositiveButton("Existing picture", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getActivity().getApplicationContext(), EncryptActivity.class);
                        i.putExtra("img", true);
                        startActivity(i);
                    }
                })
                .setNegativeButton("Take new picture", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getActivity().getApplicationContext(), EncryptActivity.class);
                        i.putExtra("img", false);
                        startActivity(i);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
