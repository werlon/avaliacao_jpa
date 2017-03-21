package br.edu.faculdadedelta.modelo;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="alunos")
public class Aluno extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_aluno")
	private Long id;

	@Column(length = 20)
	private String matricula;
	
	@Column(length = 14)
	private String cpf;
	
	@Column(name="nome_aluno",length = 60, nullable = false)
	private String nome;
	
	@Column(nullable = false)
	private int idade;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "data_nascimento", nullable = true)
	@Basic(fetch = FetchType.LAZY)
	private Date dataNascimento;
	

	public Aluno() {
	}
	
	public Aluno(Long id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

}
