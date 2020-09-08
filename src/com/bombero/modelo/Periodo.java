package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the periodo database table.
 * 
 */
@Entity
@Table(name="periodo")
@NamedQueries({
	@NamedQuery(name="Periodo.buscarPorPatron", query="SELECT p FROM Periodo p where p.descripcion like :patron order by p.estadoPeriodo asc"),
	@NamedQuery(name="Periodo.buscarPorId", query="SELECT p FROM Periodo p where p.idPeriodo = :idPeriodo"),
	@NamedQuery(name="Periodo.buscarPorEvaluacionIniciado", query="SELECT p FROM Periodo p where p.estadovaluacion = :estado and p.estado = 'A'")
})
public class Periodo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_periodo")
	private Integer idPeriodo;

	private String descripcion;

	private String estado;

	@Column(name="estado_periodo")
	private String estadoPeriodo;
	
	@Column(name="estado_evaluacion")
	private String estadovaluacion;
	
	@Temporal(TemporalType.DATE)
	@Column(name="fecha_fin")
	private Date fechaFin;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_inicio")
	private Date fechaInicio;

	//bi-directional many-to-one association to Evaluacion
	@OneToMany(mappedBy="periodo")
	private List<Evaluacion> evaluacions;

	public Periodo() {
	}

	public Integer getIdPeriodo() {
		return this.idPeriodo;
	}

	public void setIdPeriodo(Integer idPeriodo) {
		this.idPeriodo = idPeriodo;
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

	public Date getFechaFin() {
		return this.fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Date getFechaInicio() {
		return this.fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public List<Evaluacion> getEvaluacions() {
		return this.evaluacions;
	}

	public void setEvaluacions(List<Evaluacion> evaluacions) {
		this.evaluacions = evaluacions;
	}

	public Evaluacion addEvaluacion(Evaluacion evaluacion) {
		getEvaluacions().add(evaluacion);
		evaluacion.setPeriodo(this);

		return evaluacion;
	}

	public Evaluacion removeEvaluacion(Evaluacion evaluacion) {
		getEvaluacions().remove(evaluacion);
		evaluacion.setPeriodo(null);

		return evaluacion;
	}

	public String getEstadoPeriodo() {
		return estadoPeriodo;
	}

	public void setEstadoPeriodo(String estadoPeriodo) {
		this.estadoPeriodo = estadoPeriodo;
	}

	public String getEstadovaluacion() {
		return estadovaluacion;
	}

	public void setEstadovaluacion(String estadovaluacion) {
		this.estadovaluacion = estadovaluacion;
	}

}