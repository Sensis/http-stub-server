package au.com.sensis.stubby.test.support;

import au.com.sensis.stubby.standalone.HttpServerInstance;
import au.com.sensis.stubby.standalone.ServerHandler;
import au.com.sensis.stubby.standalone.ServerInstance;

import java.util.concurrent.Executors;

/*
 * Simple test server using standalone implementation
 */
public class TestServer {

    public static final int FREE_PORT = 0; // let operating system choose a free port
    public static final int NUM_SERVER_WORKERS = 2;

    private static ServerInstance server;

    public static void start() {
        try {
            server = new HttpServerInstance(FREE_PORT, new ServerHandler(), Executors.newFixedThreadPool(NUM_SERVER_WORKERS));
        } catch (Exception e) {
            throw new RuntimeException("Error starting server", e);
        }
    }

    public static int getPort() {
        return server.getAddress().getPort();
    }

    public static boolean isRunning() {
        return (server != null);
    }

}
