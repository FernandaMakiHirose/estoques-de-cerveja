package one.digitalinnovation.beerstock.repository;

import one.digitalinnovation.beerstock.entity.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// essa classe conversa com o banco de dados
public interface BeerRepository extends JpaRepository<Beer, Long> {

    // busca todas as cervejas pelo nome, o 'Optional' ajuda a fazer validações
    Optional<Beer> findByName(String name);
}
