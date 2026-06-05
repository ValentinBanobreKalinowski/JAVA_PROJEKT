package org.example.votingsystem.voting.model;

import jakarta.persistence.*;
import org.example.votingsystem.user.model.User;
import java.util.Set;

@Entity
@Table(name = "voting_groups")
public class VotingGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members;

}