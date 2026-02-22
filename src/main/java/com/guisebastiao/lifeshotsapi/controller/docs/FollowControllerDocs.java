package com.guisebastiao.lifeshotsapi.controller.docs;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.FollowParam;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.response.FollowResponse;
import com.guisebastiao.lifeshotsapi.dto.swagger.ErrorResponse;
import com.guisebastiao.lifeshotsapi.dto.swagger.InvalidRequestBodyResponse;
import com.guisebastiao.lifeshotsapi.dto.swagger.PagingSuccess;
import com.guisebastiao.lifeshotsapi.dto.swagger.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(
        name = "Follow",
        description = "Endpoints responsible for managing follow relationships between user profiles."
)
public interface FollowControllerDocs {

    class FollowListSuccess extends PagingSuccess<List<FollowResponse>> {}

    @Operation(
            summary = "Follow profile",
            description = "Creates a follow relationship between the authenticated user and the specified profile. The user cannot follow themselves or follow the same profile more than once."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Profile followed successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Follow relationship already exists.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to follow a profile.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid operation. Users cannot follow their own profile.",
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
    ResponseEntity<DefaultResponse<Void>> follow(String profileId);

    @Operation(
            summary = "Retrieve authenticated user's follow relationships",
            description = "Retrieves a paginated list of follow relationships associated with the authenticated user. The result may include followers, following, or both depending on the provided filter parameter."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Follow relationships retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FollowListSuccess.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to access follow data.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid pagination or filter parameters.",
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
    ResponseEntity<DefaultResponse<List<FollowResponse>>> findAllMyFollows(FollowParam param, PaginationParam pagination);

    @Operation(
            summary = "Retrieve profile follow relationships",
            description = "Retrieves a paginated list of follow relationships for the specified profile. The result may include followers or following users depending on the provided filter parameter."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Follow relationships retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FollowListSuccess.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to access follow data.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid pagination or filter parameters.",
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
    ResponseEntity<DefaultResponse<List<FollowResponse>>> findAllFollows(String profileId, FollowParam param, PaginationParam pagination);

    @Operation(
            summary = "Unfollow profile",
            description = "Removes the follow relationship between the authenticated user and the specified profile. The user cannot unfollow themselves."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile unfollowed successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Follow relationship does not exist.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to unfollow a profile.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid operation. Users cannot unfollow their own profile.",
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
    ResponseEntity<DefaultResponse<Void>> unfollow(String profileId);
}
