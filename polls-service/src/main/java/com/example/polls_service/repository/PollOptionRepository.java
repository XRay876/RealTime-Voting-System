package com.example.polls_service.repository;

import com.example.polls_service.model.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PollOptionRepository extends JpaRepository<PollOption, Long> {
    List<PollOption> findByPollId(Long pollId);
    Optional<PollOption> findByIdAndPollId(Long id, Long pollId);
}