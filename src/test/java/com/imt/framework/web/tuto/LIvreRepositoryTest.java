package com.imt.framework.web.tuto;

import com.imt.framework.web.tuto.entities.Livre;
import com.imt.framework.web.tuto.repositories.LivreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LIvreRepositoryTest {
    @Autowired
    private LivreRepository livreRepository;

    @Test
    void shouldFindBooksWithMaxPrice() {
        // Arrange : On prépare la BDD
        Livre pasCher = new Livre();
        pasCher.setTitre("Petit livre");
        pasCher.setPrice(10.0);

        Livre cher = new Livre();
        cher.setTitre("Grand livre");
        cher.setPrice(50.0);

        livreRepository.save(pasCher);
        livreRepository.save(cher);

        List<Livre> result = livreRepository.getBooksWithMaxPrice(20.0);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitre()).isEqualTo("Petit livre");
    }
}
