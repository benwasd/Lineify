package ch.bfh.ti.lineify.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.ui.activities.TrackDetail;
import ch.bfh.ti.lineify.ui.adapter.DividerItemDecoration;
import ch.bfh.ti.lineify.ui.adapter.TouchListener;
import ch.bfh.ti.lineify.ui.adapter.TrackRecyclerViewAdapter;

public class HistoryFragment extends Fragment {
    private final IWayPointRepository wayPointRepository;
    private final TrackRecyclerViewAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        View fragmentView = inflater.inflate(R.layout.fragment_history, container, false);
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
        this.recyclerView = (RecyclerView) view.findViewById(R.id.trackRecyclerView);
        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.trackSwipeRefreshLayout);
    }

    private void initializeViews() {
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(this.recyclerAdapter);
        this.recyclerView.addOnItemTouchListener(new TouchListener(this.getContext(), this.recyclerView, (view, position, isLongClick) ->
            this.navigateToTrackDetail(this.recyclerAdapter.getTrack(position))
        ));

        this.swipeRefreshLayout.setOnRefreshListener(() -> {
            this.loadTracks();
        });
    }

    private void loadTracks() {
        String userEmail = UserManagement.getCurrentUsersEmail(this.getActivity());

        this.swipeRefreshLayout.setRefreshing(true);
        this.wayPointRepository.getTracks(userEmail).subscribe(
            result -> {
                this.recyclerAdapter.setTracks(result);
                this.swipeRefreshLayout.setRefreshing(false);
            },
            exception -> {
                this.handleNetworkError(exception);
                this.swipeRefreshLayout.setRefreshing(false);
            }
        );
    }

    private void navigateToTrackDetail(Track track) {
        Intent intent = new Intent(this.getContext(), TrackDetail.class);
        intent.putExtra(Constants.TRACK_DETAIL_ACTIVITY_TRACK_EXTRA_NAME, track);

        this.startActivity(intent);
    }

    private void handleNetworkError(Throwable e) {
        Log.e("HistoryFragment", "Error while loading tracks.", e);
        Snackbar.make(this.getView(), R.string.load_lines_network_error, Snackbar.LENGTH_LONG).show();
    }
}