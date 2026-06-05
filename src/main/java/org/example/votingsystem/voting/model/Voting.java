package org.example.votingsystem.voting.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "votings")
public class Voting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private VotingType type;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private VotingGroup votingGroup;

    @ElementCollection
    private List<String> options;

    @Transient
    private Boolean hasVoted = false;

    @Transient
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public VotingType getType() {
        return type;
    }

    public void setType(VotingType type) {
        this.type = type;
    }

    public VotingGroup getVotingGroup() {
        return votingGroup;
    }

    public void setVotingGroup(VotingGroup votingGroup) {
        this.votingGroup = votingGroup;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Boolean getHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(Boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public String getStatus() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (now.isBefore(startTime)) {
            return "UPCOMING";
        } else if (now.isAfter(endTime)) {
            return "ENDED";
        } else {
            return "ACTIVE";
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }
}