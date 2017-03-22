package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;
import br.edu.faculdadedelta.util.JPAUtilTest;

public class AlunoTest {
	
	private EntityManager em;
	
	@Test 
	public void deveSalvarAluno(){
		Aluno aluno = new Aluno();
		aluno.setNome("Werlon Guilherme");
		aluno.setCpf(JPAUtilTest.CPF_PADRAO);
		aluno.setIdade(36);
		aluno.setMatricula("MAT-000000001");
		
		assertTrue("Não deve ter id definido",aluno.isTransient());
		
		em.getTransaction().begin();
		
		em.persist(aluno);
		
		em.getTransaction().commit();
		
		assertFalse("Deve ter definido",aluno.isTransient());
		assertNotNull("Deve ter id definido",aluno.getId());
	}
	
	@Test 
	public void devePesquisarAluno(){
		for(int i = 0; i<10; i++){
			deveSalvarAluno();
		}
		
		TypedQuery<Aluno> query = em.createQuery(" SELECT a FROM Aluno a", Aluno.class);
		List<Aluno> aluno = query.getResultList();
		
		assertFalse("Deve ter alunos na lista",aluno.isEmpty());
		assertTrue("Deve ter alunos na lista",aluno.size() >= 10);
	}
	
	@Test 
	public void deveAlterarAluno(){
		deveSalvarAluno();
		
		TypedQuery<Aluno> query = em.createQuery(" SELECT a FROM Aluno a", Aluno.class).setMaxResults(1);
		
		Aluno aluno = query.getSingleResult();
		
		assertNotNull("Dever ter encontrado um aluno",aluno);
		
		Integer versao = aluno.getVersion();
		
		em.getTransaction().begin();
		
		aluno.setIdade(37);
		aluno = em.merge(aluno);
		em.getTransaction().commit();
		
		assertNotEquals("Versão deve ser diferente",versao, aluno.getVersion());
	}
	
	@Test 
	public void deveRemoverAluno(){
		deveSalvarAluno();
		
		TypedQuery<Long> query = em.createQuery(" SELECT MAX(a.id) FROM Aluno a",Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		
		Aluno aluno = em.find(Aluno.class, id);

		em.remove(aluno);
		
		em.getTransaction().commit();
		
		Aluno produtoExcluido = em.find(Aluno.class, id);
		
		assertNull("Não deve achar o aluno", produtoExcluido);
	}
	
	@Test
	public void deveBuscarAlunoPelaMatriculaJpql(){
		deveSalvarAluno();
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(a.id) ");
		jpql.append(" FROM Aluno a ");
		jpql.append(" WHERE a.matricula = :matricula ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("matricula", "MAT-000000001");
		
		Long qtdRegistros = (Long) query.getSingleResult();
		
		assertTrue("Quantidade de alunos deve ser maior que zero", qtdRegistros.intValue() > 0 );
	
	}
	
	@Test
	public void deveBuscarAlunosPelaIdadeJpql(){
		deveSalvarAluno();
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(a.id) ");
		jpql.append(" FROM Aluno a ");
		jpql.append(" WHERE a.idade >= :idade ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("idade", 30);
		
		Long qtdRegistros = (Long) query.getSingleResult();
		
		assertTrue("Quantidade de alunos deve ser maior que zero", qtdRegistros.intValue() > 0 );
		
		query = em.createQuery(jpql.toString());
		query.setParameter("idade", 60);
		
		qtdRegistros = (Long) query.getSingleResult();
		
		assertFalse("Quantidade de alunos não deve ser maior que zero", qtdRegistros.intValue() > 0 );
		
	}
	
	@Test
	public void deveBuscarPorParteDoNomeJpql(){
		deveSalvarAluno();
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(a.id) ");
		jpql.append(" FROM Aluno a ");
		jpql.append(" WHERE a.nome LIKE :nome ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("nome", "%Guilherme%");
		
		Long qtdRegistros = (Long) query.getSingleResult();
		
		assertTrue("Quantidade de alunos deve ser maior que zero", qtdRegistros.intValue() > 0 );
		
		query = em.createQuery(jpql.toString());
		query.setParameter("nome", "%Guimaraes%");
		
		qtdRegistros = (Long) query.getSingleResult();
		
		assertFalse("Quantidade de alunos não deve ser maior que zero", qtdRegistros.intValue() > 0 );
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
	
	@AfterClass
	public static void deveLimparBase(){
		EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();
	
		entityManager.getTransaction().begin();
		
		Query query = entityManager.createQuery("DELETE FROM Aluno a");
		
		int registrosExcluidos = query.executeUpdate();
		
		entityManager.getTransaction().commit();
		
		assertTrue("Deve ter excluido registros", registrosExcluidos > 0);
		
	}

}
