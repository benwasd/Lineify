package ch.bfh.ti.lineify;

import android.content.Context;

import ch.bfh.ti.lineify.core.dependencyInjection.DependencyContainer;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;
import ch.bfh.ti.lineify.infrastructure.Registry;

public class DI {
    private static Object setupLockPad = new Object();
    private static IDependencyContainer dependencyContainer;

    public static IDependencyContainer container() {
        if (!isAlreadySetUp()) {
            throw new Error("Dependency container not set up.");
        }

        return dependencyContainer;
    }

    public static void setup(Context applicationContext) {
        synchronized (setupLockPad) {
            if (!isAlreadySetUp()) {
                setupDependencyContainer();
                setupApplicationContextInContainer(applicationContext);
            }
        }
    }

    public static boolean isAlreadySetUp() {
        return dependencyContainer != null;
    }

    private static void setupDependencyContainer() {
        IDependencyContainer container = new DependencyContainer();
        ch.bfh.ti.lineify.core.Registry.initializeDependencies(container);
        ch.bfh.ti.lineify.infrastructure.Registry.initializeDependencies(container);
        ch.bfh.ti.lineify.ui.Registry.initializeDependencies(container);

        dependencyContainer = container;
    }

    private static void setupApplicationContextInContainer(Context applicationContext) {
        dependencyContainer.registerContainerControlled(Context.class, () -> applicationContext);
    }
}