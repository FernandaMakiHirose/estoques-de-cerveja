# Desenvolvimento de testes unitários para validar uma API REST de gerenciamento
Aprendi a testar, unitariamente, uma API REST para o gerenciamento de estoques de cerveja. Construí testes unitários para validar o nosso sistema de gerenciamento de estoques de cerveja desenvolvido em Spring Boot, e também apresentei os principais conceitos e vantagens de criar testes unitários com JUnit e Mockito. Além disso, desenvolvi funcionalidades da nossa API através da prática do TDD.

## Pré-requisitos
- Java 14
- Maven 3.6.3
- Spring Boot 

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
