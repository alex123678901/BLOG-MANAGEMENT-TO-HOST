package com.blog.blogmanagementsystem.repository;

import com.blog.blogmanagementsystem.model.BlogPost;
import com.blog.blogmanagementsystem.model.Like;
import com.blog.blogmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, BlogPost post);

    Long countByPost(BlogPost post);
}
