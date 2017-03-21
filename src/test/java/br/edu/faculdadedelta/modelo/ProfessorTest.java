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

public class ProfessorTest {
	
	private static final String REGISTRO_PADRAO = "A0001B";
	private EntityManager em;
	
	@Test 
	public void deveSalvarProfessor(){
		
		Professor professor = new Professor();
		
		professor.setNome("Atilla Barros");
		
		professor.setRegistro(REGISTRO_PADRAO);
		
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
		
		assertNotNull("Dever ter encontrado um aluno",professor);
		
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
