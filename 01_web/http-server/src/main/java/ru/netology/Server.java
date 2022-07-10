package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Server {

    private final int port;
    private ServerSocket serverSocket;
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();
    private final int limit;
    private final List<String> allowedMethods;

    public Server(int port, int limit, List<String> allowedMethods) {
        this.port = port;
        this.limit = limit;
        this.allowedMethods = allowedMethods;
    }

    public void start() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process() {
        while (true) {
            try (
                    final var socket = serverSocket.accept();
                    final var in = new BufferedInputStream(socket.getInputStream());
                    final var out = new BufferedOutputStream(socket.getOutputStream())
            ) {
                Request request = new RequestParser().parse(in, limit, allowedMethods);
                if (request == null) {
                    processBadRequest(out);
                } else {
                    Handler handler = handlers.get(request.getPath()).get(request.getMethod());
                    if (handler == null) {
                        processBadRequest(out);
                        return;
                    }
                    handler.handle(request, out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void addHandler(String method, String path, Handler handler) {
        handlers.put(path, new ConcurrentHashMap<>(Map.of(method, handler)));
    }

    public Map<String, Map<String, Handler>> getHandlers() {
        return handlers;
    }

    public void processBadRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    public void processRightRequest(BufferedOutputStream out, Path filePath, String mimeType)
            throws IOException {
        final var length = Files.size(filePath);
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    public void processClassicCase(BufferedOutputStream out, Path filePath, String mimeType)
            throws IOException {
        final var template = Files.readString(filePath);
        final var content = template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.write(content);
        out.flush();
    }
}
