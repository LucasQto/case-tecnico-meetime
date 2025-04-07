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


## 📌 Etapa 3: Criação de Contatos

### ✅ Descrição

Foi implementado o endpoint responsável por processar o callback enviado pelo HubSpot, contendo o código de autorização. Esse código permite que a aplicação realize a troca por um token de acesso, essencial para realizar futuras requisições autenticadas aos recursos da API do HubSpot. Essa etapa marca o início efetivo do fluxo OAuth 2.0, possibilitando que o usuário conceda permissões ao aplicativo.

### 📥 Endpoint

POST /crm/contacts

### 💭 Estratégia para Implementação

Nesta etapa, foi desenvolvido o endpoint que serve como ponte de integração para criação de contatos com o HubSpot. Busquei aplicar uma melhor organização de camadas e responsabilidades no código, aproveitando para refinar e criar novos módulos.

Implementei filtros para validar o token enviado pelo usuário. A lógica é simples: se o token estiver presente, a requisição é liberada para seguir. Questões como validade ou escopo incorreto são tratadas diretamente pela resposta do HubSpot. Como a aplicação não gerencia autenticação ou sessão, essa abordagem leve foi suficiente para os objetivos do projeto.

Além disso, desenvolvi a lógica de criação de contatos com um controle manual de rate limit com backoff exponencial, seguindo as orientações da documentação oficial do HubSpot. Optei por não utilizar bibliotecas externas, implementando a lógica manualmente.

Durante essa entrega, houve um avanço considerável na estrutura da API, incluindo:

Criação de camadas de segurança via filtros;

Uma camada centralizada para requisições;

Novos modelos de dados;

Exceptions personalizadas com mensagens retornadas no corpo da resposta, como esperado de um proxy.

Observação: A verificação dos campos obrigatórios no corpo da requisição não é rígida, pois o HubSpot aceita payloads com apenas o campo email, por exemplo.

Payload esperado: 

~~~json
  {
    "email": "dfgdfdf34@gmail.com",
    "firstname": "Lucas",
    "lastname": "Quinto"
  }
~~~

A resposta do endpoint caso ocorra tudo bem é:

201
Contact created successfully

## 📌 Etapa 4: Recebimento de Webhook para Criação de Contatos

### ✅ Descrição

Foi implementado um endpoint webhook responsável por receber e processar eventos de criação de contatos enviados pelo HubSpot.

### 📥 Endpoint

POST /hubspot/webhook

### 💭 Estratégia para Implementação

#### Foi necessário adicionar as dependencias do H2 Database e JPA do spring nessa etapa.

Nesta etapa, foi criado um endpoint que escuta eventos do tipo "contact.creation". Para isso, optei por utilizar um DTO para receber os dados enviados na requisição e uma Entity para persistir essas informações no banco de dados. A conversão entre o DTO e a Entity é feita por meio de um método de fábrica na própria Entity.

Decidi armazenar os eventos recebidos no banco de dados como uma forma de garantir que nenhuma informação seja perdida e para possibilitar um eventual reprocessamento, se necessário. Cogitei utilizar o MongoDB, mas considerei que seria um exagero (overengineering) para o escopo deste caso técnico. O H2 Database atendeu bem à necessidade, por ser leve e fácil de configurar.

Cada evento recebido é armazenado na tabela CONTACT_EVENT, utilizando como chave primária o eventId enviado pelo HubSpot. Além disso, foram adicionados alguns logs de debug para facilitar a visualização do fluxo da aplicação, e foi feito um pequeno refinamento na estrutura do projeto.

Também foi necessário criar uma configuração adicional de filtro, pois a documentação do HubSpot deixa claro que é enviado o cabeçalho X-HubSpot-Signature para validar que a requisição realmente vem do HubSpot. Assim, precisei implementar uma validação mais elaborada, já que era necessário ler o corpo da requisição para comparar a assinatura enviada com o client secret e verificar se ela era de fato válida.

Dessa forma, o webhook está protegido e apenas aceita requisições legítimas provenientes do HubSpot.

Request esperado: 

~~~json
  -H Content-type: application/json,
  -H X-HubSpot-Signature fsfs2423543sdgdfg...

  {
    "appId": 10468552,
    "eventId": 100,
    "subscriptionId": 3422102,
    "portalId": 49638027,
    "occurredAt": 1743992783090,
    "subscriptionType": "contact.creation",
    "attemptNumber": 0,
    "objectId": 123,
    "changeSource": "CRM",
    "changeFlag": "NEW"
  }
~~~