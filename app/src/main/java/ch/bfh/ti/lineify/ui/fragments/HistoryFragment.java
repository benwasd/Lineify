package ch.bfh.ti.lineify.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.bfh.ti.lineify.DI;
import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.Constants;
import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.ui.TrackDetailActivity;
import ch.bfh.ti.lineify.ui.adapter.IItemTouchListener;
import ch.bfh.ti.lineify.ui.adapter.TouchListener;
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
        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.trackSwipeRefreshLayout);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.trackRecyclerView);
    }

    private void initializeViews() {
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        //this.recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(this.recyclerAdapter);

        this.recyclerView.addOnItemTouchListener(new TouchListener(this.getContext(), this.recyclerView, new IItemTouchListener() {
            @Override
            public void onClick(View view, int position) {
                Log.i("addOnItemTouchListener", "Short Pos: " + position);
                Intent intent = new Intent(HistoryFragment.this.getContext(), TrackDetailActivity.class);
                intent.putExtra(Constants.TRACK_DETAIL_ACTIVITY_TRACK_EXTRA_NAME, recyclerAdapter.getTrack(position));
                HistoryFragment.this.startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.i("addOnItemTouchListener", "LONG Pos: " + position);
            }
        }));

        this.swipeRefreshLayout.setOnRefreshListener(() -> {
            this.loadTracks();
        });
    }

    private void loadTracks() {
        String userEmail = Settings.Secure.getString(this.getActivity().getContentResolver(), Settings.Secure.ANDROID_ID); // TODO: Replace if we have user managment

        this.swipeRefreshLayout.setRefreshing(true);
        this.wayPointRepository.getTracks(userEmail).subscribe(
            t -> {
                this.recyclerAdapter.setTracks(t);
                if(t.size() > 0) {

                }
            },
            e -> {
                Snackbar.make(this.getView(), "Fehler beim Laden der Lines. Prüfe deine Netzwerkeinstellungen.", Snackbar.LENGTH_LONG).show();

                List<Track> dummyTracks = new ArrayList<>();
                dummyTracks.add(new Track("mail","identifier"));

                this.recyclerAdapter.setTracks(dummyTracks);
                this.swipeRefreshLayout.setRefreshing(false);
            },
            () -> {
                this.swipeRefreshLayout.setRefreshing(false);
            }
        );
    }
}