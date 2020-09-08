package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the respuesta_seleccionada database table.
 * 
 */
@Entity
@Table(name="respuesta_seleccionada")
@NamedQuery(name="RespuestaSeleccionada.findAll", query="SELECT r FROM RespuestaSeleccionada r")
public class RespuestaSeleccionada implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_respuesta_seleccionada")
	private Integer idRespuestaSeleccionada;

	private String estado;

	@Column(name="id_pregunta")
	private Integer idPregunta;

	@Column(name="id_respuesta")
	private Integer idRespuesta;
	
	//bi-directional many-to-one association to Persona
	@ManyToOne
	@JoinColumn(name="id_resultado")
	private ResultadosEvaluacion resultadoEvaluacion;

	public RespuestaSeleccionada() {
	}

	public Integer getIdRespuestaSeleccionada() {
		return this.idRespuestaSeleccionada;
	}

	public void setIdRespuestaSeleccionada(Integer idRespuestaSeleccionada) {
		this.idRespuestaSeleccionada = idRespuestaSeleccionada;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}



	public Integer getIdPregunta() {
		return idPregunta;
	}

	public void setIdPregunta(Integer idPregunta) {
		this.idPregunta = idPregunta;
	}


	public ResultadosEvaluacion getResultadoEvaluacion() {
		return resultadoEvaluacion;
	}

	public void setResultadoEvaluacion(ResultadosEvaluacion resultadoEvaluacion) {
		this.resultadoEvaluacion = resultadoEvaluacion;
	}

	public Integer getIdRespuesta() {
		return idRespuesta;
	}

	public void setIdRespuesta(Integer idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

}