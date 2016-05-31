package ch.bfh.ti.lineify.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.ui.activities.Main;

public class StartTrackingDialog extends DialogFragment {
    private final Main mainActivity;
    private EditText trackIdentifierEditText;

    public StartTrackingDialog(Main mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(this.mainActivity)
            .setView(this.onCreateView(this.mainActivity.getLayoutInflater()))
            .setPositiveButton(R.string.start, (dialog, id) -> this.start())
            .setNegativeButton(R.string.abort, (dialog, id) -> this.cancel())
            .create();
    }

    public View onCreateView(LayoutInflater inflater) {
        View dialogView = inflater.inflate(R.layout.dialog_track_identifier, null, false);
        this.findViews(dialogView);
        this.initializeViews();

        return dialogView;
    }

    private void findViews(View dialogView) {
        this.trackIdentifierEditText = (EditText) dialogView.findViewById(R.id.trackIdentifierEditText);
    }

    private void initializeViews() {
        this.trackIdentifierEditText.setText(Track.defaultIdentifier());
        this.trackIdentifierEditText.setSelection(trackIdentifierEditText.getText().length());
    }

    private void start() {
        String userEmail = UserManagement.getCurrentUsersEmail(this.getActivity());
        String text = this.trackIdentifierEditText.getText().toString();

        if (text.equals(null) || text.equals("")) {
            text = Track.defaultIdentifier();
        }

        this.mainActivity.startTracker(new Track(userEmail, text));
    }

    private void cancel() {
        this.getDialog().cancel();
    }
}