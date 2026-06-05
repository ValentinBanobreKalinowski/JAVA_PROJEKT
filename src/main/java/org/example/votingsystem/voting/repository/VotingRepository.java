package org.example.votingsystem.voting.repository;

import org.example.votingsystem.voting.model.Voting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface VotingRepository extends JpaRepository<Voting, Long> {
    @Query("SELECT v FROM Voting v WHERE v.votingGroup.id IN :groupIds " +
            "AND :now BETWEEN v.startTime AND v.endTime")
    List<Voting> findActiveVotingsForUserGroups(List<Long> groupIds, LocalDateTime now);
}