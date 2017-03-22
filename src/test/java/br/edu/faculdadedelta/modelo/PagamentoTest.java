package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;
import br.edu.faculdadedelta.util.JPAUtilTest;

public class PagamentoTest {
	
	private EntityManager em;
	
	private Professor preparaProfessor(){
		return preparaProfessor(null);
	}
	
	private Professor preparaProfessor(String registro){
		Professor professor = new Professor();
		professor.setNome("Werlon "+(registro==null?JPAUtilTest.REGISTRO_PADRAO:registro));
		professor.setRegistro(registro==null?JPAUtilTest.REGISTRO_PADRAO:registro);
		
		return professor;
	}
	
	@Test 
	public void deveSalvarPagamento(){
		Pagamento pagamento = new Pagamento();
		
		pagamento.setValor(150.00);
		pagamento.setDataHora(new Date());
		pagamento.setProfessor(preparaProfessor());
		
		assertTrue("Não deve ter id definido",pagamento.isTransient());
		
		em.getTransaction().begin();
		em.persist(pagamento);
		em.getTransaction().commit();
		
		assertFalse("Deve ter id definido",pagamento.isTransient());
		assertFalse("Deve ter id definido",pagamento.getProfessor().isTransient());
		
	}
	
	@Test 
	public void devePesquisarSeProfessorRecebeuPagamento(){
		deveSalvarPagamento();
		
		TypedQuery<Pagamento> query = em.createQuery(" SELECT p FROM Pagamento p INNER JOIN p.professor pr WHERE pr.registro = :registro", Pagamento.class).setMaxResults(1);
		query.setParameter("registro", JPAUtilTest.REGISTRO_PADRAO);
		
		Pagamento pagamento = query.getSingleResult();
		
		assertNotNull("Dever ter encontrado um pagamento",pagamento);
		
		
	}
	
	@Test 
	public void deveAlterarPagamento(){
		deveSalvarPagamento();
		
		TypedQuery<Pagamento> query = em.createQuery(" SELECT p FROM Pagamento p", Pagamento.class).setMaxResults(1);
		
		Pagamento pagamento = query.getSingleResult();
		
		assertNotNull("Dever ter encontrado um pagamento",pagamento);
		
		Integer versao = pagamento.getVersion();
		
		em.getTransaction().begin();
		
		pagamento.setValor(300.00);
		pagamento = em.merge(pagamento);
		em.getTransaction().commit();
		
		assertNotEquals("Versão deve ser diferente",versao, pagamento.getVersion());
	}
	
	@Test 
	public void deveRemoverUltimoPagamento(){
		deveSalvarPagamento();
		
		TypedQuery<Long> query = em.createQuery(" SELECT MAX(p.id) FROM Pagamento p",Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		
		Pagamento pagamento = em.find(Pagamento.class, id);

		em.remove(pagamento);
		
		em.getTransaction().commit();
		
		Pagamento pagamentoRemovido = em.find(Pagamento.class, id);
		
		assertNull("Não deve achar o pagamento", pagamentoRemovido);
	}
	
	@Test
	public void deveConsultarPagamentoPorNomeProfessorJpql(){
		
		for (int i = 0; i < 10; i++) {
			Pagamento pagamento = new Pagamento();
			
			pagamento.setValor(150.00+i);
			pagamento.setDataHora(JPAUtilTest.getTipoDateTime("01/0"+i+"/2017 08:00:00"));
			pagamento.setProfessor(preparaProfessor("PTB000"+i));
			
			assertTrue("Não deve ter id definido",pagamento.isTransient());
			
			em.getTransaction().begin();
			em.persist(pagamento);
			em.getTransaction().commit();
		}
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(pg.id) ");
		jpql.append(" FROM Pagamento pg ");
		jpql.append(" INNER JOIN pg.professor p ");
		jpql.append(" WHERE p.nome LIKE :nome ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("nome", "Werlon%");
		
		Long qtdRegistros = (Long) query.getSingleResult();
		
		assertTrue("Quantidade de pagamentos deve ser igual a quantidade da lista", qtdRegistros.intValue() >= 10 );
	}
	
	
	@Test
	public void deveConsultarPagamentoPorPeriodoJpql(){
		
		for (int i = 0; i < 10; i++) {
			Pagamento pagamento = new Pagamento();
			
			pagamento.setValor(150.00+i);
			pagamento.setDataHora(JPAUtilTest.getTipoDateTime("01/0"+i+"/2017 08:00:00"));
			pagamento.setProfessor(preparaProfessor("PTB000"+i));
			
			assertTrue("Não deve ter id definido",pagamento.isTransient());
			
			em.getTransaction().begin();
			em.persist(pagamento);
			em.getTransaction().commit();
		}
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(pg.id) ");
		jpql.append(" FROM Pagamento pg ");
		jpql.append(" WHERE pg.dataHora >= :dataInicial ");
		jpql.append(" AND pg.dataHora <= :dataFinal ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("dataInicial", JPAUtilTest.getTipoDateTime("10/11/2016 00:00:00"));
		query.setParameter("dataFinal", JPAUtilTest.getTipoDateTime("10/03/2017 00:00:00"));
		
		Long qtdRegistros = (Long) query.getSingleResult();
		
		assertFalse("Quantidade de pagamentos deve ser igual a quantidade da lista", qtdRegistros.intValue() < 5 );
		assertEquals("Quantidade de pagamentos deve ser igual a quantidade da lista", qtdRegistros.intValue() , 5 );
	}
	
	@Test
	public void deveConsultarPagamentoPorMateria(){
		deveSalvarPagamento();
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
		
		Query query = entityManager.createQuery("DELETE FROM Pagamento p");
		
		int registrosExcluidos = query.executeUpdate();
		
		entityManager.getTransaction().commit();
		
		assertTrue("Deve ter excluido registros", registrosExcluidos > 0);
		
	}
}
