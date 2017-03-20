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
	
	private EntityManager em;
	
	@Test 
	public void deveSalvarProfessor(){
		
	}
	
	@Test 
	public void devePesquisarProfessor(){
		
	}
	
	@Test 
	public void deveAlterarProfessor(){
		
	}
	
	@Test 
	public void deveRemoverProfessor(){
		
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
