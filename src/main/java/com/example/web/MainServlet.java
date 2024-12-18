package com.example.web;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.example.Main;
import com.example.computation.app.Configuration;
import com.example.computation.compute.FDS;
import com.example.computation.utils.Graph6Converter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.FileUtils;

public class MainServlet {

    private HttpServer server;

    public MainServlet() throws IOException {
        int serverPort = Configuration.webPort;
        server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/api", new ApiGetHttpHandler());
        server.createContext("/", new StaticHandler("/", Configuration.resourcePath));
        server.setExecutor(null); // creates a default executor
    }

    public int start() {
        server.start();
        return 10;
    }

    static class GetHttpHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            File file = new File("src/main/webapp/WEB-INF/index.html");
            if(!file.exists()) {
                System.out.println("ERROR");
            }
            String response = FileUtils.readFileToString(file);
            String encoding = "UTF-8";
            t.getResponseHeaders().set("Content-Type", "text/html; charset=" + encoding);
            t.getResponseHeaders().set("Accept-Ranges", "bytes");
            t.sendResponseHeaders(200, response.getBytes("UTF-8").length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes("UTF-8"));
            os.close();
        }
    }

    static class ApiGetHttpHandler implements HttpHandler {

        public void handle(HttpExchange httpExchange) throws IOException {
            StringBuilder response = new StringBuilder();
            Map<String,String> parms = MainServlet.queryToMap(httpExchange.getRequestURI().getQuery());
            FDS fds = new FDS(Graph6Converter.fromGraph6ToAdjacentMatrix(parms.get("g6")));
            StringBuilder ans = new StringBuilder();
            ans.append(fds.getGeoDominantSet()).append(";").append(fds.getDependentGeoDominantSet())
                    .append(";").append(fds.getIndependentGeoDominantSet())
                    .append(";").append(fds.getDominantSet())
                    .append(";").append(fds.getDependentDominantSet())
                    .append(";").append(fds.getIndependentDominantSet());
            final byte[] rawResponseBody = ans.toString().getBytes();
            httpExchange.getResponseHeaders().put("Access-Control-Allow-Origin", Arrays.asList("*"));
            httpExchange.sendResponseHeaders(220, rawResponseBody.length);
            httpExchange.getResponseBody().write(rawResponseBody);
            httpExchange.getResponseBody().flush();
            httpExchange.close();
        }
    }

    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}