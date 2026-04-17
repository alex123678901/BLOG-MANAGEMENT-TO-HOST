package com.blog.blogmanagementsystem.repository;

import com.blog.blogmanagementsystem.model.BlogPost;
import com.blog.blogmanagementsystem.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(BlogPost post);

    List<Comment> findByPostAndParentIsNull(BlogPost post);
}
