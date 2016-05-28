package ch.bfh.ti.lineify.ui.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.ui.adapter.TrackRecyclerViewAdapter;

public class HistoryFragment extends Fragment {
    private final IWayPointRepository wayPointRepository;

    public HistoryFragment() {
        this.wayPointRepository =DI.container().resolve(IWayPointRepository.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.initializeRecyclerView(inflater, container);
    }

    private RecyclerView initializeRecyclerView(LayoutInflater inflater, ViewGroup container) {
        String userEmail = Settings.Secure.getString(this.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID); // TODO: Replace if we have user managment

        TrackRecyclerViewAdapter recyclerAdapter = new TrackRecyclerViewAdapter();
        recyclerAdapter.setData(this.wayPointRepository.getTracks(userEmail));

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_lineify_history, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        return recyclerView;
    }
}