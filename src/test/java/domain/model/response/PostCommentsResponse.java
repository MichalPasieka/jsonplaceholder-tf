package domain.model.response;

import lombok.Data;

@Data
public class PostCommentsResponse {

    String postId;
    String id;
    String name;
    String email;
    String body;
}