package au.com.sensis.stubby.standalone;

import java.io.IOException;

import com.sun.net.httpserver.HttpServer;

public class HttpServerInstance extends ServerInstance {

    private HttpServer server;

    public HttpServerInstance(int port, ServerHandler handler) throws IOException {
        this.server = HttpServer.create(allInterfaces(port), SOCKET_BACKLOG);
        this.server.createContext("/", handler);
        this.server.start();
    }
    
    public HttpServer getServer() {
        return server;
    }
    
}
