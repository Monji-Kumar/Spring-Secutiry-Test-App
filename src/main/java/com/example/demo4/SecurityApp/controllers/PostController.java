package com.example.demo4.SecurityApp.controllers;

import com.example.demo4.SecurityApp.dto.PostDTO;
import com.example.demo4.SecurityApp.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    @Secured("ROLE_USER")
    public List<PostDTO> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{postId}")
//    @Secured("ROLE_USER")
    @PreAuthorize("@postSecurity.isOwnerOfPost(#postId)")
//    @PreAuthorize("hasAnyRole('USER')")
    public PostDTO getPostById(@PathVariable Long postId) {
        System.out.print("Inside getpostbyId");return postService.getPostById(postId);
    }

    @PostMapping
    public PostDTO createNewPost(@RequestBody PostDTO inputPost) {
        return postService.createNewPost(inputPost);
    }

}
