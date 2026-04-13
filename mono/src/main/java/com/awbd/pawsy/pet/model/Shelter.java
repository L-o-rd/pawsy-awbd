package com.awbd.pawsy.pet.model;

import com.awbd.pawsy.user.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shelters")
public class Shelter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private String email;
    private String phone;

    @OneToOne(optional = false)
    @JoinColumn(name = "manager_id")
    private User manager;
}
