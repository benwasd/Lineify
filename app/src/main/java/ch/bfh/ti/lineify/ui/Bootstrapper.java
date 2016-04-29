package ch.bfh.ti.lineify.ui;

import android.content.Context;

import ch.bfh.ti.lineify.core.dependencyInjection.DependencyContainer;
import ch.bfh.ti.lineify.core.dependencyInjection.IDependencyContainer;

public class Bootstrapper {
    private static IDependencyContainer dependencyContainer;

    public void setup(Context applicationContext) {
        this.setupDependencyContainer();
        this.setupApplicationContextInContainer(applicationContext);
    }

    private void setupDependencyContainer() {
        IDependencyContainer container = new DependencyContainer();
        ch.bfh.ti.lineify.core.Registry.initializeDependencies(container);
        ch.bfh.ti.lineify.infrastructure.location.Registry.initializeDependencies(container);
        ch.bfh.ti.lineify.ui.Registry.initializeDependencies(container);

        this.dependencyContainer = container;
    }

    private void setupApplicationContextInContainer(Context applicationContext) {
        this.dependencyContainer.registerContainerControlled(Context.class, () -> applicationContext);
    }
}