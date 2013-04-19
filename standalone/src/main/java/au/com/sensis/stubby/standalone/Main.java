package au.com.sensis.stubby.standalone;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        final List<ServerInstance> servers = new ArrayList<ServerInstance>();
        final ServerHandler handler = new ServerHandler(); // share between protocols
        
        if (args.length >= 1) { // HTTP server
            HttpServerInstance httpServer = new HttpServerInstance(Integer.parseInt(args[0]), handler);
            LOGGER.info("Started HTTP server on " + httpServer.getAddress());
            servers.add(httpServer);
        }
        if (args.length >= 2) { // HTTPS server
            HttpsServerInstance httpsServer = new HttpsServerInstance(Integer.parseInt(args[1]), handler);
            LOGGER.info("Started HTTPS server on " + httpsServer.getAddress());
            servers.add(httpsServer);
        }
        if (args.length > 3) {
            throw new RuntimeException("Usage: java ... <http_port> [<https_port>]");
        }
        
        Thread shutdownHook = new Thread() {
            public void run() {
                LOGGER.info("Stopping servers...");
                for (ServerInstance server : servers) {
                    server.shutdown();
                }
            }
        };
        
        handler.setShutdownHook(shutdownHook); // handle shutdown requests over HTTP
        Runtime.getRuntime().addShutdownHook(shutdownHook); // handle SIGINT etc.
    }

}
