package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

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
	
	public void deveSalvarMateriaComAlunosEProfessor(){
		Materia materia = prepararMateria();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		
		Date dn1;
		try {
			dn1 = (java.util.Date) formato.parse("14/03/1980");
		} catch (ParseException e) {
			dn1 = new Date();
			//e.printStackTrace();
		}
		materia.getAlunos().add(prepararAluno("Werlon Guilherme", "123.456.789-00", dn1, 36));
		materia.getAlunos().add(prepararAluno("Marcella Ferreira", "111.456.789-01", formato.parse("06/04/1984"), 32));
		materia.getAlunos().add(prepararAluno("Walder Filho", "222.456.789-02", formato.parse("28/05/1984"), 32));
		materia.getAlunos().add(prepararAluno("Sue Ellen", "333.456.789-03", formato.parse("04/07/1982"), 34));
		
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
	public void deveSalvarMateria(){
		
	}
	
	@Test 
	public void devePesquisarMateria(){
		
	}
	
	@Test 
	public void deveAlterarMateria(){
		
	}
	
	@Test 
	public void deveRemoverMateria(){
		
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
