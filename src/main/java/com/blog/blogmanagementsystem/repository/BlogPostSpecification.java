package com.blog.blogmanagementsystem.repository;

import com.blog.blogmanagementsystem.model.BlogPost;
import com.blog.blogmanagementsystem.model.PostStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BlogPostSpecification {

    public static Specification<BlogPost> filterPosts(PostStatus status, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                        searchPattern);
                Predicate contentPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("content")),
                        searchPattern);
                predicates.add(criteriaBuilder.or(titlePredicate, contentPredicate));
            }

            // By default, if search is not specified, we can order by date
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
