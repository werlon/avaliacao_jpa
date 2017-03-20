package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class AlunoTest {
	
	private static final String CPF_PADRAO = "000.001.002-03";
	private EntityManager em;
	
	@Test 
	public void deveSalvarAluno(){
		Aluno aluno = new Aluno();
		aluno.setNome("Werlon Guilherme");
		aluno.setCpf(CPF_PADRAO);
		
		assertTrue("Não deve ter id definido",aluno.isTransient());
		
		em.getTransaction().begin();
		
		em.persist(aluno);
		
		em.getTransaction().commit();
		
		assertFalse("Deve ter definido",aluno.isTransient());
		assertNotNull("Deve ter id definido",aluno.getId());
	}
	
	@Test 
	public void devePesquisarAluno(){
		assertFalse("Retorna false",false);
	}
	
	@Test 
	public void deveAlterarAluno(){
		assertFalse("Retorna false",false);
	}
	
	@Test 
	public void deveRemoverAluno(){
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
		
		Query query = entityManager.createQuery("DELETE FROM Aluno a");
		
		int registrosExcluidos = query.executeUpdate();
		
		entityManager.getTransaction().commit();
		
		assertTrue("Deve ter excluido registros", registrosExcluidos > 0);
		
	}

}
