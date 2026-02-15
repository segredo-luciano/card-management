<h1><b>Card Management API</b></h1>

<p align="right">
  <a href="#-english-version">🇺🇸 English</a> |
  <a href="#-versão-em-português">🇧🇷 Português</a>
</p>

<hr>

<h2 id="-english-version">🇺🇸 English Version</h2>

<h3>API for secure storage and retrieval of full card numbers</h3>

<hr>

<h3>Important Notes!!!</h3>
<ul>
  <li>All endpoints require a Bearer Token. You can obtain it through: <b>POST /auth/login</b></li>
  <li>To make the authentication flow more realistic, one of the migrations creates a <b>User</b> table and inserts a default record used to validate the login</li>
  <li>Default user:
    <br>
    <b>
    login: admin
    <br>
    password: card$management
    </b>
    <br>
    This is the only valid user for authentication
  </li>
  <li>For technical evaluation purposes, the <b>application.properties</b> file was versioned with database credentials.
  <br>
  In a real production environment, these values should be provided through environment variables</li>
</ul>

<hr>

<h3>Running the application with Docker</h3>
<h4>To simplify the execution of the API and the database, Docker Compose was used. Follow the steps below:</h4>

<ul>
  <li>1. Docker Desktop must be running so the containers can start correctly</li>
  <li>2. Go to the project root folder and start the container:
    <pre><code>docker compose up -d</code></pre>
  </li>
  <li>3. Build the project:</li>
</ul>

Mac / Linux:
<pre><code>./mvnw clean package</code></pre>

Windows:
<pre><code>mvnw clean package</code></pre>

<ul>
  <li>4. You can run the application directly from an IDE (Eclipse, IntelliJ) or using:
    <pre><code>mvn spring-boot:run</code></pre>
    if Maven is installed globally
  </li>
</ul>

<h5>Available endpoints:</h5>
<ul>
  <li>POST /auth/login</li>
  <li>GET /cards?cardNumber=</li>
  <li>POST /cards</li>
  <li>POST /cards/batch</li>
</ul>

<hr>

<h3>Technologies</h3>
<ul>
<li>Java 17+</li>
<li>Apache Maven 3.9.9</li>
<li>Spring Boot</li>
<li>Spring Security (JWT)</li>
<li>Spring Data JPA</li>
<li>MySQL</li>
<li>Flyway</li>
<li>Docker & Docker Compose</li>
</ul>

<hr>

<h3>Architecture</h3>
<span>The project follows a hexagonal architecture.</span>
<span>This choice ensures:</span>
<ul>
  <li>Clear separation of responsibilities between domain, use cases, input adapters (Controllers) and output adapters</li>
  <li>Better maintainability by isolating business rules from infrastructure details</li>
  <li>Flexibility for future changes with minimal impact on the application</li>
</ul>

<hr>

<h3>Security and Cryptography</h3>
<ul>
  <li>JWT was chosen because it is stateless, allowing horizontal scalability without session storage</li>
  <li>To obtain the token: POST /auth/login</li>
  <li><b>When the application starts, Flyway creates a default user:
    <br>
    login: admin
    <br>
    password: card$management</b></li>
  <li>All other routes require a Bearer token</li>
  <li>Data at rest:
    <ul>
      <li>AES encryption applied to card numbers before storing</li>
      <li>SHA-256 hash for fast existence checks</li>
      <li>The card number is never stored in plain text</li>
    </ul>
  </li>
  <li>Data in transit:
    <ul>
      <li>The client side was not implemented, so end-to-end encryption is not complete.
      A real scenario would require key exchange or asymmetric cryptography involving the frontend or another client service</li>
      <li>In production this can be achieved using a Load Balancer or SSL configuration in Spring Boot</li>
    </ul>
  </li>
</ul>

<hr>

<h3>Scalability</h3>
<span>The application was designed to handle large data volumes:</span>
<ul>
  <li>Batch file processing with configurable chunk size</li>
  <li>Buffered file reading</li>
  <li>Indexed hash-based search</li>
  <li>High-performance duplicate handling</li>
  <li>Batch persistence</li>
</ul>

<hr>

<h3>Flyway Migrations</h3>
<span>Database versioning is managed with Flyway</span>
<br>
<span>Scripts location: src/main/resources/db/migration</span>
<br>
<span>Migrations run automatically when the application starts. In <b>V3__insert_user.sql</b> a default user is created:
<br>
<b>login: admin
<br>
password: card$management</b>
</span>

<hr>

<h2 id="-versão-em-português">🇧🇷 Versão em Português</h2>

<h3>API para armazenamento seguro e consulta de números completos de cartão</h3>

<hr>

