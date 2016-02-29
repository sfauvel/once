package org.sf.once.ihm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebServer {

    public static void main(String[] args) throws IOException {
        InetSocketAddress addr = new InetSocketAddress(8080);
        HttpServer server = HttpServer.create(addr, 0);

        server.createContext("/", new MyHandler());
        server.createContext("/once", new OnceHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server is listening on port 8080");
    }
}

class MyHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equalsIgnoreCase("GET")) {
            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "html");
            exchange.sendResponseHeaders(200, 0);

            OutputStream responseBody = exchange.getResponseBody();
            StringBuffer buffer = new StringBuffer();

            buffer.append("<html><body>");
            buffer.append("<h1>Ma page web de base</h1>");

            for (Entry<String, List<String>> headerValue : getAllRequestHeaderValues(exchange.getRequestHeaders()).entrySet()) {
                buffer.append(headerValue.getKey() + " = " + headerValue.getValue().toString() + "<br/>");
            }

            buffer.append("</body></html>");

            responseBody.write(buffer.toString().getBytes());

            responseBody.close();
        }
    }

    private Map<String, List<String>> getAllRequestHeaderValues(Headers requestHeaders) {
        Set<String> keySet = requestHeaders.keySet();
        Iterator<String> iter = keySet.iterator();

        Map<String, List<String>> result = new HashMap<String, List<String>>();
        while (iter.hasNext()) {
            String key = iter.next();
            List<String> values = requestHeaders.get(key);
            result.put(key, values);
        }
        return result;
    }
}

