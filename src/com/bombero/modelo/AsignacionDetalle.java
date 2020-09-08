package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the asignacion_detalle database table.
 * 
 */
@Entity
@Table(name="asignacion_detalle")
@NamedQuery(name="AsignacionDetalle.findAll", query="SELECT a FROM AsignacionDetalle a")
public class AsignacionDetalle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_asignacion_detalle")
	private int idAsignacionDetalle;

	private String estado;

	//bi-directional many-to-one association to AsignacionCabecera
	@ManyToOne
	@JoinColumn(name="id_asignacion")
	private AsignacionCabecera asignacionCabecera;

	//bi-directional many-to-one association to EquiposVestimenta
	@ManyToOne
	@JoinColumn(name="id_equipo")
	private EquiposVestimenta equiposVestimenta;

	public AsignacionDetalle() {
	}

	public int getIdAsignacionDetalle() {
		return this.idAsignacionDetalle;
	}

	public void setIdAsignacionDetalle(int idAsignacionDetalle) {
		this.idAsignacionDetalle = idAsignacionDetalle;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public AsignacionCabecera getAsignacionCabecera() {
		return this.asignacionCabecera;
	}

	public void setAsignacionCabecera(AsignacionCabecera asignacionCabecera) {
		this.asignacionCabecera = asignacionCabecera;
	}

	public EquiposVestimenta getEquiposVestimenta() {
		return this.equiposVestimenta;
	}

	public void setEquiposVestimenta(EquiposVestimenta equiposVestimenta) {
		this.equiposVestimenta = equiposVestimenta;
	}

}