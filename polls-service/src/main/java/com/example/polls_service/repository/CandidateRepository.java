package com.example.polls_service.repository;

import com.example.polls_service.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByPollId(Long pollId);
    Optional<Candidate> findByIdAndPollId(Long id, Long pollId);
}