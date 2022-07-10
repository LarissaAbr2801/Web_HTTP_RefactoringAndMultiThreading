package ru.netology;

import org.apache.http.NameValuePair;

import java.util.List;
import java.util.stream.Collectors;

public class Request {

    private final String method;
    private final String path;
    private List<NameValuePair> queryString;
    private final List<String> headers;
    private final String body;
    private List<NameValuePair> postParams;

    public Request(String method, String path,
                   List<NameValuePair> queryString, List<String> headers,
                   String body, List<NameValuePair> postParams) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.headers = headers;
        this.body = body;
        this.postParams = postParams;
    }

    public Request(String method, String path,
                  List<String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public List<NameValuePair> getQueryParams() {
        return queryString;
    }

    public List<NameValuePair> getQueryParam(String name) {
        return queryString.stream()
                .filter(x -> x.getName().equals(name))
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getPostParams() {
      return postParams;
    }

    public List<NameValuePair> getPostParam(String name) {
        return postParams.stream()
                .filter(x -> x.getName().equals(name))
                .collect(Collectors.toList());
    }

}
