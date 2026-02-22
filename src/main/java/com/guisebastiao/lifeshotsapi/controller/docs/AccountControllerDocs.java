package com.guisebastiao.lifeshotsapi.controller.docs;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteAccountRequest;
import com.guisebastiao.lifeshotsapi.dto.request.ProfilePrivacyRequest;
import com.guisebastiao.lifeshotsapi.dto.request.UpdatePasswordRequest;
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
        name = "Account",
        description = "Operations related to account management, security settings, and profile privacy."
)
public interface AccountControllerDocs {

    @Operation(
            summary = "Update profile privacy settings",
            description = "Updates the privacy configuration of the authenticated user's profile. This operation allows the user to control the visibility of their profile information."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile privacy updated successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to perform this action.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict while updating profile privacy due to business rule constraints.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid request body. One or more fields contain invalid or missing values.",
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
    ResponseEntity<DefaultResponse<Void>> setProfilePrivacy(ProfilePrivacyRequest dto);


    @Operation(
            summary = "Update account password",
            description = "Updates the password of the authenticated user's account. The current password must be provided and validated before the new password is applied."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password updated successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to update the password.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Current password is incorrect.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid request body. Password does not meet security requirements or required fields are missing.",
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
    ResponseEntity<DefaultResponse<Void>> updatePassword(UpdatePasswordRequest dto);

    @Operation(
            summary = "Delete user account",
            description = "Permanently deletes the authenticated user's account and all associated data. This action is irreversible and requires password confirmation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Login is required.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )

            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Password incorrect.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )

            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid request body",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InvalidRequestBodyResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    ResponseEntity<DefaultResponse<Void>> deleteAccount(DeleteAccountRequest dto);
}
