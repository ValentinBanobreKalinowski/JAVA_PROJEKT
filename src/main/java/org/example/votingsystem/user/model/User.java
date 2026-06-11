package org.example.votingsystem.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "users_seq", allocationSize = 1)
    private Long id;

    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    @NotBlank
    @Size
    private String password;
    @Past
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;
    @NotNull
    @Column(unique = true, nullable = false)
    private String pesel;
    private String role = "ROLE_USER";

    public User modify(User newUserInfo) {
        this.setId(newUserInfo.getId());
        this.setName(newUserInfo.getName());
        this.setSurname(newUserInfo.getSurname());
        this.setEmail(newUserInfo.getEmail());
        this.setDateOfBirth(newUserInfo.getDateOfBirth());
        this.setPesel(newUserInfo.getPesel());
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return String.valueOf(pesel);
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Integer getVoteWeight() {
        // Default vote weight is 1, can be customized based on user attributes
        return 1;
    }
}
