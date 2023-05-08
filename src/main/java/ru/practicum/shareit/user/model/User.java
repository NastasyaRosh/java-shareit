package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
}
