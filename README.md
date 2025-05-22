# MC656 - Engenharia de Software

O software a ser desenvolvido é uma plataforma de mobilidade urbana que conecta pessoas 
com veículos não motorizados ociosos a pessoas que gostariam de utilizá-los. Essa plataforma tem 
como objetivo principal facilitar a locomoção de membros da comunidade acadêmica, pensando no 
escopo de adoção da prefeitura universitária e população em geral no caso de adoção da prefeitura 
de Campinas além do direito à cidade conforme previsto no artigo 2º, incisos I e II, do Estatuto da 
Cidade (Lei nº 10.257/2001), que reconhece o direito de todos os cidadãos a cidades democráticas, 
justas e ambientalmente sustentáveis. Os principais recursos incluem a retirada e devolução desses 
equipamentos, o cadastro de dados pessoais, verificação de documentos e pagamento. Além disso, 
haverá no app mapa da estação de devolução mais próxima do usuário, pesquisa com filtro por 
localização, avaliação de veículo e pessoal. Também são esperados recursos que incentivam e 
auxiliam o usuário a participar da plataforma, como por exemplo um sistema de avaliações (por parte 
de quem está emprestando um veículo e por parte de quem está usufruindo) e engajamento, através 
de conquistas (modelo de gamificação).

# Integrantes:

- Antonio De Cesare Del Nero (220277)
- Thiago do Carmo Rodrigues Pinto (237827)
- José Carlos Cieni Júnior (170859)
- Barbara Nascimento (167109)
- Gabriel Augusto Ribeiro Ferreira (171851)
- Gustavo Oliveira de Sousa (247149)

## Arquitetura da Solução

### Estilo Arquitetural Adotado

Adotamos a arquitetura **MVC (Model-View-Controller)** com API **RESTful**, separando de maneira clara a camada de controle (Controllers), regras de negócio (Services) e persistência (Repositories). Essa arquitetura facilita integrações externas (mapas, pagamentos, validação de documentos) e permite escalar ou adaptar o backend para múltiplas interfaces clientes (Web, Mobile).

## Principais Funcionalidades

- **Cadastro de usuários** com verificação de documentos
- **Retirada e devolução de veículos** por meio de estações inteligentes
- **Avaliação de veículos e usuários** para manter a confiança na plataforma
- **Pagamentos e taxas** para operações de locação
- **Sistema de feedbacks** e reporte de uso indevido


### Principais Componentes e Responsabilidades

- **API Controller:** Recebe requisições HTTP dos clientes e direciona para a camada de serviço apropriada. Realiza validações iniciais dos dados de entrada e retorna as respostas.
- **Service Layer:** Implementa as regras de negócio, faz a orquestração entre controladores, repositórios, integrações externas e módulos específicos como validação de documentos.
- **Repository Layer:** Responsável pelo acesso e persistência de dados, abstraindo a comunicação com o banco de dados e mantendo o sistema desacoplado do armazenamento específico.
- **Entities/Domain Model:** Representa os principais conceitos do sistema (Usuário, Veículo, Estação, Avaliação, Locação etc.), servindo de base para as operações de negócio.
- **Security/Authentication:** Gerencia autenticação, autorização dos usuários e garante a aplicação das normas de segurança e proteção de dados (LGPD).
- **Document Validation Module:** Implementa a lógica de validação de diferentes tipos de documentos apresentados pelos usuários durante o cadastro.
- **Feedback/Evaluation Module:** Permite o registro e gerenciamento de avaliações e feedbacks sobre veículos e usuários, promovendo confiança na plataforma.
- **External Integrations:** Facilita a integração com APIs externas, como mapas (Google/Apple Maps), pagamentos e validação de documentos.

---
### Padrão de Projeto Adotado

No módulo de **Validação de Documentos**, adotamos o padrão **Strategy**, que permite definir múltiplas estratégias de validação para diferentes tipos de documentos (ex: RG, CNH), facilitando a manutenção e expansão da plataforma.

**Como funciona:**  
Cada tipo de documento possui uma implementação específica da interface `DocumentValidationStrategy`, responsável por validar os dados segundo as regras do documento. O serviço de cadastro seleciona a estratégia adequada dinamicamente, promovendo flexibilidade.

### Diagrama de Componentes (C4 - Nível 3)

```mermaid
graph TD
  subgraph Backend
    Controller[API Controller]
    Service[Service Layer]
    Repository[Repository Layer]
    Domain[Entities/Domain Model]
    Security[Security/Authentication]
    DocumentValidation[Document Validation Module]
    Feedback[Feedback/Evaluation Module]
  end

  Client[Mobile/Web App] -->|HTTP/REST| Controller
  Controller --> Service
  Service --> Repository
  Service --> Domain
  Service --> DocumentValidation
  Service --> Feedback
  Service --> Security
  Service --> ExtMaps[External Maps API]
  Service --> ExtPayment[External Payment API]
  Repository --> Database[(Database)]


