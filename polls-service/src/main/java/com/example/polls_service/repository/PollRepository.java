package com.example.polls_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.polls_service.model.Poll;

public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByCreatedBy(UUID createdBy);
}
