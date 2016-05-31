package ch.bfh.ti.lineify;

import android.content.Context;

import ch.bfh.ti.lineify.core.dependencyInjection.DependencyContainer;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;

public class DI {
    private static Object setupLockPad = new Object();
    private static IDependencyContainer dependencyContainer;

    public static IDependencyContainer container() {
        if (!isContainerAlreadySetUp()) {
            throw new Error("Dependency container not set up.");
        }

        return dependencyContainer;
    }

    public static void setup(Context applicationContext) {
        synchronized (setupLockPad) {
            if (!isContainerAlreadySetUp()) {
                setupContainer(applicationContext);
            }
        }
    }

    public static IDependencyContainer createDependencyContainer() {
        IDependencyContainer container = new DependencyContainer();
        ch.bfh.ti.lineify.core.Registry.initializeDependencies(container);
        ch.bfh.ti.lineify.infrastructure.Registry.initializeDependencies(container);
        ch.bfh.ti.lineify.ui.Registry.initializeDependencies(container);

        return container;
    }

    public static IDependencyContainer registerApplicationContext(IDependencyContainer container, Context applicationContext) {
        container.registerContainerControlled(Context.class, () -> applicationContext);

        return container;
    }

    private static boolean isContainerAlreadySetUp() {
        return dependencyContainer != null;
    }

    private static void setupContainer(Context applicationContext) {
        IDependencyContainer container;
        container = createDependencyContainer();
        container = registerApplicationContext(container, applicationContext);

        dependencyContainer = container;
    }
}