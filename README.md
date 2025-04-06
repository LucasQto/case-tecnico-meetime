# 🔗 Integração com HubSpot - Desafio Técnico

## 🎯 Objetivo

Este projeto implementa uma API REST em Java com Spring Boot para integração com a API do HubSpot, utilizando o fluxo OAuth 2.0 (Authorization Code Flow).

### Funcionalidades atuais

✅ Geração de URL para autenticação OAuth com o HubSpot  
⬜️ Recebimento do callback OAuth e troca do authorization code por access token  
⬜️ Criação de contatos no CRM HubSpot  
⬜️ Recebimento de notificações via webhook (ex: contact.creation)

---

## ⚙️ Tecnologias e Versões

| Ferramenta     | Versão   |
|----------------|----------|
| Java           | 21       |
| Spring Boot    | 3.4.4    |
| Maven          | 3.9.9    |
| Ngrok (testes) | Opcional |

---

## 💡 Considerações inicias

Antes de iniciar o projeto, me preocupei bastante em entender como funcionaria todo o fluxo da aplicação e como seria feita a entrega. Com entrega, quero dizer garantir a disponibilidade e a facilidade de execução da aplicação. Minha intenção é que ela seja dockerizada, permitindo fácil uso e configuração, inclusive no que diz respeito às variáveis de ambiente e outros recursos necessários.

Outro ponto que considerei desde o início foi a possível utilização de um banco de dados de cache, como o Redis. Essa ideia surgiu principalmente pensando na manipulação de tokens e também em um possível uso futuro no quarto requisito, relacionado ao recebimento de notificações.

Tendo isso em mente e com um caminho bem definido, decidi começar pelo básico: implementar o fluxo de requisitos de forma funcional e com qualidade. As melhorias e otimizações, como o uso de cache, ficarão para etapas mais avançadas do projeto.


## 🔖 Diagrama de sequencia (Simples) para mapeamento do fluxo
![Arquitetura](docs/images/arquitetura-hubspot-integration.png)

Criei esse diagrama em: https://sequencediagram.org/

## 📌 Etapa 1: Geração da Authorization URL

### ✅ Descrição

Implementado endpoint responsável por gerar e redirecionar o usuário para a URL de autorização do HubSpot. Essa etapa inicia o fluxo OAuth 2.0 e permite ao usuário conceder permissões ao aplicativo.

### 📥 Endpoint

GET /oauth/authorize-url

### 💭 Estratégia para Implementação

O desenvolvimento deste endpoint foi focado em atender ao primeiro requisito do desafio técnico: gerar a URL de autorização do HubSpot para dar início ao fluxo de autenticação OAuth 2.0. Levei em consideração algumas boas práticas de segurança, como:

Evitar exposição de chaves sensíveis, mantendo-as configuráveis via variáveis de ambiente, tanto para desenvolvimento quanto para produção.

Criação de um controller dedicado ao fluxo de OAuth, responsável por atender ao primeiro e segundo requisitos do desafio. Essa separação tem como objetivo garantir clareza no código e facilitar a manutenção futura.

Além disso, optei por criar um Record para representar o modelo de resposta da URL de autorização. Essa abordagem permite flexibilidade, facilitando a personalização da resposta, caso seja necessário.

No processamento do endpoint, utilizei o UriComponentsBuilder para a construção da URL. Essa ferramenta torna o código mais legível e facilita a adição de novos parâmetros, se necessário.

Antes de seguir para a próxima etapa, resolvi já aplicar a camada de serviço para delegar responsabilidades. Dessa forma tive um controller mais limpo e uma camada de serviço estruturada para trabalhar a segunda parte do desafio.

Exemplo de Resposta do Endpoint

~~~json
  {
    "authorizationUrl": "https://app.hubspot.com/oauth/authorize?client_id=xyz&redirect_uri=https://host/callback&scope=crm.objects.contacts.read"
  }
~~~


## 📌 Etapa 2: Processamento do Callback OAuth

### ✅ Descrição

Foi implementado o endpoint responsável por processar o callback enviado pelo HubSpot, contendo o código de autorização. Esse código permite que a aplicação realize a troca por um token de acesso, essencial para realizar futuras requisições autenticadas aos recursos da API do HubSpot. Essa etapa marca o início efetivo do fluxo OAuth 2.0, possibilitando que o usuário conceda permissões ao aplicativo.

### 📥 Endpoint

GET /oauth/callback?code={authorization_code}

### 💭 Estratégia para Implementação

Nesta etapa, foi desenvolvido o endpoint /oauth/callback, responsável por concluir o fluxo Authorization Code Flow. O HubSpot envia uma requisição para esse endpoint com um code via RequestParam. Com esse código, a aplicação realiza um POST para o endpoint de troca de token da API do HubSpot, obtendo o token de acesso.

Durante o desenvolvimento, foi considerada a utilização de um cache (como Redis) para armazenar o hub_id do usuário vinculado ao token, com o objetivo de facilitar o gerenciamento e reutilização futura. No entanto, essa abordagem exigiria que o cliente da aplicação enviasse o hub_id nas requisições futuras, o que poderia tornar o uso mais complexo. Por isso, optou-se por uma resposta mais simples contendo apenas o essencial: token_type e access_token, encapsulados em um DTO.

Além disso, nesta entrega, a estrutura do código foi refinada e organizada. Foi implementado um @ControllerAdvice para tratar erros provenientes de integrações externas (como falhas na comunicação com o HubSpot). Isso permite retornar erros padronizados e legíveis para o cliente, com informações úteis como status, mensagem e correlationId retornado pelo HubSpot.

A exceção é lançada diretamente no método de troca de token, mapeando o corpo da resposta de erro do HubSpot para uma classe customizada. Esse tratamento será aprimorado ainda mais nas próximas etapas.

Para a construção do corpo da requisição de troca de token (que exige o envio dos dados no formato application/x-www-form-urlencoded), foi utilizado o MultiValueMap fornecido pelo Spring Framework. Essa escolha se deu por dois motivos principais:

O formato form-data é o exigido pela API do HubSpot para esse endpoint específico;

O MultiValueMap facilita a construção e leitura do corpo da requisição de forma estruturada e compatível com o RestTemplate ou WebClient, permitindo adição de chaves e valores com clareza e segurança.

Exemplo de resposta do Endpoint

~~~json
  {
    "tokenType":"bearer","accessToken":"123456789-fdgdge..."
  }
~~~