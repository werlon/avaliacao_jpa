package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;
import br.edu.faculdadedelta.util.JPAUtilTest;

public class RelatoriosCriteriaTest {
	
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
	
	/**
	 * Testes para Professor
	 */
	
	@Test
	public void buscarProfessoresPorRegistro(){
		criarProfessores(3);
		
		Criteria criteria = createCriteria(Professor.class,"p")
				.add(Restrictions.eq("p.registro", JPAUtilTest.REGISTRO_PADRAO+2))
				.setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List<Professor> professores = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		Professor pr = professores.get(0);
		
		assertFalse("Deve existir professor", pr.isTransient());
	}
	
	@Test
	public void buscaProfessoresPorParteDoNome(){
		criarProfessores(3);
		
		Criteria criteria = createCriteria(Professor.class)
				.add(Restrictions.eq("nome", "Pedro 2"))
				.setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List<Professor> professores = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertFalse("Deve existir professor", professores.get(0).isTransient());
	}
	
	@Test
	public void listarProfessorComMap(){
		criarProfessores(5);
		
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("id"))
				.add(Projections.property("nome"));

		Criteria criteria = createCriteria(Professor.class);
		criteria.setProjection(projectionList);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> professores = criteria
				.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
				.list();
		
		assertTrue("Verifica se teve pelomenos 3 produtos", professores.size() >= 5);
		
