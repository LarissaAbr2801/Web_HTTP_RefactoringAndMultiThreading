package ru.netology;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

public interface Parser {

    Request parse(BufferedInputStream in, int limit, List<String> allowedMethods) throws IOException;
}
