package com.blog.blogmanagementsystem.repository;

import com.blog.blogmanagementsystem.model.BlogPost;
import com.blog.blogmanagementsystem.model.PostStatus;
import com.blog.blogmanagementsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long>, JpaSpecificationExecutor<BlogPost> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "author" })
    Page<BlogPost> findByAuthor(User author, Pageable pageable);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "author" })
    Page<BlogPost> findByStatus(PostStatus status, Pageable pageable);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "author" })
    Page<BlogPost> findByTitleContainingIgnoreCaseAndStatus(String title, PostStatus status, Pageable pageable);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "author" })
    Page<BlogPost> findAll(org.springframework.data.jpa.domain.Specification<BlogPost> spec, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.viewCount) FROM BlogPost p")
    Long sumViewCount();
}