		professores.forEach(map -> {
			map.forEach((chave, valor) ->{
				assertTrue("primeiro deve ser string",chave instanceof String);
				assertTrue("segundo pode ser string ou long",valor instanceof String || valor instanceof Long );
			});
			
		});
	}
	
	/**
	 * Testes para Pagamento
	 */
	
	@Test
	public void buscarPagamentoPorValor(){
		em.getTransaction().begin();
		for(int i=0; i<3; i++){
			Professor professor = criarProfessor(i);
			Pagamento pagamento = criarPagamento(professor, i);
			em.persist(pagamento);
		}
		em.getTransaction().commit();
		
		Criteria criteria = createCriteria(Pagamento.class)
				.add(Restrictions.eq("valor", 300.00)).setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List<Pagamento> pagamentos = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		Pagamento pg = pagamentos.get(0);
		
		assertFalse("Deve ter pagamento", pg.isTransient());
	}
	
	@Test
	public void buscarPagamentoPorProfessor(){
		
		em.getTransaction().begin();
		for(int i=0; i<3; i++){
			Professor professor = criarProfessor(i);
			Pagamento pagamento = criarPagamento(professor, i);
			em.persist(pagamento);
		}
		em.getTransaction().commit();
		
		Criteria criteria = createCriteria(Pagamento.class,"pg")
				.createAlias("pg.professor","pr",JoinType.INNER_JOIN)
				.add(Restrictions.eq("pr.nome", "Pedro 1")).setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List<Pagamento> pagamentos = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertFalse("Deve ter pagamento", pagamentos.get(0).isTransient());
	}
	
	@Test
	public void buscarTodosOsPagamentosDeUmProfessor(){
		Professor professor = criarProfessor(1);
		
		em.getTransaction().begin();
		for(int i=0; i<3; i++){
			Pagamento pagamento = criarPagamento(professor, i);
			em.persist(pagamento);
		}
		em.getTransaction().commit();
		
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("p.id").as("id"))
				.add(Projections.property("p.valor").as("valor"));
		
		Criteria criteria = createCriteria(Pagamento.class, "p")
				.createAlias("p.professor", "pr",JoinType.INNER_JOIN)
				.setProjection(projectionList);
		
		@SuppressWarnings("unchecked")
		List<Object[]> pagamentos = criteria
				.setResultTransformer(Criteria.PROJECTION)
				.add(Restrictions.eq("pr.nome", "Pedro 1"))
				.list();
		
		assertTrue("Verifica se teve pelomenos 3 produtos", pagamentos.size() >= 1);
		
		pagamentos.forEach(pagamento -> {
			assertTrue("primeiro deve ser id Long",pagamento[0] instanceof Long);
			assertTrue("segundo deve ser Valor Double",pagamento[1] instanceof Double);
		});
	}
	
	/**
	 * Testes para Materia
	 */

	@Test
	public void deveVerificarSeAQuantidadeDeMateriasEMaiorQueOMaximoRegistro(){
		List<Aluno> alunos = new ArrayList<>();
		criarMaterias(criarProfessor(1), alunos, 3);
		
		Criteria criteria = createCriteria(Materia.class, "m")
				.setProjection(Projections.max("m.id"));
		
		Long maiorRegistro = (Long) criteria
				.setResultTransformer(Criteria.PROJECTION)
				.uniqueResult();
		
		assertTrue("Verifica se o maximo registro ficou maior ou igual a 3",maiorRegistro >= 3L);
	}
	
	@Test
	public void deveBuscarMateriaPeloProfessor(){
		List<Aluno> alunos = new ArrayList<>();
		Professor professor = criarProfessor(1);
		criarMaterias(professor, alunos, 3);
		
		Criteria criteria = createCriteria(Materia.class,"m")
				.createAlias("m.professor", "p", JoinType.INNER_JOIN)
				.add(Restrictions.like("p.nome","Pedro",MatchMode.ANYWHERE))
				.setProjection(Projections.rowCount());
		
		Long qtdRegistros = (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
		
		assertTrue("Verifica se teve pelomenos 3 matérias com professor", qtdRegistros >= 2);
	}
	
	@Test
	public void deveBuscarMateriaPeloAluno(){
		List<Aluno> alunos = new ArrayList<>();
		alunos.add(criarAluno(1));
		criarMaterias(criarProfessor(1), alunos, 4);
		
		Criteria criteria = createCriteria(Materia.class,"m")
				.createAlias("m.alunos", "a", JoinType.INNER_JOIN)
				.setFetchMode("alunos", FetchMode.JOIN)
				.add(Restrictions.ilike("a.nome","Werlon",MatchMode.ANYWHERE))
				.setProjection(Projections.rowCount());

		Long qtdRegistros = (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();

		assertEquals("Deve ter 1 registro", 4, qtdRegistros.intValue());
		assertTrue("Verifica se teve pelomenos 3 matérias com aluno", qtdRegistros >= 4);
	}
	
	/**
	 * Testtes para Aluno
	 */
	@Test
	public void deveExistirAlunoNaBusca(){
		criarAlunos(4);
		Criteria criteria = createCriteria(Aluno.class,"a")
				.add(Restrictions.eq("a.idade", 12))
				.setMaxResults(1);
		
		@SuppressWarnings("unchecked")
		List<Aluno> alunos = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		Aluno al = alunos.get(0);
		
		assertFalse("Deve existir aluno", al.isTransient());
	}
	
	@Test
	public void buscarAlunosRestringindoPorIdadeSubquery(){
		criarAlunos(10);
		
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Aluno.class,"a")
				.add(Restrictions.in("a.idade", 11,12,13,14,15,16,17,18,19,20))
				.setProjection(Projections.property("a.nome"));
		
		Criteria criteria = createCriteria(Aluno.class,"al")
				.add(Subqueries.propertyIn("al.nome", detachedCriteria));
		
		@SuppressWarnings("unchecked")
		List<Aluno> alunos = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("Verifica se teve pelomenos 1 aluno", alunos.size() >= 1);
		
		alunos.forEach(aluno -> assertFalse(aluno.isTransient()));
	}
	
	@Test
	public void buscarAlunosRestringindoPorDataNascimento(){
		criarAlunos(10);
		
		Criteria criteria = createCriteria(Aluno.class, "a")
				.add(Restrictions.between("a.idade", 10, 20))
				.setProjection(Projections.rowCount());

		Long qtdRegistros = (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
		
		assertTrue("Verifica se teve pelomenos 3 alunos", qtdRegistros >= 3);
	}
	
	private Aluno criarAluno(int i){
		int numero = 100 + i;
		int idade = 10+i;
		int ano = 1950+i;
		
		Aluno aluno = new Aluno();
		aluno.setNome("Werlon "+i);
		aluno.setCpf("001.002."+numero+"-00");
		aluno.setDataNascimento(JPAUtilTest.getTipoDate(ano+"/02/01"));
		aluno.setIdade(idade);
		return aluno;
	}
	
	private void criarAlunos(int quantidade){
		em.getTransaction().begin();
		for(int i = 0 ; i < quantidade ; i++){
			Aluno novoAluno = criarAluno(i);
			em.persist(novoAluno);
		}
		em.getTransaction().commit();
	}
	
	private Materia criarMateria(Professor professor, List<Aluno> alunos, int i){
		Materia materia = new Materia();
		materia.setCodigo(JPAUtilTest.CODIGO_PADRAO+i);
		materia.setProfessor(professor);
		materia.setTitulo("Materia "+i);
		materia.setAlunos(alunos);
		
		return materia;
	}
	
	private void criarMaterias(Professor professor, List<Aluno> alunos, int quantidade){
		em.getTransaction().begin();
		for(int i = 0 ; i < quantidade ; i++){
			Materia materia = criarMateria(professor, alunos, i);
			em.persist(materia);
		}
		em.getTransaction().commit();
	}
	
	private Professor criarProfessor(int i){
		Professor professor = new Professor();
		professor.setNome("Pedro "+i);
		professor.setRegistro(JPAUtilTest.REGISTRO_PADRAO+i);
		professor.setPagamentos(new ArrayList<>());
		return professor;
	}
	
	private void criarProfessores(int quantidade){
		em.getTransaction().begin();
		for(int i = 0 ; i < quantidade ; i++){
			Professor professor = criarProfessor(i);
			em.persist(professor);
		}
		em.getTransaction().commit();
	}
	
	private Pagamento criarPagamento(Professor professor, int i){
		double valor = 300.00 + i;
		Pagamento pagamento = new Pagamento();
		pagamento.setValor(valor);
		pagamento.setDataHora(new Date());
		pagamento.setProfessor(professor);
		return pagamento;
	}
	
	private void crarPagamentos(Professor professor, int quantidade){
		em.getTransaction().begin();
		for(int i = 0 ; i < quantidade ; i++){
			Pagamento pagamento = criarPagamento(professor, i);
			em.persist(pagamento);
		}
		em.getTransaction().commit();
	}
	
	private void criarRegistrosParaTeste(int quantidade){
		em.getTransaction().begin();
		
		for(int i = 0 ; i < quantidade ; i++){
			Professor professor = criarProfessor(i);
			
			Materia materia = criarMateria(professor, new ArrayList<>(), i); 
			
			for(int j = 0; j < quantidade; j++){
				int numero = 100 + i + j;
				int idade = 10+i+j;
				int ano = 1950+i+j;
				double valor = 300.00 + i + j;
				
				Aluno novoAluno = new Aluno();
				novoAluno.setNome("Werlon "+i+j);
				novoAluno.setCpf("001.002."+numero+"-00");
				novoAluno.setDataNascimento(JPAUtilTest.getTipoDate(ano+"/02/01"));
				novoAluno.setIdade(idade);
				
				materia.getAlunos().add(novoAluno);
				
				Pagamento pagamento = criarPagamento(professor, i);
				
				em.persist(pagamento);
			}
			
			assertTrue("Deve estar não persistido", materia.isTransient());

			em.persist(materia);
			
			assertFalse("Deve estar persistido", materia.isTransient());
			
		}
		em.getTransaction().commit();
		
	}
	
	@Before
	public void instanciarEntityManager(){
		em = JPAUtil.INSTANCE.getEntityManager();
	}
		
	@After
	public void fecharEntityManager(){
		if(em.isOpen()){
			em.close();
		}
	}

}
