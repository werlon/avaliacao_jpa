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

public class MateriaTest {
	
	
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
		professor.setNome("JOÃO "+(codigo==null?JPAUtilTest.CODIGO_PADRAO:codigo));
		professor.setRegistro(registro==null?JPAUtilTest.REGISTRO_PADRAO:registro);
		
		Materia materia = new Materia();
		materia.setCodigo(codigo==null?JPAUtilTest.CODIGO_PADRAO:codigo);
		materia.setProfessor(professor);
		materia.setTitulo("Materia "+(registro==null?JPAUtilTest.REGISTRO_PADRAO:registro));
		
		return materia;
	}
	
	@Test
	public void deveSalvarMateriaComAlunosEProfessorPersistenciaCascata(){
		Materia materia = prepararMateria();	
		
		materia.getAlunos().add(prepararAluno("Werlon Guilherme", "123.456.789-00", JPAUtilTest.getTipoDate("14/03/1990"), 26));
		materia.getAlunos().add(prepararAluno("Werlon Borges", "111.456.789-01", JPAUtilTest.getTipoDate("06/04/1984"), 32));
		materia.getAlunos().add(prepararAluno("Werlon Silva", "222.456.789-02", JPAUtilTest.getTipoDate("28/05/1984"), 32));
		materia.getAlunos().add(prepararAluno("Werlon Werlon", "333.456.789-03", JPAUtilTest.getTipoDate("04/07/1982"), 34));
		materia.getAlunos().add(prepararAluno("Werlon Padrao", JPAUtilTest.CPF_PADRAO, JPAUtilTest.getTipoDate("14/03/1980"), 36));
		
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
		Materia materia = prepararMateria(JPAUtilTest.REGISTRO_PADRAO, "C0003B");
		
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
		query.setParameter("registro", JPAUtilTest.REGISTRO_PADRAO);
		
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
	public void deveAlterarMateria(){
		deveSalvarMateriaComAlunosEProfessorPersistenciaCascata();
		
		TypedQuery<Materia> query = em.createQuery(" SELECT m FROM Materia m", Materia.class).setMaxResults(1);
		
		Materia materia = query.getSingleResult();
		
		assertNotNull("Dever ter encontrado uma materia",materia);
		
		Integer versao = materia.getVersion();
		
		em.getTransaction().begin();
		
		materia.setTitulo("Novo Titulo");
		materia = em.merge(materia);
		em.getTransaction().commit();
		
		assertNotEquals("Versão deve ser diferente",versao, materia.getVersion());
	}
	
	@Test 
	public void deveRemoverMateria(){
		deveSalvarMateriaComAlunosEProfessorPersistenciaCascata();
		
		TypedQuery<Long> query = em.createQuery(" SELECT MAX(m.id) FROM Materia m",Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		
		Materia materia = em.find(Materia.class, id);

		em.remove(materia);
		
		em.getTransaction().commit();
		
		Materia materiaRemovida = em.find(Materia.class, id);
		
		assertNull("Não deve achar a matéria", materiaRemovida);
	}
	
	@Test
	public void deveBuscarMateriaQuePossuiAlunoComCpfComecandoPorJpql(){
		
		deveSalvarMateriaComAlunosEProfessorPersistenciaCascata();
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(m.id) ");
		jpql.append(" FROM Materia m ");
		jpql.append(" INNER JOIN m.alunos a ");
		jpql.append(" WHERE a.cpf LIKE :cpf ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("cpf", "123%");
		
		Long qtdMatrias = (Long) query.getSingleResult();
		
		assertTrue("Quantidade de matérias deve ser maior que zero", qtdMatrias.intValue() > 0);
		
		query = em.createQuery(jpql.toString());
		query.setParameter("cpf", "555%");
		
		qtdMatrias = (Long) query.getSingleResult();
		
		assertFalse("Quantidade de matérias não deve ser maior que zero", qtdMatrias.intValue() > 0);
	}
	
	@Test
	public void deveBuscarMateriaPeloNomeCompletoDoAlunoEPorParteDoNomeDoProfessorJpql(){
		deveSalvarMateriaComAlunosEProfessorPersistenciaCascata();
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(m.id) ");
		jpql.append(" FROM Materia m ");
		jpql.append(" INNER JOIN m.professor p ");
		jpql.append(" INNER JOIN m.alunos a ");
		jpql.append(" WHERE a.nome = :nomea ");
		jpql.append(" AND p.nome LIKE :nomepro ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("nomea", "Werlon Guilherme");
		query.setParameter("nomepro", "JO%");
		
		Long qtdMatrias = (Long) query.getSingleResult();
		
		assertTrue("Quantidade de matérias deve ser maior que zero", qtdMatrias.intValue() > 0);
		
		query = em.createQuery(jpql.toString());
		query.setParameter("nomea", "Werlon");
		query.setParameter("nomepro", "A%");
		
		qtdMatrias = (Long) query.getSingleResult();
		
		assertFalse("Quantidade de matérias não deve ser maior que zero", qtdMatrias.intValue() > 0);
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
