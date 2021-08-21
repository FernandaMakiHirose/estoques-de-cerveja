package one.digitalinnovation.beerstock.service;

import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.exception.BeerStockExceededException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repository.BeerRepository;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// vai usar o mockito para criar os objetos dublês
@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    // cria o mock da classe
    @Mock
    private BeerRepository beerRepository;

    private BeerMapper beerMapper = BeerMapper.INSTANCE;

    // cria uma instância da classe e injeta os mocks que são criados com as anotações @Mock(ou @Spy) nesta instância
    @InjectMocks
    private BeerService beerService;


    // valida se a cerveja foi criada com sucesso
    @Test
    // quando uma cerveja for informada ela deve ser criada
    void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
        // given
        // cria o DTO e retorna o .toBeerDTO com todos os dados preenchidos
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        // cria a entidade cerveja
        Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);

        // when
        // vai pegar pelo nome da cerveja e não retorna nada
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
        // vai salvar e retornar esses dados
        when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        //then - hamcrest
        // cria a cerveja
        BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);

        // o id da cerveja criada é igual ao id do DTO passado
        assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));

        // o nome da cerveja criada é igual ao nome do DTO passado
        assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));

        // a quantidade da cerveja criada é igual a quantidade do DTO passado
        assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));
    }

    // teste caso uma cerveja seja cadastrada no sistema
    @Test
    // quando uma cerveja já criada é informada uma exceção vai ser lançada
    void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown() {
        // given
        // cria o DTO e retorna o .toBeerDTO com todos os dados preenchidos
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        // cria a entidade cerveja
        Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);

        // when
        // vai pegar pelo nome da cerveja e retorna a entidade da cerveja
        when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        // then
        // se a cerveja já estiver cadastrada vai lançar a mensagem da BeerAlreadyRegisteredException.class
        assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectedBeerDTO));
    }

    @Test
    // quando um nome válido de BeerName é dado retorna uma cerveja
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
        // given
        // cria o DTO e retorna o .toBeerDTO com todos os dados preenchidos
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        // cria a entidade cerveja encontrada
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        // when
        // quando chamar a repository precisa chamar a cerveja que encontrar para verificar se ela foi chamada com sucesso 
        when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));

        // then
        // retorna o método da service e depois de achar a cerveja vai converter a partir da entidade um DTO 
        BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());

        // faz a validação, o objeto mock é igual ao objeto de retorno, com isso a cerveja com criada com sucesso
        assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));
    }

    @Test
    // quando uma cerveja não registrada vai ser lançar essa exceção 
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
        // given
        // cria o DTO e retorna o .toBeerDTO com todos os dados preenchidos
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // when
        // quando chamar a repository precisa chamar a cerveja que encontrar para verificar se ela foi chamada com sucesso e não retorna nada 
        when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());

        // then
        // se a cerveja não foi encontrada 
        assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedFoundBeerDTO.getName()));
    }

    @Test
    // quando a lista de cervejas for chamada vai retornar uma lista de cervejas
    void whenListBeerIsCalledThenReturnAListOfBeers() {
        // given
        // cria o DTO e retorna o .toBeerDTO com todos os dados preenchidos
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // cria a entidade cerveja encontrada
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        //when
        // encontra todas as cervejas e retorna uma lista imutável das cervejas encontradas
        when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

        //then
        // lista todas as cervejas da service
        List<BeerDTO> foundListBeersDTO = beerService.listAll();

        // a lista de cervejas não está vazia
        assertThat(foundListBeersDTO, is(not(empty())));
        // o primeiro valor da lista é igual ao valor esperado
        assertThat(foundListBeersDTO.get(0), is(equalTo(expectedFoundBeerDTO)));
    }

    @Test
    // se essa lista não retornar nenhum resultado
    void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
        //when
        // encontra todas as cervejas e retorna uma lista vazia 
        when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        // se encontrar alguma cerveja vai mostrá-la
        List<BeerDTO> foundListBeersDTO = beerService.listAll();

        // a lista de cervejas está vazia
        assertThat(foundListBeersDTO, is(empty()));
    }

    @Test
    // método que faz a exclusão de cervejas
    void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException{
        // given
        // cria o DTO e retorna o .toBeerDTO com todos os dados preenchidos
        BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        // cria a entidade cerveja encontrada
        Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);

        // when
        // encontra o id e se o id existir deleta a cerveja
        when(beerRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
        // não faz nada quando deleta
        doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());

        // then 
        // deleta achando o id
        beerService.deleteById(expectedDeletedBeerDTO.getId());

        // verifica se o .findById foi chamado uma vez
        verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
        // verifica se o .deleteById foi chamado uma vez
        verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
    }

    @Test
    // quando um incremento é chamado a cerveja vai ser guardada
    void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
        //given
        // cria o DTO e retorna o .toBeerDTO com todos os dados preenchidos
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // cria a entidade cerveja encontrada
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        //when
        // encontra a cerveja por id e retorna a mesma
        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        // salva a cerveja e retorna a esperada
        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        // quantidade de cervejas que quer adicionar
        int quantityToIncrement = 10;
        // a quantidade de cervejas após o incremento é igual a cerveja que vai retornar
        int expectedQuantityAfterIncrement = expectedBeerDTO.getQuantity() + quantityToIncrement;

        // then
        // informa o id da cerveja que quer incrementar
        BeerDTO incrementedBeerDTO = beerService.increment(expectedBeerDTO.getId(), quantityToIncrement);

        // esses dois valores são iguais
        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBeerDTO.getQuantity()));

        // todas as cervejas de incremento vão ser menor que a quantidade máxima do estoque
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedBeerDTO.getMax()));
    }

    @Test
    // quando o incremento é maior que a quantidade máxima
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        // cria o DTO e retorna o .toBeerDTO com todos os dados preenchidos
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // cria a entidade cerveja encontrada
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        // encontra a cerveja por id e retorna a mesma
        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        // quantidade de cervejas que quer adicionar
        int quantityToIncrement = 80;

        // informa o id da cerveja que quer incrementar
        assertThrows(BeerStockExceededException.class, () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }

    @Test
    // quando o incremento após a soma é maior que a quantidade máxima
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        // cria o DTO e retorna o .toBeerDTO com todos os dados preenchidos
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // cria a entidade cerveja encontrada
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        // encontra a cerveja por id e retorna a mesma
        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        // quantidade de cervejas que quer adicionar
        int quantityToIncrement = 45;

        // informa o id da cerveja que quer incrementar
        assertThrows(BeerStockExceededException.class, () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }

    @Test
    // quando o incremento é inválido 
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        // quantidade de cervejas que quer adicionar
        int quantityToIncrement = 10;

        // quando ser chamado por esse id não vai retornar nada
        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());

        // vai lançar uma exceção caso a cerveja não seja encontrada e faz um incremento
        assertThrows(BeerNotFoundException.class, () -> beerService.increment(INVALID_BEER_ID, quantityToIncrement));
    }
