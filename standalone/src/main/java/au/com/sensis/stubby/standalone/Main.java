package au.com.sensis.stubby.standalone;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    private static final int NUM_WORKER_THREADS = 10;

    public static void main(String[] args) throws Exception {
        final List<ServerInstance> servers = new ArrayList<ServerInstance>();
        final ServerHandler handler = new ServerHandler(); // share between protocols
        final ExecutorService executor = Executors.newFixedThreadPool(NUM_WORKER_THREADS);

        if (args.length >= 1) { // HTTP server
            HttpServerInstance httpServer = new HttpServerInstance(Integer.parseInt(args[0]), handler, executor);
            LOGGER.info("Started HTTP server on " + httpServer.getAddress());
            servers.add(httpServer);
        }

        if (args.length >= 2) { // HTTPS server
            HttpsServerInstance httpsServer = new HttpsServerInstance(Integer.parseInt(args[1]), handler, executor);
            LOGGER.info("Started HTTPS server on " + httpsServer.getAddress());
            servers.add(httpsServer);
        }

        if (args.length > 3) {
            throw new RuntimeException("Usage: java ... <http_port> [<https_port>]");
        }
        
        Thread shutdownHook = new Thread() {
            public void run() {
                LOGGER.info("Stopping servers...");
                try {
                    for (ServerInstance server : servers) {
                        server.shutdown();
                    }
                    executor.shutdown();
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    LOGGER.error("Error performing graceful shutdown", e);
                }
            }
        };
        
        handler.setShutdownHook(shutdownHook); // handle shutdown requests over HTTP
        Runtime.getRuntime().addShutdownHook(shutdownHook); // handle SIGINT etc.
    }

}
