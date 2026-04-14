package com.example.polls_service.repository;

import com.example.polls_service.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByPollIdAndUserId(Long pollId, UUID userId);
    Optional<Vote> findByPollIdAndUserId(Long pollId, UUID userId);
    long countByPollId(Long pollId);
    long countByPollIdAndCandidateId(Long pollId, Long candidateId);
}
