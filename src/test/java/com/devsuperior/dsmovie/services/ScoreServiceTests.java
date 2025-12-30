package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.module.ResolutionException;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserService userService;

    @Mock
    private ScoreRepository scoreRepository;

    private Long existingId,nonExistingId;
    private MovieEntity movie;
    private MovieDTO movieDTO;
    private ScoreEntity score;
    private ScoreDTO scoreDTO;
    private UserEntity user;

    @BeforeEach
    void setUp(){
        existingId = 1L;
        nonExistingId = 2L;

        movie = MovieFactory.createMovieEntity();
        movieDTO = new MovieDTO(movie);

        score = ScoreFactory.createScoreEntity();
        scoreDTO = new ScoreDTO(score);

        Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        Mockito.when(movieRepository.findById(existingId)).thenReturn(Optional.of(movie));

        Mockito.when(userService.authenticated()).thenReturn(user);

        Mockito.when(scoreRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(score);

        Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movie);
    }
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {

        MovieDTO result = service.saveScore(scoreDTO);

        Assertions.assertNotNull(result);
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            movie.setId(nonExistingId);
            score.setMovie(movie);

            ScoreDTO result = new ScoreDTO(score);

            service.saveScore(result);
        });
	}
}
