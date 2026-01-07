package com.imt.framework.web.tuto;

import com.imt.framework.web.tuto.entities.Livre;
import com.imt.framework.web.tuto.repositories.LivreRepository;
import com.imt.framework.web.tuto.resources.LivreResource;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LivreResourceTest {
    @Mock
    private LivreRepository livreRepository;

    @InjectMocks
    private LivreResource livreResource;

    @Test
    @SuppressWarnings("unchecked")
    void shouldGetAllBooksWhenNoParam() {
        // Arrange
        Livre l1 = new Livre(); l1.setTitre("A");
        when(livreRepository.findAll()).thenReturn(List.of(l1));

        // Act
        Response response = livreResource.getBooks(null);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        List<Livre> entity = (List<Livre>) response.getEntity();
        assertThat(entity).hasSize(1);
        verify(livreRepository, times(1)).findAll();
    }

    @Test
    void shouldGetBooksFilteredByPrice() {
        // Arrange
        Livre l1 = new Livre(); l1.setPrice(10.0);
        when(livreRepository.getBooksWithMaxPrice(15.0)).thenReturn(List.of(l1));

        // Act
        Response response = livreResource.getBooks(15.0);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        verify(livreRepository, times(1)).getBooksWithMaxPrice(15.0);
        verify(livreRepository, never()).findAll();
    }

    @Test
    void shouldCreateBook() {
        // Arrange
        Livre l = new Livre();

        // Act
        livreResource.createBook(l);

        // Assert
        verify(livreRepository, times(1)).save(l);
    }

    @Test
    void shouldUpdateBookWhenExists() throws Exception {
        // Arrange
        Integer id = 1;
        Livre existingLivre = new Livre();
        existingLivre.setId(id);
        existingLivre.setTitre("Old Title");

        Livre newInfo = new Livre();
        newInfo.setTitre("New Title");
        newInfo.setAuteur("New Author");
        newInfo.setPrice(20.0);

        when(livreRepository.findById(id)).thenReturn(Optional.of(existingLivre));

        // Act
        livreResource.updateBook(id, newInfo);

        // Assert
        verify(livreRepository).save(argThat(savedLivre ->
                savedLivre.getTitre().equals("New Title") &&
                        savedLivre.getAuteur().equals("New Author") &&
                        savedLivre.getId().equals(1)
        ));
    }

    @Test
    void shouldThrowExceptionWhenUpdateUnknownBook() {
        // Arrange
        when(livreRepository.findById(99)).thenReturn(Optional.empty());
        Livre l = new Livre();

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            livreResource.updateBook(99, l);
        });

        assertThat(exception.getMessage()).isEqualTo("Livre inconnu");
        verify(livreRepository, never()).save(any());
    }

    @Test
    void shouldDeleteBook() {
        // Act
        livreResource.deleteBook(1);

        // Assert
        verify(livreRepository, times(1)).deleteById(1);
    }
}
