package ch.bfh.ti.lineify.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class TrackingFragment extends Fragment {
    private TextView aaaaaaasdTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_lineify_main, container, false);
        this.initializeViews(fragmentView);

        return fragmentView;
    }

    public void initializeViews(View fragmentView) {
        this.aaaaaaasdTextView = (TextView) fragmentView.findViewById(R.id.aaaaaaasdTextView);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public void onReceiveWayPoint(Track track, WayPoint wayPoint) {
        this.aaaaaaasdTextView.setText(track.identifier() + "as" + track.userEmail() + "xx" + wayPoint.altitude()+"m, " +wayPoint.latitude()+" | " +wayPoint.longitude()+" | "+wayPoint.accuracy());
    }
}