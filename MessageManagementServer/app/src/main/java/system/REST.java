package system;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
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
        System.out.println("Starting REST server");

        Injector injector = Guice.createInjector(new AppModule());

        state = injector.getInstance(State.class);
        handler = injector.getInstance(Handler.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down REST server");
            handler.stop();
        }));
    }

    public static State getState() {
        return state;
    }
}
