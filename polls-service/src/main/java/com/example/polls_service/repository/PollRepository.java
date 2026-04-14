package com.example.polls_service.repository;

import com.example.polls_service.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByCreatedBy(UUID createdBy);
}
