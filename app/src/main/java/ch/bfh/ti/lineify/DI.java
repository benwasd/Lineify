package ch.bfh.ti.lineify;

import android.content.Context;

import ch.bfh.ti.lineify.core.dependencyInjection.DependencyContainer;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;

public class DI {
    private final static Object setupLockPad = new Object();
    private static IDependencyContainer dependencyContainer;

    public static IDependencyContainer container() {
        if (!isAlreadySetup()) {
            throw new Error("Dependency container not set up.");
        }

        return dependencyContainer;
    }

    public static void setup(Context applicationContext) {
        synchronized (setupLockPad) {
            if (!isAlreadySetup()) {
                dependencyContainer = createDependencyContainer(applicationContext);
            }
        }
    }

    private static boolean isAlreadySetup() {
        return dependencyContainer != null;
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