package com.bombero.modelo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the resultados_evaluacion database table.
 * 
 */
@Entity
@Table(name="resultados_evaluacion")
@NamedQuery(name="ResultadosEvaluacion.buscarPorPersona", query="SELECT r FROM ResultadosEvaluacion r where r.persona.idPersona = :idPersona")
public class ResultadosEvaluacion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_resultado")
	private Integer idResultado;

	private Integer calificacion;

	private String estado;

	//bi-directional many-to-one association to Evaluacion
	@ManyToOne
	@JoinColumn(name="id_evaluacion")
	private Evaluacion evaluacion;

	//bi-directional many-to-one association to Persona
	@ManyToOne
	@JoinColumn(name="id_persona")
	private Persona persona;


	//bi-directional many-to-one association to ResultadosEvaluacion
	@OneToMany(mappedBy="resultadoEvaluacion", cascade = CascadeType.ALL)
	private List<RespuestaSeleccionada> respuestaSeleccionadas;

	public ResultadosEvaluacion() {
	}

	public Integer getIdResultado() {
		return this.idResultado;
	}

	public void setIdResultado(Integer idResultado) {
		this.idResultado = idResultado;
	}

	public Integer getCalificacion() {
		return this.calificacion;
	}

	public void setCalificacion(Integer calificacion) {
		this.calificacion = calificacion;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Evaluacion getEvaluacion() {
		return this.evaluacion;
	}

	public void setEvaluacion(Evaluacion evaluacion) {
		this.evaluacion = evaluacion;
	}

	public Persona getPersona() {
		return this.persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<RespuestaSeleccionada> getRespuestaSeleccionadas() {
		return respuestaSeleccionadas;
	}

	public void setRespuestaSeleccionadas(List<RespuestaSeleccionada> respuestaSeleccionadas) {
		this.respuestaSeleccionadas = respuestaSeleccionadas;
	}

	public RespuestaSeleccionada addRespuestaSeleccionada(RespuestaSeleccionada respuestaSeleccionada) {
		getRespuestaSeleccionadas().add(respuestaSeleccionada);
		respuestaSeleccionada.setResultadoEvaluacion(this);

		return respuestaSeleccionada;
	}

	public RespuestaSeleccionada removeRespuestaSeleccionada(RespuestaSeleccionada respuestaSeleccionada) {
		getRespuestaSeleccionadas().remove(respuestaSeleccionada);
		respuestaSeleccionada.setResultadoEvaluacion(null);

		return respuestaSeleccionada;
	}
}