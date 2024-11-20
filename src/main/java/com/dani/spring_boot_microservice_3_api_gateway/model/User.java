package com.dani.spring_boot_microservice_3_api_gateway.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name= "users")
@Data
public class User {


    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name="username" , unique = true, length = 100)
    private String password;

    @Column(name="nombre")
    private String nombre;

    @Column(name="fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role;


}
