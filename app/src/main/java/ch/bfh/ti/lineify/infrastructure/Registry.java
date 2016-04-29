package ch.bfh.ti.lineify.infrastructure;

import android.content.Context;

import ch.bfh.ti.lineify.core.IPermissionRequestor;
import ch.bfh.ti.lineify.core.IWayPointService;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;
import ch.bfh.ti.lineify.infrastructure.location.WayPointService;

public class Registry {
    public static void initializeDependencies(IDependencyContainer container) {
        container.registerContainerControlled(IWayPointService.class, () -> new WayPointService(container.resolve(Context.class)));
        container.registerPerResolve(IPermissionRequestor.class, () -> new PermissionRequestor(container.resolve(Context.class)));
    }
}