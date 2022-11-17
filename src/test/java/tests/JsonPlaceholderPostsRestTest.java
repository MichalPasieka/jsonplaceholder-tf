package tests;

import core.RestCore;
import domain.JsonPlaceholderEndpoint;
import domain.model.request.PostsRequest;
import domain.model.response.PostCommentsResponse;
import domain.model.response.PostsResponse;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonPlaceholderPostsRestTest {

    private static final String NON_EXISTING_ID = "xyz";
    private static final String VALID_ID = "8";
    private final RestCore restCore = new RestCore(JsonPlaceholderEndpoint.host);

    @Test
    public void validateGetPosts200Response() {
        PostsResponse expectedResponse = new PostsResponse();
        expectedResponse.setId("3");
        expectedResponse.setUserId("1");
        expectedResponse.setTitle("ea molestias quasi exercitationem repellat qui ipsa sit aut");
        expectedResponse.setBody("et iusto sed quo iure\n" +
                "voluptatem occaecati omnis eligendi aut ad\n" +
                "voluptatem doloribus vel accusantium quis pariatur\n" +
                "molestiae porro eius odio et labore et velit aut");

        Response response = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postId, expectedResponse.getId()));
        response.then().statusCode(200);

        PostsResponse actualResponse = response.getBody().as(PostsResponse.class);

        Assert.assertEquals(actualResponse, expectedResponse);
    }

    @Test
    public void validateGetPosts494Response() {
        Response response = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postId, NON_EXISTING_ID));
        response.then().statusCode(404);
        response.then().body("isEmpty()", Matchers.is(true));
    }

    @Test
    public void validateGetAllPosts200Response() {
        Response response = restCore.getResponse(JsonPlaceholderEndpoint.post);
        response.then().statusCode(200);

        PostsResponse[] postsResponses = response.getBody().as(PostsResponse[].class);
        Assert.assertTrue(postsResponses.length > 0);

        Arrays.stream(postsResponses).forEach(x -> Assert.assertNotNull(x.getId()));
    }

    @Test
    public void validateGetPostsComments200Response() {
        Response response = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postIdComments, VALID_ID));
        response.then().statusCode(200);

        PostCommentsResponse[] postResponses = response.getBody().as(PostCommentsResponse[].class);
        Assert.assertTrue(postResponses.length > 0);

        Arrays.stream(postResponses).forEach(x -> Assert.assertEquals(x.getPostId(), VALID_ID));
    }

    @Test
    public void validateGetComments200Response() {
        Map<String, String> params = new HashMap<>();
        params.put("postId", "3");
        Response response = restCore.getResponse(params, JsonPlaceholderEndpoint.comments);
        response.then().statusCode(200);

        PostCommentsResponse[] postResponses = response.getBody().as(PostCommentsResponse[].class);
        Assert.assertTrue(postResponses.length > 0);
    }

    @Test
    public void validateDeletePosts200Response() {
        Response response = restCore.deleteResponse(getParamPath(JsonPlaceholderEndpoint.postId, VALID_ID));
        response.then().statusCode(200);
    }

    //TODO potential API bug - DELETE on non existing id should return 404 error response
    @Test
    public void validateDeletePosts404Response() {
        Response response = restCore.deleteResponse(getParamPath(JsonPlaceholderEndpoint.postId, NON_EXISTING_ID));
        response.then().statusCode(404);
    }

    //TODO potential API bug - PUT response returns 200, but entry is not updated
    @Test
    public void validatePutPosts200Response() {
        PostsRequest postsRequest = new PostsRequest();
        postsRequest.setBody("updated Body");
        postsRequest.setTitle("updated Title");
        postsRequest.setUserId("123");

        String existingId = "5";
        Response putResponse = restCore.putResponse(postsRequest, getParamPath(JsonPlaceholderEndpoint.postId, existingId));
        putResponse.then().statusCode(200);

        PostsResponse actualPutResponse = putResponse.getBody().as(PostsResponse.class);

        Assert.assertEquals(actualPutResponse.getId(), existingId);
        Assert.assertEquals(actualPutResponse.getUserId(), postsRequest.getUserId());
        Assert.assertEquals(actualPutResponse.getBody(), postsRequest.getBody());
        Assert.assertEquals(actualPutResponse.getTitle(), postsRequest.getTitle());

        // validate updated entry
        Response getResponse = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postId, existingId));
        getResponse.then().statusCode(200);

        PostsResponse getObject = getResponse.getBody().as(PostsResponse.class);

        Assert.assertEquals(getObject, actualPutResponse);
    }
    //TODO potential API bug - invalid 500 response with text/html content instead of 'Not Found' response
    @Test
    public void validatePutPosts404Response() {
        Response response = restCore.putResponse("invalidRequestBody", getParamPath(JsonPlaceholderEndpoint.postId, NON_EXISTING_ID));
        response.then().statusCode(404);
    }

    //TODO potential bug - created entry with generated id cannot be found by GET "/posts/{id}"
    @Test
    public void validatePostPosts200Response() {
        PostsRequest postsRequest = new PostsRequest();
        postsRequest.setBody("Test body");
        postsRequest.setTitle("Test Title");
        postsRequest.setUserId("105");

        Response postResponse = restCore.postResponse(postsRequest, JsonPlaceholderEndpoint.post);
        postResponse.then().statusCode(201);

        PostsResponse actualResponse = postResponse.getBody().as(PostsResponse.class);

        String generatedId = actualResponse.getId();
        Assert.assertNotNull(generatedId);
        Assert.assertEquals(actualResponse.getUserId(), postsRequest.getUserId());
        Assert.assertEquals(actualResponse.getBody(), postsRequest.getBody());
        Assert.assertEquals(actualResponse.getTitle(), postsRequest.getTitle());

        // validate created entry
        Response getResponse = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postId, generatedId));
        getResponse.then().statusCode(200);
    }

    //TODO potential API bug - invalid 200 response for non existing id
    @Test
    public void validatePatchPosts404Response() {
        PostsRequest postsRequest = new PostsRequest();
        postsRequest.setTitle("patched Title");

        Response response = restCore.patchResponse(postsRequest, getParamPath(JsonPlaceholderEndpoint.postId, NON_EXISTING_ID));
        response.then().statusCode(404);
    }

    @Test
    public void validatePatchPosts200Response() {
        PostsRequest postsRequest = new PostsRequest();
        postsRequest.setBody("patched Body");

        String existingId = "12";
        Response patchResponse = restCore.patchResponse(postsRequest, getParamPath(JsonPlaceholderEndpoint.postId, existingId));
        patchResponse.then().statusCode(200);

        PostsResponse actualPutResponse = patchResponse.getBody().as(PostsResponse.class);

        Assert.assertEquals(actualPutResponse.getBody(), postsRequest.getBody());
        Assert.assertEquals(actualPutResponse.getTitle(), "in quibusdam tempore odit est dolorem");
        Assert.assertEquals(actualPutResponse.getUserId(), "2");
        Assert.assertEquals(actualPutResponse.getId(), existingId);

        // validate patched entry
        Response getResponse = restCore.getResponse(getParamPath(JsonPlaceholderEndpoint.postId, existingId));
        getResponse.then().statusCode(200);

        PostsResponse getObject = getResponse.getBody().as(PostsResponse.class);

        Assert.assertEquals(getObject.getBody(), postsRequest.getBody());
    }

    private String getParamPath(String path, String id) {
        return path.replace("{id}", id);
    }
}