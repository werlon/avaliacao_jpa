package br.edu.faculdadedelta.modelo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="professores")
public class Professor extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_professor")
	private Long id;

	@Column(length = 20)
	private String registro;
	
	@Column(name="nome_professor",length = 60, nullable = false)
	private String nome;
	
	@OneToOne(mappedBy = "professor", fetch = FetchType.LAZY)
	private Materia materia;
	
	@OneToMany(mappedBy = "professor", fetch = FetchType.LAZY)
	private List<Pagamento> pagamentos;
	
	public Professor() {
	}
	
	public Professor(Long id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getRegistro() {
		return registro;
	}

	public void setRegistro(String registro) {
		this.registro = registro;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Materia getMateria() {
		return materia;
	}

	public void setMateria(Materia materia) {
		this.materia = materia;
	}

	public List<Pagamento> getPagamentos() {
		return pagamentos;
	}

	public void setPagamentos(List<Pagamento> pagamentos) {
		this.pagamentos = pagamentos;
	}
	
	
	
	
}
