package org.example.votingsytsem.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
    private String name;
    private String surname;
    private String password;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;
    @Column(unique = true, nullable = false)
    private Long pesel;
    private String role = "ROLE_USER";

    public User modify(User newUserInfo) {
        this.setId(newUserInfo.getId());
        this.setName(newUserInfo.getName());
        this.setSurname(newUserInfo.getSurname());
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
}
