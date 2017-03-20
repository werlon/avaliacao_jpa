package br.edu.faculdadedelta.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="pagamentos")
public class Pagamento extends BaseEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_pagamento")
	private Long id;
	
	@Column(nullable = false)
	private double valor;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_hora",nullable = false)
	private Date dataHora;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_professor")
	private Professor professor;

	@Override
	public Long getId() {
		return id;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public Professor getProfessor() {
		return professor;
	}

	public void setProfessor(Professor professor) {
		this.professor = professor;
	}

}
