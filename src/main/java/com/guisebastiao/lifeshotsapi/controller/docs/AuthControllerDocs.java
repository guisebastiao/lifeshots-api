package com.guisebastiao.lifeshotsapi.controller.docs;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.LoginRequest;
import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.AuthResponse;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.io.IOException;

@Tag(
        name = "Authentication",
        description = "Endpoints responsible for user authentication, authorization, and session management."
)
public interface AuthControllerDocs {

    class AuthSuccess extends DataSuccess<AuthResponse> {}

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user using email and password credentials. If the credentials are valid, an access token is issued and authentication cookies are set when applicable."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful. Access token and user information returned.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthSuccess.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed. Incorrect email or password.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid request body. Required fields are missing or improperly formatted.",
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
    ResponseEntity<DefaultResponse<AuthResponse>> login(HttpServletResponse httpResponse, LoginRequest dto);


    @Operation(
            summary = "Google OAuth login",
            description = "Initiates the Google OAuth 2.0 authentication flow. The user is redirected to the Google authorization server and, upon successful authentication, redirected back to the application callback endpoint."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to Google OAuth provider or application callback URL.",
                    content = @Content()
            )
    })
    void googleLogin(HttpServletResponse response) throws IOException;

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with the provided registration details. If successful, authentication information may be returned depending on the application configuration."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthSuccess.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Specified role not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Registration failed. Email already in use or username unavailable.",
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
    ResponseEntity<DefaultResponse<AuthResponse>> register(RegisterRequest dto);

    @Operation(
            summary = "Refresh authentication token",
            description = "Generates a new access token using a valid refresh token stored in an HTTP-only cookie. If the refresh token is valid, a new access token and updated cookies are issued."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Refresh token cookie not found or invalid.",
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
    ResponseEntity<DefaultResponse<Void>> refresh(HttpServletRequest httpRequest, HttpServletResponse httpResponse);

    @Operation(
            summary = "Logout user",
            description = "Logs out the authenticated user by invalidating authentication cookies and clearing session data."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful. Authentication session invalidated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
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
    ResponseEntity<DefaultResponse<Void>> logout(HttpServletResponse httpResponse);
}
