package com.guisebastiao.lifeshotsapi.service;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PostRequest;
import com.guisebastiao.lifeshotsapi.dto.request.PostUpdateRequest;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;

public interface PostService {
    DefaultResponse<PostResponse> createPost(PostRequest dto);
    DefaultResponse<PostResponse> findPostById(String postId);
    DefaultResponse<PostResponse> updatePost(String postId, PostUpdateRequest dto);
    DefaultResponse<Void> deletePost(String postId);
}
