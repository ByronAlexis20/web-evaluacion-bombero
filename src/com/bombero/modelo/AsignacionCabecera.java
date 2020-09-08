package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the asignacion_cabecera database table.
 * 
 */
@Entity
@Table(name="asignacion_cabecera")
@NamedQuery(name="AsignacionCabecera.findAll", query="SELECT a FROM AsignacionCabecera a")
public class AsignacionCabecera implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_asignacion")
	private int idAsignacion;

	private String estado;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_registro")
	private Date fechaRegistro;

	//bi-directional many-to-one association to PersonalAutorizado
	@ManyToOne
	@JoinColumn(name="id_personal")
	private PersonalAutorizado personalAutorizado;

	//bi-directional many-to-one association to AsignacionDetalle
	@OneToMany(mappedBy="asignacionCabecera")
	private List<AsignacionDetalle> asignacionDetalles;

	public AsignacionCabecera() {
	}

	public int getIdAsignacion() {
		return this.idAsignacion;
	}

	public void setIdAsignacion(int idAsignacion) {
		this.idAsignacion = idAsignacion;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaRegistro() {
		return this.fechaRegistro;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public PersonalAutorizado getPersonalAutorizado() {
		return this.personalAutorizado;
	}

	public void setPersonalAutorizado(PersonalAutorizado personalAutorizado) {
		this.personalAutorizado = personalAutorizado;
	}

	public List<AsignacionDetalle> getAsignacionDetalles() {
		return this.asignacionDetalles;
	}

	public void setAsignacionDetalles(List<AsignacionDetalle> asignacionDetalles) {
		this.asignacionDetalles = asignacionDetalles;
	}

	public AsignacionDetalle addAsignacionDetalle(AsignacionDetalle asignacionDetalle) {
		getAsignacionDetalles().add(asignacionDetalle);
		asignacionDetalle.setAsignacionCabecera(this);

		return asignacionDetalle;
	}

	public AsignacionDetalle removeAsignacionDetalle(AsignacionDetalle asignacionDetalle) {
		getAsignacionDetalles().remove(asignacionDetalle);
		asignacionDetalle.setAsignacionCabecera(null);

		return asignacionDetalle;
	}

}