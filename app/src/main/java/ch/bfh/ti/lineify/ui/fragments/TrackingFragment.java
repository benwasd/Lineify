package ch.bfh.ti.lineify.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class TrackingFragment extends Fragment {
    private TextView altitudeTextView;
    private TextView trackIdentifierTextView;
    private TextView wayPointsTextView;
    private TextView accuracyTextView;
    private LinearLayout trackingInfoLinearLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_tracking, container, false);
        this.findViews(fragmentView);
        this.initializeViews();

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void findViews(View fragmentView) {
        this.altitudeTextView = (TextView) fragmentView.findViewById(R.id.altitudeTextView);
        this.trackIdentifierTextView = (TextView) fragmentView.findViewById(R.id.trackIdentifierTextView);
        this.wayPointsTextView = (TextView) fragmentView.findViewById(R.id.wayPointsTextView);
        this.accuracyTextView = (TextView) fragmentView.findViewById(R.id.accuracyTextView);
        this.trackingInfoLinearLayout = (LinearLayout) fragmentView.findViewById(R.id.trackingInfoLinearLayout);
    }

    private void initializeViews() {
        this.bindTrack(null);
        this.bindWayPoint(null);
    }

    public void onServiceStart(Track track) {
        this.bindTrack(track);
        this.bindWayPoint(null);
    }

    public void onReceiveWayPoint(Track track, WayPoint wayPoint) {
        this.bindTrack(track);
        this.bindWayPoint(wayPoint);
    }

    public void onServiceStop() {
        this.bindTrack(null);
        this.bindWayPoint(null);
    }

    private void bindTrack(Track track) {
        if (track != null) {
            this.trackIdentifierTextView.setText(track.identifier());
            this.wayPointsTextView.setText(String.format("%d", track.wayPointCount()));

            this.trackingInfoLinearLayout.setVisibility(View.VISIBLE);
        }
        else {
            this.trackingInfoLinearLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void bindWayPoint(WayPoint wayPoint) {
        if (wayPoint != null) {
            this.altitudeTextView.setText(String.format("%.0f", wayPoint.altitude()));
            this.accuracyTextView.setText(String.format("%.0f", wayPoint.accuracy()));
        }
        else {
            this.altitudeTextView.setText("- - -");
            this.accuracyTextView.setText("0");
        }
    }
}