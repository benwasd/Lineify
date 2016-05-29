package ch.bfh.ti.lineify.ui.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.ui.adapter.TrackRecyclerViewAdapter;

public class HistoryFragment extends Fragment {
    private final IWayPointRepository wayPointRepository;
    private final TrackRecyclerViewAdapter recyclerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    public HistoryFragment() {
        this.wayPointRepository = DI.container().resolve(IWayPointRepository.class);
        this.recyclerAdapter = new TrackRecyclerViewAdapter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_lineify_history, container, false);
        this.findViews(fragmentView);
        this.initializeViews();

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.loadTracks();
    }

    private void findViews(View view) {
        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.trackSwipeRefreshLayout);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.trackRecyclerView);
    }

    private void initializeViews() {
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        this.recyclerView.setAdapter(this.recyclerAdapter);

        this.swipeRefreshLayout.setOnRefreshListener(() -> {
            this.loadTracks();
        });
    }

    private void loadTracks() {
        String userEmail = Settings.Secure.getString(this.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID); // TODO: Replace if we have user managment
        this.wayPointRepository.getTracks(userEmail).subscribe(
            t -> this.recyclerAdapter.setData(t),
            e -> Log.i("HistoryFragment", "Error getting tracks.", e),
            () -> this.swipeRefreshLayout.setRefreshing(false)
        );
    }
}