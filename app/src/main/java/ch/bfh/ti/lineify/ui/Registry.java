package ch.bfh.ti.lineify.ui;

import ch.bfh.ti.lineify.core.IWayPointRepository;
import ch.bfh.ti.lineify.core.IWayPointStore;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;
import ch.bfh.ti.lineify.ui.adapter.TrackRecyclerViewAdapter;
import ch.bfh.ti.lineify.ui.fragments.HistoryFragment;
import ch.bfh.ti.lineify.ui.fragments.TrackingFragment;

public class Registry {
    public static void initializeDependencies(IDependencyContainer container) {
        container.registerContainerControlled(TrackingFragment.class, () -> new TrackingFragment());
        container.registerContainerControlled(HistoryFragment.class, () -> new HistoryFragment(container.resolve(IWayPointRepository.class), container.resolve(IWayPointStore.class), container.resolve(TrackRecyclerViewAdapter.class)));
        container.registerPerResolve(TrackRecyclerViewAdapter.class, () -> new TrackRecyclerViewAdapter());
    }
}