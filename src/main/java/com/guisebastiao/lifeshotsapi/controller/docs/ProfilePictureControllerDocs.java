package com.guisebastiao.lifeshotsapi.controller.docs;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.ProfilePictureRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfilePictureResponse;
import com.guisebastiao.lifeshotsapi.dto.swagger.DataSuccess;
import com.guisebastiao.lifeshotsapi.dto.swagger.ErrorResponse;
import com.guisebastiao.lifeshotsapi.dto.swagger.InvalidRequestBodyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Profile Picture", description = "Profile picture management endpoints")
public interface ProfilePictureControllerDocs {

    class ProfilePictureSuccess extends DataSuccess<ProfilePictureResponse> {}

    @Operation(
            summary = "Upload profile picture",
            description = "Uploads a profile picture for the authenticated user. Only one profile picture is allowed."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Profile picture uploaded successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfilePictureSuccess.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file upload request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to upload profile picture.",
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
                    description = "Profile picture already exists.",
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
    ResponseEntity<DefaultResponse<ProfilePictureResponse>> uploadProfilePicture(ProfilePictureRequest dto);

    @Operation(
            summary = "Find profile picture by profile id",
            description = "Returns the profile picture of a given profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile picture found successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfilePictureSuccess.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to find profile picture.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile picture not found.",
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
    ResponseEntity<DefaultResponse<ProfilePictureResponse>> findProfilePictureById(String profileId);

    @Operation(
            summary = "Delete authenticated user's profile picture",
            description = "Deletes the current authenticated user's profile picture."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile picture deleted successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid delete request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to delete profile picture.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profile or profile picture not found.",
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
    ResponseEntity<DefaultResponse<Void>> deleteProfilePicture();
}
