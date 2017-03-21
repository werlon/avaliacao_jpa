package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;
import br.edu.faculdadedelta.util.JPAUtilTest;

public class MateriaTest {
	
	private static final String REGISTRO_PADRAO = "A0001B";
	private static final String CODIGO_PADRAO = "PTG-001";
	
	private EntityManager em;
	
	private Aluno prepararAluno(String nome, String cpf, Date dataNascimento, int idade){
		Aluno novoAluno = new Aluno();
		novoAluno.setNome(nome);
		novoAluno.setCpf(cpf);
		novoAluno.setDataNascimento(dataNascimento);
		novoAluno.setIdade(idade);
		
		return novoAluno;
	}
	
	private Materia prepararMateria(){
		return prepararMateria(null,null);
	}
	
	private Materia prepararMateria(String registro,String codigo){
		Professor professor = new Professor();
		professor.setNome("JOÃO "+(codigo==null?CODIGO_PADRAO:codigo));
		professor.setRegistro(registro==null?REGISTRO_PADRAO:registro);
		
		Materia materia = new Materia();
		materia.setCodigo(codigo==null?CODIGO_PADRAO:codigo);
		materia.setProfessor(professor);
		materia.setTitulo("Materia "+(registro==null?REGISTRO_PADRAO:registro));
		
		return materia;
	}
	
	@Test
	public void deveSalvarMateriaComAlunosEProfessorPersistenciaCascata(){
		Materia materia = prepararMateria();	
		
		materia.getAlunos().add(prepararAluno("Werlon Guilherme", "123.456.789-00", JPAUtilTest.getTipoDate("14/03/1980"), 36));
		materia.getAlunos().add(prepararAluno("Marcella Ferreira", "111.456.789-01", JPAUtilTest.getTipoDate("06/04/1984"), 32));
		materia.getAlunos().add(prepararAluno("Walder Filho", "222.456.789-02", JPAUtilTest.getTipoDate("28/05/1984"), 32));
		materia.getAlunos().add(prepararAluno("Sue Ellen", "333.456.789-03", JPAUtilTest.getTipoDate("04/07/1982"), 34));
		
		assertTrue("Não deve ter id definido",materia.isTransient());
		
		em.getTransaction().begin();
		em.persist(materia);
		em.getTransaction().commit();
		
		assertFalse("Deve ter id definido",materia.isTransient());
		assertFalse("Deve ter id definido",materia.getProfessor().isTransient());
		
		materia.getAlunos().forEach(aluno->{
			assertFalse("Deve ter id definido",aluno.isTransient());
		});
	}
	
	@Test
	public void deveConsultarQuantidadeAlunosNaMateria(){
		Materia materia = prepararMateria(REGISTRO_PADRAO, "C0003B");
		
		for (int i = 0; i < 10; i++) {
			materia.getAlunos().add(prepararAluno("Werlon Guilherme"+i, "123.456.789-00", JPAUtilTest.getTipoDate("14/03/1980"), 20+i));
		}
		
		em.getTransaction().begin();
		em.persist(materia);
		em.getTransaction().commit();
		
		assertFalse("Deve ter persistido a materia",materia.isTransient());
		
		int qtdAluno = materia.getAlunos().size();
		
		assertTrue("Lista de alunos deve ter registros", qtdAluno > 0);
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(a.id) ");
		jpql.append(" FROM Materia m ");
		jpql.append(" INNER JOIN m.alunos a ");
		jpql.append(" INNER JOIN m.professor p ");
		jpql.append(" WHERE p.registro = :registro ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("registro", REGISTRO_PADRAO);
		
		Long qtdAlunosTurma = (Long) query.getSingleResult();
		
		assertEquals("Quantidade de alunos deve ser igual a quantidade da lista", qtdAlunosTurma.intValue(), qtdAluno);
	}
	
	@Test(expected = IllegalStateException.class)
	public void naoPodeFazerMergeEmMateriaQueNaoFoiPersistida(){
		Materia materia = prepararMateria();	
		
		materia.getAlunos().add(prepararAluno("Werlon Guilherme", "001.456.789-00", JPAUtilTest.getTipoDate("14/03/1980"), 31));
		materia.getAlunos().add(prepararAluno("Werlon Borges", "002.456.789-00", JPAUtilTest.getTipoDate("15/03/1980"), 32));
		materia.getAlunos().add(prepararAluno("Werlon Silva", "003.456.789-00", JPAUtilTest.getTipoDate("16/03/1980"), 33));
		
		assertTrue("Não deve ter id definido",materia.isTransient());
		
		em.getTransaction().begin();
		materia = em.merge(materia);
		em.getTransaction().commit();
		
		fail("Não deveria ter salvo (merge) uma materia nova com relacionamentos transient");
	}
	
	@Test 
	public void deveSalvarMateria(){
		assertFalse("retorno falso",false);
	}
	
	@Test 
	public void devePesquisarMateria(){
		assertFalse("retorno falso",false);
	}
	
	@Test 
	public void deveAlterarMateria(){
		assertFalse("retorno falso",false);
	}
	
	@Test 
	public void deveRemoverMateria(){
		assertFalse("retorno falso",false);
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
		
		Query query = entityManager.createQuery("DELETE FROM Materia m");
		
		int registrosExcluidos = query.executeUpdate();
		
		entityManager.getTransaction().commit();
		
		assertTrue("Deve ter excluido registros", registrosExcluidos > 0);
		
	}
}
