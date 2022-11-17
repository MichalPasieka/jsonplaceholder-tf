package core;

import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class RestCore {

    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";
    private final String host;

    public RestCore(String host) {
        this.host = host;
    }

    public Response getResponse(String path) {
        return given().baseUri(host).when().get(path);
    }

    public Response getResponse(Map<String, String> params, String path) {
        return given().baseUri(host).when().params(params).get(path);
    }

    public Response deleteResponse(String path) {
        return given().baseUri(host).when().delete(path);
    }

    public Response postResponse(Object requestBody, String path) {
        return given().baseUri(host).when().header(CONTENT_TYPE, APPLICATION_JSON).body(requestBody).post(path);
    }

    public Response putResponse(Object requestBody, String path) {
        return given().baseUri(host).when().header(CONTENT_TYPE, APPLICATION_JSON).body(requestBody).put(path);
    }

    public Response patchResponse(Object requestBody, String path) {
        return given().baseUri(host).when().header(CONTENT_TYPE, APPLICATION_JSON).body(requestBody).patch(path);
    }
}