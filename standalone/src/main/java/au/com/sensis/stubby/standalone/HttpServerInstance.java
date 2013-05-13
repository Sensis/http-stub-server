package au.com.sensis.stubby.standalone;

import java.io.IOException;
import java.util.concurrent.Executor;

import com.sun.net.httpserver.HttpServer;

public class HttpServerInstance extends ServerInstance {

    private HttpServer server;

    public HttpServerInstance(int port, ServerHandler handler, Executor executor) throws IOException {
        this.server = HttpServer.create(allInterfaces(port), SOCKET_BACKLOG);
        this.server.createContext("/", handler);
        this.server.setExecutor(executor);
        this.server.start();
    }
    
    public HttpServer getServer() {
        return server;
    }
    
}
