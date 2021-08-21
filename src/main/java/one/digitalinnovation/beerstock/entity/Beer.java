package one.digitalinnovation.beerstock.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import one.digitalinnovation.beerstock.enums.BeerType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data // do lombok, gera métodos que não precisam ser escritos
@Entity // descreve a entidade
@NoArgsConstructor
@AllArgsConstructor
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // esses campos sempre precisam ser preenchidos com algum valor
    // pode apenas ter um nome de cerveja
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String brand;

    // número máximo de cervejas que pode ter
    @Column(nullable = false)
    private int max;

    // vai adicionar a quantidade desde que ela esteja menor ou igual ao máximo da cerveja
    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BeerType type;


}
