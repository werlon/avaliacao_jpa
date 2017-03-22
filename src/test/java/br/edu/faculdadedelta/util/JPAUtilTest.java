package br.edu.faculdadedelta.util;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JPAUtilTest {
	
	public static final String CPF_PADRAO = "000.001.002-03";
	public static final String REGISTRO_PADRAO = "A0001B";
	public static final String CODIGO_PADRAO = "PTG-001";

	private EntityManager em;
	
	public static String getCpfPadrao(){
		return CPF_PADRAO;
	}
	
	public static String getRegistroPadrao(){
		return REGISTRO_PADRAO;
	}
	
	public static String getCodigoPadrao(){
		return CODIGO_PADRAO;
	}
	
	
	
	/**
	 * Retorna uma data no formato Date. Espera uma string formato dd/MM/yyyy
	 * @param data
	 * @return
	 */
	public static Date getTipoDate(String data) {
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		
		Date ndt;
		try {
			ndt = (java.util.Date) formato.parse(data);
		} catch (ParseException e) {
			ndt = new Date();
			//e.printStackTrace();
		}
		return ndt;
	}
	
	/**
	 * Retorna uma data no formato Date. Espera uma string formato dd/MM/yyyy
	 * @param data
	 * @return
	 */
	public static Date getTipoDateTime(String dataHora) {
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		Date ndt;
		try {
			ndt = (java.util.Date) formato.parse(dataHora);
		} catch (ParseException e) {
			ndt = new Date();
			//e.printStackTrace();
		}
		return ndt;
	}
	
	@Test
	public void deveTerInstanciaDoEntityManager(){
		assertNotNull("Deve ter instanciado o entity manager",em);
	}
	
	@Test
	public void deveFecharEntityManager(){
		em.close();
		
		assertFalse("Deve ter instanciado o entity manager",em.isOpen());
	}
	
	@Test
	public void deveAbrirUmaTransacao(){
		assertFalse("Transacao deve estar fechada",em.getTransaction().isActive());	
		em.getTransaction().begin();
		assertTrue("Transacao deve estar aberta",em.getTransaction().isActive());
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
