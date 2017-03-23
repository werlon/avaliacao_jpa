package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
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
	@Test
	public void deveTerMaisDeCincoMateriasPelosProfessoresNaLista(){
		criarRegistrosParaTeste(10);

		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Professor.class,"p")
				.add(Restrictions.in("p.id", 1L,2L,3L,4L,5L,6L,7L,8L,9L,10L))
				.setProjection(Projections.property("p.nome"));
		
		Criteria criteria = createCriteria(Materia.class,"m")
				.createAlias("m.professor", "p")
				.add(Subqueries.propertyIn("p.nome", detachedCriteria));
		
		@SuppressWarnings("unchecked")
		List<Materia> materia = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		
		assertTrue("Verifica se teve pelomenos 1 vendas", materia.size() >= 1);
		
		materia.forEach(mat -> assertFalse(mat.getProfessor().isTransient()));
	}
	
	/**
	 * Testes para Pagamento
	 */
	
	/**
	 * Testes para Professor
	 */
	
	/**
	 * Testtes para Aluno
	 */
	
	private void criarRegistrosParaTeste(int quantidade){
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
