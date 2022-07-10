package ru.netology;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static final int PORT = 9999;
    static final int THREADS_QUANTITY = 64;
    static final String GET = "GET";
    static final String POST = "POST";
    static final int LIMIT = 4096;
    static final String PATH_NAME = "01_web/http-server/public";

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS_QUANTITY);

        var allowedMethods = new ArrayList<>(List.of(GET, POST));

        Server server = new Server(PORT, LIMIT, allowedMethods);
        server.start();

        server.addHandler("GET", "/index.html",
                (request, responseStream) -> {
                    try {
                        final var filePath = Path.of(PATH_NAME, "/index.html");
                        final var mimeType = Files.probeContentType(filePath);
                        server.processRightRequest(responseStream, filePath, mimeType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        server.addHandler("GET", "/classic.html",
                (request, responseStream) -> {
            try {
                final var filePath = Path.of(PATH_NAME, request.getPath());
                final var mimeType = Files.probeContentType(filePath);
                server.processClassicCase(responseStream, filePath, mimeType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("GET", "/forms.html",
                (request, responseStream) -> {
                    try {
                        final var filePath = Path.of(PATH_NAME, request.getPath());
                        final var mimeType = Files.probeContentType(filePath);
                        server.processClassicCase(responseStream, filePath, mimeType);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        System.out.println(server.getHandlers());

        for (int i = 0; i < THREADS_QUANTITY; i++) {
            executorService.submit(new Thread(server::process));
        }
        executorService.shutdown();

    }
}


