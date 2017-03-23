package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.Date;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
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
	 * Testes para Materia
	 */
	
	/**
	 * Testes para Pagamento
	 */
	
	/**
	 * Testes para Professor
	 */
	
	/**
	 * Testtes para Aluno
	 */
	
	@Test
	public void criarRegistrosParaTeste(){
		int quantidade = 3;
		em.getTransaction().begin();
		
		for(int i = 0 ; i < quantidade ; i++){
			Professor professor = new Professor();
			professor.setNome("Pedro "+i);
			professor.setRegistro(JPAUtilTest.REGISTRO_PADRAO+i);
			
			Materia materia = new Materia();
			materia.setCodigo(JPAUtilTest.CODIGO_PADRAO+i);
			materia.setProfessor(professor);
			materia.setTitulo("Materia "+i);
			
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
				
				Pagamento pagamento = new Pagamento();
				pagamento.setValor(valor);
				pagamento.setDataHora(new Date());
				pagamento.setProfessor(professor);
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
