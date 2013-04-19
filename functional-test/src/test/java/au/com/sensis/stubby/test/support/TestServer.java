package au.com.sensis.stubby.test.support;

import au.com.sensis.stubby.standalone.HttpServerInstance;
import au.com.sensis.stubby.standalone.ServerHandler;
import au.com.sensis.stubby.standalone.ServerInstance;

/*
 * Simple test server using standalone implementation
 */
public class TestServer {

    public static final int FREE_PORT = 0; // let operating system choose a free port

    private static ServerInstance server;

    public static void start() {
        try {
            server = new HttpServerInstance(FREE_PORT, new ServerHandler());
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
