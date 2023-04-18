package tests;

import annotation.Description;
import annotation.Issue;
import core.RestCore;
import domain.JsonPlaceholderEndpoint;
import domain.model.request.PostsRequest;
import domain.model.response.PostCommentsResponse;
import domain.model.response.PostsResponse;
import enums.TestType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.JsonUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * A TEST TASK
 * Create a test suite for testing the following methods from this REST service using Java: https://jsonplaceholder.typicode.com
 * GET /posts
 * GET /posts/ {id}
 * GET /posts/ {id} /comments
 * GET /comments?postId= {id}
 * POST /posts
 * PUT /posts/ {id}
 * PATCH /posts/ {id}
 * DELETE /posts/ {id}
 *
 * ACCEPTANCE CRITERIA
 * Tests should be built with layered architecture (core, domain, tests levels)
 * Tests should be created using either Rest Assured or Spring Rest Template or Apache Http Client.
 * Tests have to include critical path tests validations both positive and negative (define a set of tests on your own).
 * Implemented tests should be readable with needed comments.
 * Tests must be implemented so that they can be launched in parallel.
 * Naming and Code Conventions should be followed https://google.github.io/styleguide/javaguide.html or any other.
 **/
public class JsonPlaceholderPostsRestTest {

    private static final String NON_EXISTING_ID = "xyz";
    private static final String VALID_ID = "8";
    private static final String POSTS_200_RESPONSE_JSON = "src/test/resources/responses/getPosts200Response.json";
    private final RestCore restCore = new RestCore(JsonPlaceholderEndpoint.host);