//
//    @Test
//    void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
//
//        int quantityToDecrement = 5;
//        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
//        BeerDTO incrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDTO.getQuantity()));
//        assertThat(expectedQuantityAfterDecrement, greaterThan(0));
//    }
//
//    @Test
//    void whenDecrementIsCalledToEmptyStockThenEmptyBeerStock() throws BeerNotFoundException, BeerStockExceededException {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//        when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);
//
//        int quantityToDecrement = 10;
//        int expectedQuantityAfterDecrement = expectedBeerDTO.getQuantity() - quantityToDecrement;
//        BeerDTO incrementedBeerDTO = beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement);
//
//        assertThat(expectedQuantityAfterDecrement, equalTo(0));
//        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedBeerDTO.getQuantity()));
//    }
//
//    @Test
//    void whenDecrementIsLowerThanZeroThenThrowException() {
//        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);
//
//        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
//
//        int quantityToDecrement = 80;
//        assertThrows(BeerStockExceededException.class, () -> beerService.decrement(expectedBeerDTO.getId(), quantityToDecrement));
//    }
//
//    @Test
//    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
//        int quantityToDecrement = 10;
//
//        when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());
//
//        assertThrows(BeerNotFoundException.class, () -> beerService.decrement(INVALID_BEER_ID, quantityToDecrement));
//    }
}
