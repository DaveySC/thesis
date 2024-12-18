package com.example.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StaticHandler implements HttpHandler {

    private String routePath;
    private String fsPath;

    private Map<String, String> headers = new HashMap<String, String>(){{
        put("html", "text/html");
        put("css", "text/css");
        put("js", "text/javascript");
        put("json", "application/json");
        put("svg", "image/svg+xml");
    }};

    public StaticHandler(String path, String filesystemPath) {
        routePath = path;
        fsPath = filesystemPath;
    }

    @Override
    public void handle(HttpExchange http) throws IOException {
        OutputStream outputStream = http.getResponseBody();
        http.getRequestBody();
        String request = http.getRequestURI().getRawPath();
        System.out.println(request);
        byte[] result;
        int code;
        try {

            try {
                String path = request.equals("/") ? fsPath + "/index.html" : fsPath + request;
                System.out.println("requested: " + path);
                //result = read(new FileInputStream(path)).toByteArray();
                result = read(StaticHandler.class.getResourceAsStream(path)).toByteArray();
                String ext = request.substring(request.lastIndexOf(".") + 1);
                if (headers.containsKey(ext))
                    http.getResponseHeaders().add("Content-Type", headers.get(ext));
                code = 200;
            } catch (IOException e) {
                e.printStackTrace();
                result = (404 + " " + request).getBytes();
                code = 404;
            }

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            result = sw.getBuffer().toString().getBytes();
            code = 500;
        }

        http.sendResponseHeaders(code, result.length);
        outputStream.write(result);
        outputStream.close();
    }

    static ByteArrayOutputStream read(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer;
    }

}