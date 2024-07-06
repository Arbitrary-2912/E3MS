package system;

import com.google.inject.AbstractModule;
import state.Handler;
import state.State;

/**
 * Module that binds dependencies for the application.
 */
public class AppModule extends AbstractModule {

    /**
     * Configures the module.
     */
    @Override
    protected void configure() {
        bind(State.class);
        bind(Handler.class);
    }
}