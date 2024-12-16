package com.dani.spring_boot_microservice_3_api_gateway.service;

import com.dani.spring_boot_microservice_3_api_gateway.model.Role;
import com.dani.spring_boot_microservice_3_api_gateway.model.User;
import com.dani.spring_boot_microservice_3_api_gateway.repository.UserRepository;
import com.dani.spring_boot_microservice_3_api_gateway.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    /*METODO PARA REGISTRAR USUARIO, CREAR UN METODO BEAN PARA EN LA CLASE PRINCIPAL PARA INYECTAR A LA CLASE USER
    SERVICE Y NO TENER CONTRASEÑAS PLANAS*/

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public User saveUser(User user){

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setFechaCreacion(LocalDateTime.now());

        User userCreated =  userRepository.save(user);

        String jwt = jwtProvider.generateToken(userCreated);
        userCreated.setToken(jwt);

        return userCreated;
    }

    //METODO PARA USUARIO POR USERNAME
    @Override
    public Optional<User> findByUsername(String username){

        return userRepository.findByUsername(username);
    }

    //METODO PARA ACTUALIZAR ROL DEL USUARIO
    //Agregar transactional cada vez que se utiliza una sentencia sql que modifica data en el servicio

    @Transactional
    @Override
    public void changeRole(Role newRole, String username){

        userRepository.updateUserRole(username, newRole);
    }



}
