package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;

    @Mock
    private CustomUserUtil customUserUtil;

    @Mock
    private UserRepository userRepository;

    private UserEntity user;
    private String existingEmail;
    private String nonExistingEmail;
    private List<UserDetailsProjection> userDetails;

    @BeforeEach
    void setUp(){
        user = UserFactory.createUserEntity();
        existingEmail = "maria@gmail.com";
        nonExistingEmail = "pedro@gmail.com";

        userDetails = UserDetailsFactory.createCustomAdminClientUser(existingEmail);

        Mockito.when(userRepository.findByUsername(existingEmail)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByUsername(nonExistingEmail)).thenReturn(Optional.empty());

        Mockito.when(userRepository.searchUserAndRolesByUsername(existingEmail)).thenReturn(userDetails);
        Mockito.when(userRepository.searchUserAndRolesByUsername(nonExistingEmail)).thenReturn(new ArrayList<>());
    }

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {

        Mockito.when(customUserUtil.getLoggedUsername()).thenReturn(existingEmail);

        UserEntity result = service.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingEmail,result.getUsername());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

        Mockito.doThrow(ClassCastException.class).when(customUserUtil).getLoggedUsername();

        Assertions.assertThrows(UsernameNotFoundException.class,()->{
            service.authenticated();
        });
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

        UserDetails result = service.loadUserByUsername(existingEmail);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingEmail,result.getUsername());
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

        Assertions.assertThrows(UsernameNotFoundException.class,() ->{
            service.loadUserByUsername(nonExistingEmail);
        });
	}
}
