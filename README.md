# Desenvolvimento de testes unitários para validar uma API REST de gerenciamento
Aprendi a testar, unitariamente, uma API REST para o gerenciamento de estoques de cerveja. Construí testes unitários para validar o nosso sistema de gerenciamento de estoques de cerveja desenvolvido em Spring Boot, e também apresentei os principais conceitos e vantagens de criar testes unitários com JUnit e Mockito. Além disso, desenvolvi funcionalidades da nossa API através da prática do TDD.

## Requisitos
* Java 14 ou versões superiores
* Maven 3.6.3 ou versões superiores
* Spring Boot 
* IDE

## Licença
Distribuido sob a licença MIT License. Veja `LICENSE` para mais informações.

## Comandos
Para executar o projeto:
>mvn spring-boot:run 

Para executar os testes:
>mvn clean test

Abra o projeto no endereço:
>http://localhost:8080/api/v1/beers

## Padrão arquitetural REST
GET = listagem <br>
PUT = atualização completa <br>
PATCH = atualização parcial em dados <br>
POST = criação <br>
DELETE = exclusão <br>

![img](https://user-images.githubusercontent.com/72028645/130327252-04c173a7-7de5-4779-8199-54865e17feef.png)

## Pirâmides de testes
1- Testes unitários: valida uma única unidade de código <br>
2- Testes de integração: cria recursos a parte para executar o teste <br>
2- Testes de regressão: teste de de ponta a ponta, desde o início até o final <br>

## Nível 1 - Testes unitários
* Maior número de testes, menos custo e tempo
* Testes feito pelo próprio desenvolvedor
* Rápidos, com base em linhas de código
* Cobertura de vários cenários para as linhas
* Integração com outros códigos: através de mocks

## Principais frameworks
* JUnit 
* Mockito
* Hamcrest

## Tópicos abordados
* Apresentação conceitual sobre testes: a pirâmide dos tipos de testes, e também a importância de cada tipo de teste durante o ciclo de desenvolvimento.
* Foco nos testes unitários: mostrar o porque é importante o desenvolvimento destes tipos de testes como parte do ciclo de desenvolvimento de software.
* Principais frameworks para testes unitários em Java: JUnit, Mockito e Hamcrest. 
* Desenvolvimento de testes unitários para validação de funcionalides básicas: criação, listagem, consulta por nome e exclusão de cervejas.
* TDD: apresentação e exemplo prático em 2 funcionaliades importantes: incremento e decremento do número de cervejas no estoque.

## Links interessantes
* [SDKMan! para gerenciamento e instalação do Java e Maven](https://sdkman.io/)
* [Referência do Intellij IDEA Community, para download](https://www.jetbrains.com/idea/download)
* [Palheta de atalhos de comandos do Intellij](https://resources.jetbrains.com/storage/products/intellij-idea/docs/IntelliJIDEA_ReferenceCard.pdf)
* [Site oficial do Spring](https://spring.io/)
* [Site oficial JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
* [Site oficial Mockito](https://site.mockito.org/)
* [Site oficial Hamcrest](http://hamcrest.org/JavaHamcrest/)
* [Referências - testes em geral com o Spring Boot](https://www.baeldung.com/spring-boot-testing)
* [Referência para o padrão arquitetural REST](https://restfulapi.net/)
* [Referência pirâmide de testes - Martin Fowler](https://martinfowler.com/articles/practical-test-pyramid.html#TheImportanceOftestAutomation)