    @Test
    @Description(type = TestType.POSITIVE, description = "Check 200 response for GET '/posts/{id}' with existing Id")
    public void validateGetPosts200Response() {
        String existingId = "3";
        PostsResponse expectedResponse = JsonUtils.readObjectFromJson(POSTS_200_RESPONSE_JSON, PostsResponse.class);

        Response response = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postId, existingId));
        response.then().statusCode(HttpStatus.SC_OK);

        PostsResponse actualResponse = response.getBody().as(PostsResponse.class);
        Assert.assertEquals(actualResponse, expectedResponse);
    }

    @Test
    @Description(type = TestType.NEGATIVE, description = "Check 404 response for GET '/posts/{id}' with non-existing Id")
    public void validateGetPosts404Response() {
        Response response = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postId, NON_EXISTING_ID));
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
        response.then().body("isEmpty()", Matchers.is(true));
    }

    @Test
    @Description(type = TestType.POSITIVE, description = "Check 200 response for GET '/posts'")
    public void validateGetAllPosts200Response() {
        Response response = restCore.getResponse(JsonPlaceholderEndpoint.post);
        response.then().statusCode(HttpStatus.SC_OK);

        PostsResponse[] postsResponses = response.getBody().as(PostsResponse[].class);
        Assert.assertTrue(postsResponses.length > 0);
        Arrays.stream(postsResponses).forEach(x -> Assert.assertNotNull(x.getId()));
    }

    @Test
    @Description(type = TestType.POSITIVE, description = "Check 200 response for GET '/posts/{id}/comments' with existing id")
    public void validateGetPostsComments200Response() {
        Response response = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postIdComments, VALID_ID));
        response.then().statusCode(HttpStatus.SC_OK);

        PostCommentsResponse[] postResponses = response.getBody().as(PostCommentsResponse[].class);
        Assert.assertTrue(postResponses.length > 0);
        Arrays.stream(postResponses).forEach(x -> Assert.assertEquals(x.getPostId(), VALID_ID));
    }

    @Test
    @Description(type = TestType.POSITIVE, description = "Check 200 response for GET '/comments' with existing id")
    public void validateGetComments200Response() {
        Map<String, String> params = Map.of("postId", "3");
        Response response = restCore.getResponse(params, JsonPlaceholderEndpoint.comments);
        response.then().statusCode(HttpStatus.SC_OK);

        PostCommentsResponse[] postResponses = response.getBody().as(PostCommentsResponse[].class);
        Assert.assertTrue(postResponses.length > 0);
    }

    @Test
    @Description(type = TestType.POSITIVE, description = "Check 200 response for DELETE '/posts/{id}' with existing id")
    public void validateDeletePosts200Response() {
        Response response = restCore.deleteResponse(getParamPath(JsonPlaceholderEndpoint.postId, VALID_ID));
        response.then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Issue(ticketNumber = "", description = "potential API bug - DELETE on non existing id should return 404 error response")
    @Description(type = TestType.NEGATIVE, description = "Check 404 response for DELETE '/posts/{id}' with non-existing id")
    public void validateDeletePosts404Response() {
        Response response = restCore.deleteResponse(getParamPath(JsonPlaceholderEndpoint.postId, NON_EXISTING_ID));
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Issue(ticketNumber = "", description = "potential API bug - PUT response returns 200, but entry is not updated")
    @Description(type = TestType.POSITIVE, description = "Check 200 response for PUT '/posts/{id}' with existing id")
    public void validatePutPosts200Response() {
        PostsRequest postsRequest = new PostsRequest();
        postsRequest.setBody("updated Body");
        postsRequest.setTitle("updated Title");
        postsRequest.setUserId("123");

        String existingId = "5";
        Response putResponse = restCore.putResponse(postsRequest, getParamPath(JsonPlaceholderEndpoint.postId, existingId));
        putResponse.then().statusCode(HttpStatus.SC_OK);

        PostsResponse actualPutResponse = putResponse.getBody().as(PostsResponse.class);

        Assert.assertEquals(actualPutResponse.getId(), existingId);
        Assert.assertEquals(actualPutResponse.getUserId(), postsRequest.getUserId());
        Assert.assertEquals(actualPutResponse.getBody(), postsRequest.getBody());
        Assert.assertEquals(actualPutResponse.getTitle(), postsRequest.getTitle());

        Response getResponse = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postId, existingId));
        getResponse.then().statusCode(HttpStatus.SC_OK);

        PostsResponse getObject = getResponse.getBody().as(PostsResponse.class);
        Assert.assertEquals(getObject, actualPutResponse);
    }

    @Test
    @Issue(ticketNumber = "", description = "potential API bug - invalid 500 response with text/html content instead of 'Not Found' response")
    @Description(type = TestType.NEGATIVE, description = "Check 404 response for PUT '/posts/{id}' with non-existing id")
    public void validatePutPosts404Response() {
        Response response = restCore.putResponse("invalidRequestBody", getParamPath(JsonPlaceholderEndpoint.postId, NON_EXISTING_ID));
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Issue(ticketNumber = "", description = "potential bug - created entry with generated id cannot be found by GET '/posts/{id}'")
    @Description(type = TestType.POSITIVE, description = "Check 201 response for POST '/posts' with existing id")
    public void validatePostPosts200Response() {
        PostsRequest postsRequest = new PostsRequest();
        postsRequest.setBody("Test body");
        postsRequest.setTitle("Test Title");
        postsRequest.setUserId("105");

        Response postResponse = restCore.postResponse(postsRequest, JsonPlaceholderEndpoint.post);
        postResponse.then().statusCode(HttpStatus.SC_CREATED);

        PostsResponse actualResponse = postResponse.getBody().as(PostsResponse.class);

        String generatedId = actualResponse.getId();
        Assert.assertNotNull(generatedId);
        Assert.assertEquals(actualResponse.getUserId(), postsRequest.getUserId());
        Assert.assertEquals(actualResponse.getBody(), postsRequest.getBody());
        Assert.assertEquals(actualResponse.getTitle(), postsRequest.getTitle());

        Response getResponse = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postId, generatedId));
        getResponse.then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    @Issue(ticketNumber = "", description = "potential API bug - invalid 200 response for non existing id")
    @Description(type = TestType.NEGATIVE, description = "Check 404 response for PATCH '/posts/{id}' with non-existing id")
    public void validatePatchPosts404Response() {
        PostsRequest postsRequest = new PostsRequest();
        postsRequest.setTitle("patched Title");

        Response response = restCore.patchResponse(postsRequest, getParamPath(JsonPlaceholderEndpoint.postId, NON_EXISTING_ID));
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @Issue(ticketNumber = "", description = "Patch does not update particular field e.g. body")
    @Description(type = TestType.POSITIVE, description = "Check 200 response for PATCH '/posts/{id}' with existing id")
    public void validatePatchPosts200Response() {
        PostsRequest postsRequest = new PostsRequest();
        postsRequest.setBody("patched Body");

        String existingId = "12";
        Response patchResponse = restCore.patchResponse(postsRequest, getParamPath(JsonPlaceholderEndpoint.postId, existingId));
        patchResponse.then().statusCode(HttpStatus.SC_OK);

        PostsResponse actualPutResponse = patchResponse.getBody().as(PostsResponse.class);

        Assert.assertEquals(actualPutResponse.getBody(), postsRequest.getBody());
        Assert.assertEquals(actualPutResponse.getTitle(), "in quibusdam tempore odit est dolorem");
        Assert.assertEquals(actualPutResponse.getUserId(), "2");
        Assert.assertEquals(actualPutResponse.getId(), existingId);

        Response getResponse = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postId, existingId));
        getResponse.then().statusCode(HttpStatus.SC_OK);

        PostsResponse getObject = getResponse.getBody().as(PostsResponse.class);
        Assert.assertEquals(getObject.getBody(), postsRequest.getBody());
    }

    private String getParamPath(String path, String id) {
        return path.replace("{id}", id);
    }
}