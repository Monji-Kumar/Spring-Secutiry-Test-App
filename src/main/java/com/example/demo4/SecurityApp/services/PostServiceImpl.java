package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.dto.PostDTO;
import com.example.demo4.SecurityApp.entities.PostEntity;
import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.exceptions.ResourceNotFoundException;
import com.example.demo4.SecurityApp.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service @RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;

    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(this::getPostDtobyPost)
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO createNewPost(PostDTO inputPost) {
        PostEntity postEntity = getPostByPostDto(inputPost);
        return getPostDtobyPost(postRepository.save(postEntity));
    }

    @Override
    public PostDTO getPostById(Long postId) {
        PostEntity postEntity = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id "+postId));
        return getPostDtobyPost(postEntity);
    }

    private PostDTO getPostDtobyPost(PostEntity post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        return dto;
    }

    private PostEntity getPostByPostDto(PostDTO dto) {
        PostEntity entity = new PostEntity();
        entity.setDescription(dto.getDescription());
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        return entity;
    }
}
