# CND Certidão Monitor - Frontend

Este é o frontend da aplicação CND Certidão Monitor.

## Como executar o ambiente de desenvolvimento

Para executar o ambiente de desenvolvimento completo (backend e frontend), siga estas etapas:

1. **Pré-requisitos:**
   - [Docker](https://docs.docker.com/get-docker/) e [Docker Compose](https://docs.docker.com/compose/install/) instalados.
   - [Node.js](https://nodejs.org/) (versão 18 ou superior) e [npm](https://www.npmjs.com/) instalados.

2. **Variáveis de Ambiente:**
   - Na raiz do projeto, crie um arquivo chamado `.env` e adicione as seguintes variáveis de ambiente, substituindo os valores pelos da sua configuração do PostgreSQL:
     ```
     SPRING_DATASOURCE_URL=jdbc:postgresql://bd-cnd:5432/seu_banco_de_dados
     SPRING_DATASOURCE_USERNAME=seu_usuario
     SPRING_DATASOURCE_PASSWORD=sua_senha
     POSTGRES_DB=seu_banco_de_dados
     POSTGRES_USER=seu_usuario
     POSTGRES_PASSWORD=sua_senha
     WATCHTOWER_HTTP_API_TOKEN=seu_token_aqui
     ```

3. **Iniciar o Backend:**
   - Na raiz do projeto (onde está o `docker-compose.yml`), execute o seguinte comando para construir e iniciar os contêineres do backend e do banco de dados:
     ```bash
     docker-compose up -d --build
     ```
   - O backend estará disponível em `http://localhost:8080`.

4. **Iniciar o Frontend:**
   - Navegue até a pasta `cnd-certidao-monitor`:
     ```bash
     cd cnd-certidao-monitor
     ```
   - Instale as dependências do frontend:
     ```bash
     npm install
     ```
   - Inicie o servidor de desenvolvimento do frontend:
     ```bash
     npm run dev
     ```
   - O frontend estará disponível em `http://localhost:5173`.

Agora você pode acessar a aplicação no seu navegador e o frontend se comunicará com o backend.
