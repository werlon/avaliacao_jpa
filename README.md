# Avaliacao JPA
## Trabalho de avaliação do módulo JPA pós graduação Fullstack Faculdade Delta 2017/2018

### 1- Qual a responsabilidade/objeto das anotações:

__@MappedSuperclass__
> * Anotação usada para mapear classes que serão herdadas por entidades
> * Define que essa classe foi mapeada como classe padrão para todas as outras
 
__@Version__
> * Anotação usada para mapear atributo de versionamento (Optimistic Locking)
> * Define qual a versão atual do registro (para versionamento interno)
 
__@Entity__
> * Usado para definir que a classe representa uma entidade no banco
 
__@Table__
> *  Usado para definir detalhes da tabela do BD que a entidade representará
> *  Usado Junto com a @Entity, onde passamos detalhes sobre a tabela a ser criada (exemplo: nome="nome_da_tabela")

__@Id__
> * Usado para mapear a chave primária da entidade
> * Pode ser usada em tipos primitivos, wrappers e classes
> * Faz uso do equals e hashCode
> * Usado para marcar na classe qual a coluna será usada como id (PK) na tabela.

__@GeneratedValue__
> * Usado para mapear um gerador de valores para a chave primária
> * strategy= GenerationType.AUTO, IDENTITY, SEQUENCE ou TABLE
 Acompanha a chave primaria @Id
 
__@Column__
> * Usada para detalhar a coluna que o atributo representa
> * Para informar que o atributo será usado como coluna podendo passar opções dessa coluna.
 
__@Basic__
> * Usada para especificar se a coluna é NULLou NOT NULL
> * Usada também para especificar o carregamento do atributo ao consultar (LAZY e EAGER)
> * Pode ser usado com os outros atributos @Column, @Temporal

__@Temporal__
 Usada para mapear campos Date, Time e DateTime
 Para definir colunas do tipo data(date), hora(time), datahora(datetime), timestamp
 
 
### 2- Qual a responsabilidade/objeto das anotações:

__@ManyToOne__
> * Usada para mapear um relacionamento de muitos-para-um
> * Pode-se especificar os atributos:
`<addr>`->fetch–diz se o relacionamento será ou não carregado após consulta da entidade
->cascade–diz se o relacionamento sofrerá as mesmas ações de persistência que a entidade
->option–diz se a respectiva coluna do relacionado é definida como NULLou NOT NULL
->targetEntity –especifica a classe da entidade em caso de mapear uma classe mais genérica

__@ManyToMany__
> * Usada para mapear um relacionamento de muitos-para-muitos
> * Os atributos da anotação se assemelham à anotação @OneToOne

__@OneToOne__
> * Usada para mapear um relacionamento de um-para-um
->mappedBy –usado para mapear um relacionamentos bi-direcionais; é usado no lado fracodo relacionamento; o atributo recebe uma Stringcom o nome do objeto no lado forte
->orphanRemoval –excluir ou não registros lixo após desfazer um relacionamento
 
__@JoinColumn__
> * Usada para especificar detalhes sobre a coluna da Foreign Key (Usado com outros acima)
> * @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.LAZY)
> * @JoinColumn(name="nome_coluna_local", referencedColumnName = "nome_coluna_referenciada", insertable = true, updatable = false, nullable = false) <atributos da coluna>

__@JoinTable__
> * Usada para detalhar a tabela de junção dos relacionamentos @*ToMany
> * Possui os atributos da anotação @Table Mais os atributos
->foreignKey–especifica o nome da constraint FK de um dos lados do relacionamento
->inverseForeignKey–nome da constraint FK do outro lado do relacionamento
->joinColumns–mapear detalhes da coluna da FKde um dos lados do relacionamento
->inverseJoinColumns–mapear detalhes da coluna da FKdo outro lado do relacionamento
> * @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST}, )
> * @JoinTable(nome = "nome_outra_tabela",
> * 		joinColumns = @JoinColumn(name="id_segunda_coluna_outra_tabela"),
> * 		inverseJoinColumns = @JoinColumn(name="id_primeira_coluna_outra_tabela_ligado_nesta"))

### 3- Qual a responsabilidade/objeto dos métodos do EntityManager:
__isOpen__
> * Verificar se o EntityManager foi iniciado
 
__close__
> * Fecha a instancia aberta do EntityManager
 
__createQuery__
> * Criará a consulta pelos parametros jpql
 
__find__
> * Busca os objetos que estejam no escopo do EntityManager
 
__merge__
> * Salva ou altera se existir. Depende do CascadeType.MERGE. se tiver só CascadeType.PERSIST e tiver colunas LAZY vai gerar erro
 
