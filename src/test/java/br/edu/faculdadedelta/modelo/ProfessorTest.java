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

public class ProfessorTest {
	
	private EntityManager em;
	
	@Test 
	public void deveSalvarProfessor(){
		
		Professor professor = new Professor();
		
		professor.setNome("Werlon Guilherme");
		
		professor.setRegistro(JPAUtilTest.REGISTRO_PADRAO);
		
		assertTrue("Não deve ter id definido",professor.isTransient());
		
		em.getTransaction().begin();
		
		em.persist(professor);
		
		em.getTransaction().commit();
		
		assertFalse("Deve ter definido",professor.isTransient());
		assertNotNull("Deve ter id definido",professor.getId());
	}
	
	@Test 
	public void devePesquisarProfessor(){
		for(int i = 0; i < 10; i++){
			deveSalvarProfessor();			
		}
		
		TypedQuery<Professor> query = em.createQuery(" SELECT p FROM Professor p", Professor.class);
		List<Professor> professor = query.getResultList();
		
		assertFalse("Deve ter professores na lista",professor.isEmpty());
		assertTrue("Deve ter professores na lista",professor.size() >= 10);
	}
	
	@Test 
	public void deveAlterarProfessor(){
		deveSalvarProfessor();
		
		TypedQuery<Professor> query = em.createQuery(" SELECT p FROM Professor p", Professor.class).setMaxResults(1);
		
		Professor professor = query.getSingleResult();
		
		assertNotNull("Dever ter encontrado um professor",professor);
		
		Integer versao = professor.getVersion();
		
		em.getTransaction().begin();
		
		professor.setRegistro("A0001C");
		professor = em.merge(professor);
		em.getTransaction().commit();
		
		assertNotEquals("Versão deve ser diferente",versao, professor.getVersion());
	}
	
	@Test 
	public void deveRemoverProfessor(){
		deveSalvarProfessor();
		
		TypedQuery<Long> query = em.createQuery(" SELECT MAX(p.id) FROM Professor p",Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		
		Professor professor = em.find(Professor.class, id);

		em.remove(professor);
		
		em.getTransaction().commit();
		
		Professor produtoExcluido = em.find(Professor.class, id);
		
		assertNull("Não deve achar o professor", produtoExcluido);
	}
	
	@Test
	public void deveBuscarProfessorPeloRegistroJpql(){
		deveSalvarProfessor();
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(p.id) ");
		jpql.append(" FROM Professor p ");
		jpql.append(" WHERE p.registro = :registro ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("registro", JPAUtilTest.REGISTRO_PADRAO);
		
		Long qtdRegistros = (Long) query.getSingleResult();
		
		assertTrue("Quantidade de professores deve ser maior que zero", qtdRegistros.intValue() > 0 );
		
		query = em.createQuery(jpql.toString());
		query.setParameter("registro", "ABC001");
		
		qtdRegistros = (Long) query.getSingleResult();
		
		assertFalse("Quantidade de professores não deve ser maior que zero", qtdRegistros.intValue() > 0 );
	}
	
	@Test
	public void deveBuscarProfessorPorNomeCompletoJpql(){
		deveSalvarProfessor();
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(p.id) ");
		jpql.append(" FROM Professor p ");
		jpql.append(" WHERE p.nome = :nome ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("nome", "Werlon Guilherme");
		
		Long qtdRegistros = (Long) query.getSingleResult();
		
		assertTrue("Quantidade de professores deve ser maior que zero", qtdRegistros.intValue() > 0 );
		
		query = em.createQuery(jpql.toString());
		query.setParameter("nome", "Guilherme");
		
		qtdRegistros = (Long) query.getSingleResult();
		
		assertFalse("Quantidade de professores não deve ser maior que zero", qtdRegistros.intValue() > 0 );
	}
	
	@Test
	public void deveBuscarProfessorPorParteDoNomeJpql(){
		deveSalvarProfessor();
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(p.id) ");
		jpql.append(" FROM Professor p ");
		jpql.append(" WHERE p.nome LIKE :nome ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("nome", "%Guilherme%");
		
		Long qtdRegistros = (Long) query.getSingleResult();
		
		assertTrue("Quantidade de professores deve ser maior que zero", qtdRegistros.intValue() > 0 );
		
		query = em.createQuery(jpql.toString());
		query.setParameter("nome", "%Guimaraes%");
		
		qtdRegistros = (Long) query.getSingleResult();
		
		assertFalse("Quantidade de professores não deve ser maior que zero", qtdRegistros.intValue() > 0 );
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
		
		Query query = entityManager.createQuery("DELETE FROM Professor p");
		
		int registrosExcluidos = query.executeUpdate();
		
		entityManager.getTransaction().commit();
		
		assertTrue("Deve ter excluido registros", registrosExcluidos > 0);
		
	}
}
