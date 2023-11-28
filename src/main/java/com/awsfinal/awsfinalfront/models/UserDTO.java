package com.awsfinal.awsfinalfront.models;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class UserDTO {
    private String dni;
    private String firstName;
    private String lastName;
    private String email;
    private String birthdate;
    private String phoneNumber;
    private Profession profession;
    private boolean userStatus;
    private boolean editing;
}
