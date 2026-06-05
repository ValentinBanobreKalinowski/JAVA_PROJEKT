package org.example.votingsystem.voting.repository;

import org.example.votingsystem.voting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByUserIdAndVotingId(Long userId, Long votingId);
    List<Vote> findByVotingId(Long votingId);
}
