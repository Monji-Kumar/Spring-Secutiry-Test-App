package com.example.demo4.SecurityApp.utils;

import com.example.demo4.SecurityApp.dto.PostDTO;
import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostSecurity {

    private final PostService postService;

    boolean isOwnerOfPost(Long postId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostDTO postDTO = postService.getPostById(postId);
        return postDTO.getUserDto().getId().equals(user.getId());
    }
}
