package org.example.votingsystem.voting.model;

import jakarta.persistence.*;
import org.example.votingsystem.user.model.User;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "votes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "voting_id"})
        }
)
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "voting_id", nullable = false)
    private Voting voting;

    @Column(nullable = false)
    private String chosenOption;

    @Column(nullable = false)
    private Integer voteWeight;

    private LocalDateTime castAt;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Voting getVoting() {
        return voting;
    }

    public void setVoting(Voting voting) {
        this.voting = voting;
    }

    public String getChosenOption() {
        return chosenOption;
    }

    public void setChosenOption(String chosenOption) {
        this.chosenOption = chosenOption;
    }

    public Integer getVoteWeight() {
        return voteWeight;
    }

    public void setVoteWeight(Integer voteWeight) {
        this.voteWeight = voteWeight;
    }

    public LocalDateTime getCastAt() {
        return castAt;
    }

    public void setCastAt(LocalDateTime castAt) {
        this.castAt = castAt;
    }
}