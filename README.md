<h1><b>Card Management API</b></h1>

<h3>API para armazenamento seguro e consulta de números completos de cartão</h3>

<hr>

<h3>Tecnologias utilizadas</h3>
<ul>
<li>Java 17+</li>
<li>Spring Boot</li>
<li>Spring Security (JWT)</li>
<li>Spring Data JPA</li>
<li>MySQL</li>
<li>Flyway</li>
<li>Docker & Docker Compose</li>
</ul>

<hr>

<h3>Arquitetura</h3>
<span>O projeto segue uma arquitetura hexagonal</span>
<span>Essa escolha foi feita para garantir:</span>
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
<span>As migrations são executadas automaticamente ao subir a aplicação. Na migration V3__insert_user.sql um usuário padrão é criado login: admin - password: card$management </span>

<hr>

<h3>Rodando a aplicação com Docker</h3>
<h4>Para facilitar a execução da API e do banco de dados, utilizamos Docker Compose. Siga os passos abaixo</h4>
<ul>
  <li>1. O Docker Desktop precisa estar rodando para que os containers sejam iniciados corretamente</li>
  <li>2. Para facilitar a avaliação técnica, o arquivo application.properties foi versionado contendo credenciais de ambiente exclusivas para execução local via Docker Compose, sem qualquer valor sensível real.
Em um cenário de produção, essas informações seriam fornecidas via variáveis de ambiente</li>
  <li>3. Entrar na pasta do projeto e inicializar o container com o comando: <b>docker-compose up --build</b></li>
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

