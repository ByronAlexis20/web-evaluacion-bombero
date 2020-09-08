package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the asignar_guardia database table.
 * 
 */
@Entity
@Table(name="asignar_guardia")
@NamedQueries({
	@NamedQuery(name="AsignarGuardia.buscarPorCompania", query="SELECT a FROM AsignarGuardia a where a.estado = 'A' and a.compania.idCompania = :idCompania"),
	@NamedQuery(name="AsignarGuardia.buscarAsignacion", query="SELECT a FROM AsignarGuardia a where a.estado = 'A'"),
})
public class AsignarGuardia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_guardia")
	private Integer idGuardia;

	private String estado;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_fin")
	private Date fechaFin;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_inicio")
	private Date fechaInicio;

	private String observacion;

	//bi-directional many-to-one association to Compania
	@ManyToOne
	@JoinColumn(name="id_compania")
	private Compania compania;

	//bi-directional many-to-one association to PersonalAutorizado
	@ManyToOne
	@JoinColumn(name="id_personal")
	private PersonalAutorizado personalAutorizado;

	public AsignarGuardia() {
	}

	public Integer getIdGuardia() {
		return this.idGuardia;
	}

	public void setIdGuardia(Integer idGuardia) {
		this.idGuardia = idGuardia;
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

	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Compania getCompania() {
		return this.compania;
	}

	public void setCompania(Compania compania) {
		this.compania = compania;
	}

	public PersonalAutorizado getPersonalAutorizado() {
		return this.personalAutorizado;
	}

	public void setPersonalAutorizado(PersonalAutorizado personalAutorizado) {
		this.personalAutorizado = personalAutorizado;
	}

}