package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the evaluacion database table.
 * 
 */
@Entity
@Table(name="evaluacion")
@NamedQueries({
	@NamedQuery(name="Evaluacion.buscarPeriodoModulo", query="SELECT e FROM Evaluacion e where e.estado = 'A' and e.modulo.idModulo = :idModulo and e.periodo.idPeriodo = :idPeriodo"),
	@NamedQuery(name="Evaluacion.buscarUltimo", query="SELECT e FROM Evaluacion e where e.estado = 'A' order by e.idEvaluacion desc"),
	@NamedQuery(name="Evaluacion.buscarPorId", query="SELECT e FROM Evaluacion e where e.estado = 'A' and e.idEvaluacion = :idEvaluacion"),
	@NamedQuery(name="Evaluacion.buscarPorPeriodo", query="SELECT e FROM Evaluacion e where e.estado = 'A' and e.periodo.idPeriodo = :idPeriodo")
})
public class Evaluacion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_evaluacion")
	private Integer idEvaluacion;

	private String descripcion;

	private String estado;

	//bi-directional many-to-one association to Modulo
	@ManyToOne
	@JoinColumn(name="id_modulo")
	private Modulo modulo;

	//bi-directional many-to-one association to Periodo
	@ManyToOne
	@JoinColumn(name="id_periodo")
	private Periodo periodo;

	//bi-directional many-to-one association to Pregunta
	@OneToMany(mappedBy="evaluacion", cascade = CascadeType.ALL)
	private List<Pregunta> preguntas;

	//bi-directional many-to-one association to ResultadosEvaluacion
	@OneToMany(mappedBy="evaluacion")
	private List<ResultadosEvaluacion> resultadosEvaluacions;

	public Evaluacion() {
	}

	public Integer getIdEvaluacion() {
		return this.idEvaluacion;
	}

	public void setIdEvaluacion(Integer idEvaluacion) {
		this.idEvaluacion = idEvaluacion;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Modulo getModulo() {
		return this.modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	public Periodo getPeriodo() {
		return this.periodo;
	}

	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
	}

	public List<Pregunta> getPreguntas() {
		return this.preguntas;
	}

	public void setPreguntas(List<Pregunta> preguntas) {
		this.preguntas = preguntas;
	}

	public Pregunta addPregunta(Pregunta pregunta) {
		getPreguntas().add(pregunta);
		pregunta.setEvaluacion(this);

		return pregunta;
	}

	public Pregunta removePregunta(Pregunta pregunta) {
		getPreguntas().remove(pregunta);
		pregunta.setEvaluacion(null);

		return pregunta;
	}

	public List<ResultadosEvaluacion> getResultadosEvaluacions() {
		return this.resultadosEvaluacions;
	}

	public void setResultadosEvaluacions(List<ResultadosEvaluacion> resultadosEvaluacions) {
		this.resultadosEvaluacions = resultadosEvaluacions;
	}

	public ResultadosEvaluacion addResultadosEvaluacion(ResultadosEvaluacion resultadosEvaluacion) {
		getResultadosEvaluacions().add(resultadosEvaluacion);
		resultadosEvaluacion.setEvaluacion(this);

		return resultadosEvaluacion;
	}

	public ResultadosEvaluacion removeResultadosEvaluacion(ResultadosEvaluacion resultadosEvaluacion) {
		getResultadosEvaluacions().remove(resultadosEvaluacion);
		resultadosEvaluacion.setEvaluacion(null);

		return resultadosEvaluacion;
	}

}