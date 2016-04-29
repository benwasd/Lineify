package ch.bfh.ti.lineify.infrastructure.location;

import android.content.Context;

import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;

public class Registry {
    public static void initializeDependencies(IDependencyContainer container) {
        // services
        container.registerContainerControlled(IWayPointService.class, () -> new WayPointService(container.resolve(Context.class)));
    }
}