package com.guisebastiao.lifeshotsapi.controller.docs;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.NotificationParam;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;
import com.guisebastiao.lifeshotsapi.dto.response.UnreadResponse;
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
        name = "Notifications",
        description = "Endpoints for managing user notifications."
)
public interface NotificationControllerDocs {

    class NotificationListSuccess extends PagingSuccess<List<NotificationResponse>> {}

    @Operation(
            summary = "Retrieve user notifications",
            description = "Returns a paginated list of notifications for the authenticated user. Notifications can be filtered by read status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notifications retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotificationListSuccess.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to view notifications.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Invalid pagination parameters. Page number, size, or sorting values are invalid.",
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
    ResponseEntity<DefaultResponse<List<NotificationResponse>>> findAllNotificationsByUser(NotificationParam param, PaginationParam pagination);

    @Operation(
            summary = "Retrieve unread notifications count",
            description = "Returns the total number of unread notifications for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Unread notifications count retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UnreadResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to find unready notifications.",
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
    ResponseEntity<DefaultResponse<UnreadResponse>> findUnreadNotificationsByUser();

    @Operation(
            summary = "Retrieve a notification by ID",
            description = "Returns a specific notification by its ID if it belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NotificationResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to find notification.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. You do not have permission to access this notification.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification not found.",
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
    ResponseEntity<DefaultResponse<NotificationResponse>> findNotificationById(String notificationId);

    @Operation(
            summary = "Mark notification as read",
            description = "Marks a specific notification as read if it belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification marked as read successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to read notification.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. You do not have permission to modify this notification.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification not found.",
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
    ResponseEntity<DefaultResponse<Void>> readNotificationById(String notificationId);

    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all unread notifications of the authenticated user as read."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "All notifications marked as read successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to read all notifications.",
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
    ResponseEntity<DefaultResponse<Void>> readAllUnreadNotifications();

    @Operation(
            summary = "Delete a notification",
            description = "Deletes a specific notification if it belongs to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification deleted successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to delete notification.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden. You do not have permission to delete this notification.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification not found.",
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
    ResponseEntity<DefaultResponse<Void>> deleteNotificationById(String notificationId);

    @Operation(
            summary = "Delete multiple notifications",
            description = "Deletes multiple notifications belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notifications deleted successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required. The user must be logged in to delete notifications.",
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
    ResponseEntity<DefaultResponse<Void>> deleteNotifications(DeleteRequest dto);
}
