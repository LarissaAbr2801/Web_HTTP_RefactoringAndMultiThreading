package ru.netology;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

  static final int PORT = 9999;
  static final int THREADS_QUANTITY = 64;

  public static void main(String[] args) {
    ExecutorService executorService = Executors.newFixedThreadPool(THREADS_QUANTITY);

    final var validPaths = new CopyOnWriteArrayList<>(Arrays.asList("/index.html", "/spring.svg", "/spring.png", "/resources.html",
            "/styles.css", "/app.js", "/links.html", "/forms.html",
            "/classic.html", "/events.html", "/events.js"));

    String classicCasePath = "/classic.html";
    String pathName = "01_web/http-server/public";

    Server server = new Server(validPaths, PORT, pathName, classicCasePath);
    server.start();

    for (int i =0; i < THREADS_QUANTITY; i++) {
      executorService.submit(new Thread(server::process));
    }
    executorService.shutdown();

  }
}


