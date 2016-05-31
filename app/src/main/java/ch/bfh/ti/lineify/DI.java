package ch.bfh.ti.lineify;

import android.content.Context;

import java.util.HashMap;

import ch.bfh.ti.lineify.core.dependencyInjection.DependencyContainer;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;

public class DI {
    private final static Object dependencyContainersLockPad = new Object();
    private static HashMap<Context, IDependencyContainer> dependencyContainers = new HashMap<>();

    public static IDependencyContainer container(Context applicationContext) {
        if (!isAlreadySetup(applicationContext)) {
            setup(applicationContext);
        }

        return dependencyContainers.get(applicationContext);
    }

    private static boolean isAlreadySetup(Context applicationContext) {
        return dependencyContainers.containsKey(applicationContext);
    }

    private static void setup(Context applicationContext) {
        synchronized (dependencyContainersLockPad) {
            if (!isAlreadySetup(applicationContext)) {
                dependencyContainers.put(applicationContext, createDependencyContainer(applicationContext));
            }
        }
    }

    private static IDependencyContainer createDependencyContainer(Context applicationContext) {
        IDependencyContainer container = new DependencyContainer();
        ch.bfh.ti.lineify.core.Registry.initializeDependencies(container);
        ch.bfh.ti.lineify.infrastructure.Registry.initializeDependencies(container);
        ch.bfh.ti.lineify.ui.Registry.initializeDependencies(container);

        container.registerContainerControlled(Context.class, () -> applicationContext);

        return container;
    }
}