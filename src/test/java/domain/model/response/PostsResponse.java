package domain.model.response;

import lombok.Data;

@Data
public class PostsResponse {

    String userId;
    String id;
    String title;
    String body;
}