package org.example.votingsytsem.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginValidation {
    private String name;
    private String surname;
    private Long pesel;
    private String password;
}
