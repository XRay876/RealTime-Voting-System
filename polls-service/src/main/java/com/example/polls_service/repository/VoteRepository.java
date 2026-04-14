package com.example.polls_service.repository;

import com.example.polls_service.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Modifying
    @Query("DELETE FROM Vote v WHERE v.poll.id = :pollId AND v.userId = :userId")
    void deleteByPollIdAndUserId(@Param("pollId") Long pollId, @Param("userId") UUID userId);

    List<Vote> findByPollIdAndUserId(Long pollId, UUID userId);

    long countByPollId(Long pollId);
    long countByPollIdAndOptionId(Long pollId, Long optionId);

    @Query("SELECT DISTINCT v.userId FROM Vote v WHERE v.poll.id = :pollId")
    List<UUID> findParticipantIdsByPollId(@Param("pollId") Long pollId);
}