__persist__
> * Salva o objeto que está no escopo do EntityManager e todos os objetos relacionados a ele nas colunas de ligação.
 
__remove__
> * Remove um objeto inteiro do escopo do EntityManager
 
### 4- Como instânciar Criteria do Hibernate através do EntityManager?
Antes de instanciar, deve mudar a versão do hibernate, alterar a dependencia no pom.xml a versão não deve ser superior a 5.1, assim usamos 5.1.0.Final pois Criteria foi depreciado nas versões posteriores
Crie um metodo que retorna um Session do Hibernate, crie outro método que retorna  o proprio Criteria.
O Session é um Session do EntityManager conforme codigo de exemplo abaixo. A Session é uma fábrica para intancias de Criteria.
	
#### 4.1 Dê exemplo do código
	private EntityManager em;
	
	
	private Session getSession(){
		return (Session) em.getDelegate();
	}
	
	private Criteria createCriteria(Class<?> clazz){
		return getSession().createCriteria(clazz);
	}
	
	private Criteria createCriteria(Class<?> clazz, String alias){
		return getSession().createCriteria(clazz, alias);
	}
	
	//instanciando
	Criteria criteria = createCriteria(SuaClasse.class,"alias");


### 5- Como abrir uma transação?
Para abrir uma transação deve pegar a instancia ativa do EntityManager e chamar o método getTransation().begin();
#### 5.1 Dê exemplo do código

	private void criarProdutos(int quantidade){
		em.getTransaction().begin();
		
		for(int i = 0 ; i < quantidade ; i++){
			Produto produto = criarProduto("Notebook", "Dell");
			
			em.persist(produto);
		}
		
		em.getTransaction().commit();
	}

### 6- Como fechar uma transação?
Para fechar uma transação deve pegar a instancia ativa do EntityManager e chamar o método getTransation().commit() ou callback();
	Se chamar o commit() confirma as açoes solicitadas. se chamar calback() desfaz as ações solicitadas.
#### 6.1 Dê exemplo do código

	private void criarClientes(int quantidade){
		em.getTransaction().begin();
		
		for(int i = 0 ; i < quantidade ; i++){
			Cliente cliente = new Cliente();
			cliente.setNome("Werlon Guilherme");
			cliente.setCpf(CPF_PADRAO);
			
			em.persist(cliente);
		}
		
		em.getTransaction().commit();
	}

### 7- Como criar e executar uma query com JPQL?
	O JPQL facilita a criação das querys utilizando o metodo createQuery() do EntityManager sendo possível criar consultas informando o Objeto que se  quer o resultado
#### 7.1 Dê exemplo do código
	TypedQuery<Produto> query = em.createQuery(" SELECT p FROM Produto p", Produto.class).setMaxResults(1);
	Produto produto = query.getSingleResult();

### 8- Qual a responsabilidade dos valores FetchType.LAZY e FetchType.EAGER?
Nas tabelas que possuem relacionamentos os valores indicam se o relacionamento será ou não carregado após consulta da entidade.
	FetchType.LAZY só carrega o relacionamento quando for solicitado
	FetchType.EAGER sempre carrega o relacionamento

### 9- Qual a responsabilidade dos valores CascadeType.PERSIST e CascadeType.REMOVE?
Nas tabelas que possuem relacionamentos os valores indicam se o relacionamento sofrerá as mesmas ações de persistência que a entidade.
	CascadeType.PERSIST indica que quando persistir ou alterar o objeto principal os objetos relacionados devem ser persistidos também
	CascadeType.REMOVE indica que quando persistir,alterar ou remover o objeto principal os objetos relacionados devem ser removidos.

### 10- Como fazer uma operação BATCH (DELETE ou UPDATE) através do EntityManager?
Utilizado quando quero alterar ou excluir muitos registros em uma unica execução.
chamo o EntityManager crio a query com o método createQuery() e executo o metodo executeUpdate();
Exemplo:
	@AfterClass
	public static void deveLimparBase(){
		EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();
	
		entityManager.getTransaction().begin();
		
		Query query = entityManager.createQuery("DELETE FROM Produto p");
		
		int registrosExcluidos = query.executeUpdate();
		
		entityManager.getTransaction().commit();
		
		assertTrue("Deve ter excluido registros", registrosExcluidos > 0);
		
	}
	
### 11- Qual a explicação para a exception LazyInitializationException?
É uma Exception disparada sempre que se tenta buscar uma Entidade/Objeto que não está dentro do escopo do EntityManager.