<h3>Pontos Importantes!!!</h3>
<ul>
  <li>Os endpoints exigem Bearer Token para serem executados, podendo ser adquirido através desse endpoint: POST /auth/login</li>
  <li>Para tornar o processo de autenticação um pouco mais real, uma das migrations cria uma tabela User e insere um registro nela que é usada para verificar se o usuário que está fazendo login realmente existe na base</li>
  <li>O usuário criado é:
  <br>
    <b>
    login: admin 
    <br>
    password: card$management</b>
    <br>
    Sendo este o único usuário que funciona para autenticação
  </li>
  <li>Para facilitar a avaliação técnica, o arquivo application.properties foi versionado contendo credenciais de ambiente e acessos do banco de dados.
Em um cenário de produção, essas informações seriam fornecidas via variáveis de ambiente</li>
</ul>

<hr>

<h3>Rodando a aplicação com Docker</h3>
<h4>Para facilitar a execução da API e do banco de dados, utilizamos Docker Compose. Siga os passos abaixo</h4>
<ul>
  <li>1. O Docker Desktop precisa estar rodando para que os containers sejam iniciados corretamente</li>  
  <li>2. Entrar na pasta do projeto e inicializar o container com o comando: <b>docker compose up -d</b></li>
  <li>3. Buildar o projeto:</li>

    Mac / Linux:
    ./mvnw clean package
    
    Windows:
    mvnw clean package
  <li>4. É possível rodar a aplicação direto na IDE, por exemplo: Eclipse, IntelliJ ou com o comando: 
    <pre><code>mvn spring-boot:run</code></pre>
 caso o maven esteja instalado globalmente</li>
</ul>
<span>
  <h5>Endpoints disponibilizados:</h5>
  <ul>
    <li>POST /auth/login</li>
    <li>GET /cards?cardNumber=</li>
    <li>POST /cards</li>
    <li>POST /cards/batch</li>
  </ul>
</span>

<hr>

<h3>Tecnologias utilizadas</h3>
<ul>
<li>Java 17+</li>
<li>Apache Maven 3.9.9</li>
<li>Spring Boot</li>
<li>Spring Security (JWT)</li>
<li>Spring Data JPA</li>
<li>MySQL</li>
<li>Flyway</li>
<li>Docker & Docker Compose</li>
</ul>

<hr>

<h3>Arquitetura</h3>
<span>O projeto segue uma arquitetura hexagonal.</span>
<span> Essa escolha foi feita para garantir:</span>
<ul>
  <li>Separação clara de responsabilidades entre domínio, casos de uso, adaptadores de entrada (Controller) e adaptadores de saída (Adapters)</li>
  <li>Maior facilidade de manutenção, isolando regras de negócio e detalhes de infraestrutura</li>
  <li>Flexibilidade para mudanças futuras sem impactar tanto na aplicação</li>
</ul>

<hr>

<h3>Segurança e criptografia</h3>
<ul>
  <li>Foi utilizado JWT por ser um mecanismo stateless, permitindo escalabilidade horizontal da aplicação sem necessidade de armazenamento de sessão</li>
  <li>Para obter o token JWT: POST /auth/login</li>
  <li><b>Ao rodar o projeto Spring, é criado um login padrão a partir do Flyway: 
    <br>
    login: admin 
    <br>
    password: card$management</b></li>
  <li>As demais rotas exigem Bearer token para autenticação</li>  
  <li>Dados em repouso
    <ul>
      <li>Criptografia AES aplicada aos números de cartão antes de salvar no banco</li>
      <li>Hashes SHA-256 para verificação rápida de existência de cartões</li>
      <li>O número do cartão nunca é persistido em texto puro. Isso protege os dados em caso de: vazamento de base, acesso indevido, exposição de backups</li>
    </ul>
  </li>
  <li>Dados em trânsito</li>
    <ul>
      <li><b>A parte de cliente não foi implementada, então a end-to-end não é completa. Para end-to-end real, seria necessário um mecanismo de troca de chaves ou uso de criptografia assimétrica, o que envolve o frontend ou outro serviço cliente</b>
      </li>
      <li>Em ambiente produtivo isso pode ser feito por: Load Balancer ou Configuração SSL no próprio Spring Boot</li>
    </ul>
</ul>

<hr>

<h3>Escalabilidade</h3>
<span>A aplicação foi projetada considerando grandes volumes de dados:</span>
<ul>
  <li>Processamento de arquivos em lote com chunk configurável</li>
  <li>Leitura de arquivos com buffer</li>
  <li>Busca utilizando hash indexado</li>
  <li>Tratamento de duplicidade com alta performance</li>
  <li>Persistência em batch</li>
</ul>

<hr>

<h3>Migrations com Flyway</h3>
<span>O controle de versão do banco de dados é feito com Flyway</span>
<br>
<span>Os scripts ficam em: src/main/resources/db/migration</span>
<br>
<span>As migrations são executadas automaticamente ao subir a aplicação. Na migration V3__insert_user.sql um usuário padrão é criado 
<br>
  <b>login: admin 
  <br>
  password: card$management</b>  
</span>


