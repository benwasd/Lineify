package ch.bfh.ti.lineify.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.ui.Main;

public class StartTrackingDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Main mainActivity = (Main) this.getActivity();
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_track, null);
        EditText trackIdentifier = (EditText) view.findViewById(R.id.trackIdentifierEditText);
        trackIdentifier.setText(Track.defaultTrackIdentifier());
        trackIdentifier.setSelection(trackIdentifier.getText().length());

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setView(view)
            .setPositiveButton("Starten", (dialog, id) -> {
                String userEmail = Settings.Secure.getString(mainActivity.getContentResolver(), Settings.Secure.ANDROID_ID); // TODO: Replace if we have user managment
                String text = trackIdentifier.getText().toString();

                if (text.equals(null) || text.equals("")) {
                    text = Track.defaultTrackIdentifier();
                }

                mainActivity.startTracker(new Track(userEmail, text));
            })
            .setNegativeButton("Abbrechen", (dialog, id) -> this.getDialog().cancel());

        return builder.create();
    }
}