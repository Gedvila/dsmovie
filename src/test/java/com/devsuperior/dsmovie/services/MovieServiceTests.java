package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;

    @Mock
    private MovieRepository movieRepository;

    private PageImpl page;
    private MovieEntity movie;
    private MovieDTO movieDTO;
    private Long existingId, nonExistingId,dependetId;

    @BeforeEach
    void setUp(){

        existingId = 1L;
        nonExistingId = 2L;
        dependetId = 5L;

        movie = MovieFactory.createMovieEntity();
        movieDTO = new MovieDTO(movie);

        page = new PageImpl<>(List.of(movie));

        Mockito.when(movieRepository.searchByTitle(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(movieRepository.findById(existingId)).thenReturn(Optional.of(movie));
        Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movie);

        Mockito.when(movieRepository.getReferenceById(existingId)).thenReturn(movie);
        Mockito.when(movieRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(movieRepository.existsById(existingId)).thenReturn(true);
        Mockito.when(movieRepository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(movieRepository.existsById(dependetId)).thenReturn(true);

        Mockito.doNothing().when(movieRepository).deleteById(existingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(movieRepository).deleteById(dependetId);
    }
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
        Pageable pageable = PageRequest.of(0,10);

        Page<MovieDTO> result = service.findAll("Filme",pageable);

        Assertions.assertNotNull(result);
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {

        MovieDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            service.findById(nonExistingId);
        });
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
        MovieDTO result = service.insert(movieDTO);

        Assertions.assertNotNull(result);
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
        MovieDTO result = service.update(existingId,movieDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId,result.getId());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            service.update(nonExistingId,movieDTO);
        });
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(()->{
            service.delete(existingId);
        });

	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            service.delete(nonExistingId);
        });
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class,()->{
            service.delete(dependetId);
        });
	}
}
