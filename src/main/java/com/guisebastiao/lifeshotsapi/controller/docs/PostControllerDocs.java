package com.guisebastiao.lifeshotsapi.controller.docs;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PostRequest;
import com.guisebastiao.lifeshotsapi.dto.request.PostUpdateRequest;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.dto.swagger.DataSuccess;
import com.guisebastiao.lifeshotsapi.dto.swagger.ErrorResponse;
import com.guisebastiao.lifeshotsapi.dto.swagger.InvalidRequestBodyResponse;
import com.guisebastiao.lifeshotsapi.dto.swagger.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
        name = "Posts",
        description = "Endpoints for creating, retrieving, updating, and deleting posts."
)
public interface PostControllerDocs {

    class PostSuccess extends DataSuccess<PostResponse> {}

    @Operation(
            summary = "Create a post",
            description = "Creates a new post for the authenticated user. A post must contain at least one picture and can contain up to 10 pictures."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Post created successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostSuccess.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. Authentication required. The user must be logged in to create post.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid request data. Required fields are missing or improperly formatted.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InvalidRequestBodyResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "An unexpected internal server error occurred.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    ResponseEntity<DefaultResponse<PostResponse>> createPost(PostRequest dto);


    @Operation(
            summary = "Retrieve a post by ID",
            description = "Returns a post by its ID. If the post belongs to a private profile, access is restricted to the owner or mutual followers."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Post retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostSuccess.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to find post.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. You do not have permission to access this post.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "An unexpected internal server error occurred.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    ResponseEntity<DefaultResponse<PostResponse>> findPostById(String postId);


    @Operation(
            summary = "Update a post",
            description = "Updates a post that belongs to the authenticated user. A post must contain at least one picture and cannot exceed 10 pictures after the update."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Post updated successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostSuccess.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request. The number of pictures is invalid or pictures to remove do not exist.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to update post.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. You do not have permission to modify this post.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid request body.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InvalidRequestBodyResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "An unexpected internal server error occurred.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    ResponseEntity<DefaultResponse<PostResponse>> updatePost(String postId, PostUpdateRequest dto);


    @Operation(
            summary = "Delete a post",
            description = "Deletes a post that belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Post deleted successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to delete post.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. You do not have permission to delete this post.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Post not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "An unexpected internal server error occurred.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    ResponseEntity<DefaultResponse<Void>> deletePost(String postId);
}
