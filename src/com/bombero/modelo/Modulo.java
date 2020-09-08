package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the modulo database table.
 * 
 */
@Entity
@Table(name="modulo")
@NamedQueries({
	@NamedQuery(name="Modulo.buscarPorPatron", query="SELECT m FROM Modulo m where m.modulo like :patron and m.estado = 'A'"),
	@NamedQuery(name="Modulo.buscarPorId", query="SELECT m FROM Modulo m where m.idModulo = :idModulo and m.estado = 'A'")
})
public class Modulo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_modulo")
	private Integer idModulo;

	private String estado;

	private String modulo;

	//bi-directional many-to-one association to Evaluacion
	@OneToMany(mappedBy="modulo")
	private List<Evaluacion> evaluacions;

	public Modulo() {
	}

	public Integer getIdModulo() {
		return this.idModulo;
	}

	public void setIdModulo(Integer idModulo) {
		this.idModulo = idModulo;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getModulo() {
		return this.modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public List<Evaluacion> getEvaluacions() {
		return this.evaluacions;
	}

	public void setEvaluacions(List<Evaluacion> evaluacions) {
		this.evaluacions = evaluacions;
	}

	public Evaluacion addEvaluacion(Evaluacion evaluacion) {
		getEvaluacions().add(evaluacion);
		evaluacion.setModulo(this);

		return evaluacion;
	}

	public Evaluacion removeEvaluacion(Evaluacion evaluacion) {
		getEvaluacions().remove(evaluacion);
		evaluacion.setModulo(null);

		return evaluacion;
	}

}