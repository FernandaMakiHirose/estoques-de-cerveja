package one.digitalinnovation.beerstock.controller;

import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.dto.QuantityDTO;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.exception.BeerStockExceededException;
import one.digitalinnovation.beerstock.service.BeerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static one.digitalinnovation.beerstock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

    // api principal
    private static final String BEER_API_URL_PATH = "/api/v1/beers";

    // id em uma cerveja válida
    private static final long VALID_BEER_ID = 1L;

    // id em uma cerveja inválida
    private static final long INVALID_BEER_ID = 2l;

    // caminho do incremento
    private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";

    // caminho do decremento
    private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    // antes de cada teste faz a configuração do MockMvc
    @BeforeEach
    void setUp() {

        // faz o setUp para a classe beerController
        mockMvc = MockMvcBuilders.standaloneSetup(beerController)

                // adiciona suporte a projetos pagináveis 
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())

                // parte de visualização do mapeamento de jackson para json
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    // quando tem o objeto DTO, faz o post com o conteúdo do json passando no corpo uma String de Json, então espera um status como criado e espera retornar o nome, a marca e o tipo da cerveja 
    @Test
    // quando o método POST é chamado uma cerveja é criada, assim lança uma exceção
    void whenPOSTIsCalledThenABeerIsCreated() throws Exception {
        // given
        // recebe no corpo um BeerDTO
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // when
        // quando cria uma Beer passando o beerDTO retorna o DTO criado 
        when(beerService.createBeer(beerDTO)).thenReturn(beerDTO);

        // then
        // é um endereço que permite acessar uma API e seus vários recursos
        mockMvc.perform(post(BEER_API_URL_PATH)
                // tipo de conteúdo
                .contentType(MediaType.APPLICATION_JSON)
                // recebe o conteúdo asJsonString
                .content(asJsonString(beerDTO)))
                // espera o status 201
                .andExpect(status().isCreated())
                // espera um nome
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                // espera uma marca
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                // espera um tipo
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    // testa se realmente não deixa passar um campo nulo
    @Test
    // quando o método post é chamado sem um campo nulo um erro é retornado
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        // recebe no corpo um BeerDTO
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        // tira a marca da cerveja
        beerDTO.setBrand(null);

        // then
        // é um endereço que permite acessar uma API e seus vários recursos
        mockMvc.perform(post(BEER_API_URL_PATH)
                // tipo de conteúdo
                .contentType(MediaType.APPLICATION_JSON)
                // recebe o conteúdo asJsonString
                .content(asJsonString(beerDTO)))
                // espera receber uma requisição de erro
                .andExpect(status().isBadRequest());
    }

    @Test
    // quando o método GET é chamado pelo nome correto da cerveja cadastrada vai retornar um status OK
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        // recebe no corpo um BeerDTO
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        // quando encontrar por nome pega o nome e retorna a cerveja
        when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);

        // then
        // passa o endereço que permite acessar uma API e seus vários recursos e um nome
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                // tipo de conteúdo
                .contentType(MediaType.APPLICATION_JSON))
                // espera que o status seja OK
                .andExpect(status().isOk())
                // espera um nome
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                // espera uma marca
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                // espera um tipo
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    // quando o método GET é chamado sem registrar o nome vai retornar um status NotFound
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        // recebe no corpo um BeerDTO
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        // recebe o nome e lança uma exceção que a cerveja não foi encontrada
        when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);

        // then
        // passa o endereço que permite acessar uma API e seus vários recursos e um nome
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                // tipo de conteúdo
                .contentType(MediaType.APPLICATION_JSON))
                // espera que o status seja NotFound
                .andExpect(status().isNotFound());
    }

    @Test
    // quando o método GET é chamado com uma lista de cerveja o status OK é retornado
    void whenGETListWithBeersIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        // recebe no corpo um BeerDTO
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        // mostra todas as cervejas da lista e retorna uma lista imutável contendo a beerDTO
        when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

        // then
        // passa o endereço que permite acessar uma API e seus vários recursos
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
                // tipo de conteúdo
                .contentType(MediaType.APPLICATION_JSON))
                // espera um status ok 
                .andExpect(status().isOk())
                // pega o primeiro nome da lista
                .andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
                // pega a primeira marca da lista
                .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
                // pega o primeiro tipo da lista
                .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
    }

    @Test
    // quando o método GET é chamado em uma lista sem as cerveja o status OK é retornado
    void whenGETListWithoutBeersIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        // recebe no corpo um BeerDTO
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        //when
        // mostra todas as cervejas da lista e retorna uma lista imutável contendo a beerDTO
        when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

        // then
        // passa o endereço que permite acessar uma API e seus vários recursos
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
                // tipo de conteúdo
                .contentType(MediaType.APPLICATION_JSON))
                // espera um status ok 
                .andExpect(status().isOk());
    }

    @Test
    // quando o método DELETE é chamado sem validação nenhum status é retornado
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        // recebe no corpo um BeerDTO
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

        // when
        // não faz nada quando deleta pelo id
        doNothing().when(beerService).deleteById(beerDTO.getId());

        // then
        // passa o endereço que permite acessar uma API e seus vários recursos e o id
        mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + beerDTO.getId())
                // tipo de conteúdo
                .contentType(MediaType.APPLICATION_JSON))
                // espera um status sem conteúdo
                .andExpect(status().isNoContent());
    }

    @Test
    // quando o método DELETE é inválido vai retornar um status NotFound
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        // when
        // vai deletar pelo id
        doThrow(BeerNotFoundException.class).when(beerService).deleteById(INVALID_BEER_ID);

        // then
        // passa o endereço que permite acessar uma API e seus vários recursos e o id inválido
        mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                // tipo de conteúdo
                .contentType(MediaType.APPLICATION_JSON))
                // espera um status sem conteúdo
                .andExpect(status().isNotFound());
    }

    @Test
    // quando o método PATCH é chamado para incrementar desconto um status OK é retornado
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        // recebe no corpo um BeerDTO
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        // adiciona a quantidade
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

        // quando passar um incremento específico pega a quantidade e retorna a beerDTO
        when(beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                // tipo de conteúdo
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                // espera um nome
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                // espera uma marca
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                // espera um tipo
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                // espera uma quantidade
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
    }

//    @Test
//    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(30)
//                .build();
//
//        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);
//
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .con(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
//    }

//    @Test
//    void whenPATCHIsCalledWithInvalidBeerIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(30)
//                .build();
//
//        when(beerService.increment(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(5)
//                .build();
//
//        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(beerService.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);
//
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
//                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
//                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
//                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
//    }
//
//    @Test
//    void whenPATCHIsCalledToDEcrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(60)
//                .build();
//
//        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
//        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(beerService.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);
//
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void whenPATCHIsCalledWithInvalidBeerIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(5)
//                .build();
//
//        when(beerService.decrement(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);
//        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO)))
//                .andExpect(status().isNotFound());
//    }
}
