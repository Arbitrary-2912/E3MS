package system;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.Handler;
import state.State;

/**
 * Main class for the REST server.
 * <p>
 * This class is responsible for starting the REST server and initializing the
 * necessary components.
 * </p>
 * <p>
 * The REST server is responsible for handling incoming HTTP requests and
 * publishing messages to the message queue.
 * </p>
 * <p>
 * The REST server is also responsible for handling messages received from the
 * message queue.
 * </p>
 * <p>
 * The REST server is a singleton class.
 */

@Singleton
public class REST {
    private static final Logger log = LoggerFactory.getLogger(REST.class);
    private static State state;
    private static Handler handler;

    /**
     * Main method for the REST server.
     * <p>
     * This method is responsible for starting the REST server and initializing the
     * necessary components.
     * </p>
     * <p>
     * The REST server is responsible for handling incoming HTTP requests and
     * publishing messages to the message queue.
     * </p>
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        log.info("Starting REST server");

        Injector injector = Guice.createInjector(new AppModule());

        state = injector.getInstance(State.class);
        handler = injector.getInstance(Handler.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down REST server");
            state = null;
            handler.stop();
        }));
    }

    /**
     * Gets the state of the REST server.
     * @return the state of the REST server
     */
    public static State getState() {
        return state;
    }
}
