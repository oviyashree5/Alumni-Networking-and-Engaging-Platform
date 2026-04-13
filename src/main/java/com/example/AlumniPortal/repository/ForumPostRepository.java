package com.example.AlumniPortal.repository;

import com.example.AlumniPortal.entity.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    List<ForumPost> findByCategoryIgnoreCase(String category);
}