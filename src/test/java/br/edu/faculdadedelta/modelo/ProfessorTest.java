package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
		assertFalse("Retorna false",false);
	}
	
	@Test 
	public void deveAlterarProfessor(){
		assertFalse("Retorna false",false);
	}
	
	@Test 
	public void deveRemoverProfessor(){
		assertFalse("Retorna false",false);
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
