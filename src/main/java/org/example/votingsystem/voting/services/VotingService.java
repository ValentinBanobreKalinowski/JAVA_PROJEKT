package org.example.votingsystem.voting.services;

import org.example.votingsystem.voting.model.*;
import org.example.votingsystem.voting.repository.*;
import org.example.votingsystem.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VotingService {

    private final VotingRepository votingRepository;
    private final VoteRepository voteRepository;

    public VotingService(VotingRepository votingRepository, VoteRepository voteRepository) {
        this.votingRepository = votingRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional
    public Vote castVote(User user, Long votingId, String chosenOption) {
        Voting voting = votingRepository.findById(votingId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono procesu głosowania."));

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(voting.getStartTime()) || now.isAfter(voting.getEndTime())) {
            throw new IllegalStateException("To głosowanie nie jest aktywne.");
        }

        if (!voting.getOptions().contains(chosenOption)) {
            throw new IllegalArgumentException("Wybrana opcja nie istnieje w tym głosowaniu.");
        }

        if (voteRepository.existsByUserIdAndVotingId(user.getId(), votingId)) {
            throw new IllegalStateException("Już oddałeś swój głos w tym głosowaniu.");
        }

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setVoting(voting);
        vote.setChosenOption(chosenOption);
        vote.setVoteWeight(user.getVoteWeight());
        vote.setCastAt(now);

        return voteRepository.save(vote);
    }

    public Map<String, Integer> calculateResults(Long votingId) {
        return voteRepository.findByVotingId(votingId).stream()
                .collect(Collectors.groupingBy(
                        Vote::getChosenOption,
                        Collectors.summingInt(Vote::getVoteWeight)
                ));
    }

    public List<Voting> getAllVotings() {
        return votingRepository.findAll();
    }

    public List<Voting> getActiveVotings() {
        LocalDateTime now = LocalDateTime.now();
        return votingRepository.findAll().stream()
                .filter(v -> now.isAfter(v.getStartTime()) && now.isBefore(v.getEndTime()))
                .collect(Collectors.toList());
    }

    public Voting getVotingById(Long id) {
        return votingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono głosowania o id: " + id));
    }

    @Transactional
    public Voting createVoting(Voting voting) {
        if (voting.getEndTime().isBefore(voting.getStartTime()) || voting.getEndTime().isEqual(voting.getStartTime())) {
            throw new IllegalArgumentException("Data zakończenia musi być późniejsza niż data rozpoczęcia.");
        }

        return votingRepository.save(voting);
    }

    @Transactional
    public void finishVote(Long votingId) {
        Voting voting = getVotingById(votingId);
        voting.setEndTime(LocalDateTime.now());
        votingRepository.save(voting);
    }

    public boolean hasUserVoted(Long userId, Long votingId) {
        return voteRepository.existsByUserIdAndVotingId(userId, votingId);
    }

    public List<Voting> getEndedVotings() {
        LocalDateTime now = LocalDateTime.now();
        return votingRepository.findAll().stream()
                .filter(v -> now.isAfter(v.getEndTime()))
                .collect(Collectors.toList());
    }

    public boolean canEditVoting(Long votingId) {
        Voting voting = getVotingById(votingId);
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(voting.getStartTime());
    }

    @Transactional
    public Voting updateVoting(Long votingId, Voting updatedVoting) {
        Voting voting = getVotingById(votingId);

        if (!canEditVoting(votingId)) {
            throw new IllegalStateException("Nie można edytować głosowania, które już się rozpoczęło lub zakończyło.");
        }

        voting.setTitle(updatedVoting.getTitle());
        voting.setDescription(updatedVoting.getDescription());
        voting.setStartTime(updatedVoting.getStartTime());
        voting.setEndTime(updatedVoting.getEndTime());
        voting.setType(updatedVoting.getType());
        voting.setOptions(updatedVoting.getOptions());

        return votingRepository.save(voting);
    }
}