package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the personal_autorizado database table.
 * 
 */
@Entity
@Table(name="personal_autorizado")
@NamedQuery(name="PersonalAutorizado.findAll", query="SELECT p FROM PersonalAutorizado p where p.estado = 'A'")
public class PersonalAutorizado implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_personal")
	private Integer idPersonal;

	private String estado;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_ingreso")
	private Date fechaIngreso;

	//bi-directional many-to-one association to AsignacionCabecera
	@OneToMany(mappedBy="personalAutorizado")
	private List<AsignacionCabecera> asignacionCabeceras;

	//bi-directional many-to-one association to AsignarGuardia
	@OneToMany(mappedBy="personalAutorizado")
	private List<AsignarGuardia> asignarGuardias;

	//bi-directional many-to-one association to Persona
	@ManyToOne
	@JoinColumn(name="id_persona")
	private Persona persona;

	public PersonalAutorizado() {
	}

	public Integer getIdPersonal() {
		return this.idPersonal;
	}

	public void setIdPersonal(Integer idPersonal) {
		this.idPersonal = idPersonal;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaIngreso() {
		return this.fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public List<AsignacionCabecera> getAsignacionCabeceras() {
		return this.asignacionCabeceras;
	}

	public void setAsignacionCabeceras(List<AsignacionCabecera> asignacionCabeceras) {
		this.asignacionCabeceras = asignacionCabeceras;
	}

	public AsignacionCabecera addAsignacionCabecera(AsignacionCabecera asignacionCabecera) {
		getAsignacionCabeceras().add(asignacionCabecera);
		asignacionCabecera.setPersonalAutorizado(this);

		return asignacionCabecera;
	}

	public AsignacionCabecera removeAsignacionCabecera(AsignacionCabecera asignacionCabecera) {
		getAsignacionCabeceras().remove(asignacionCabecera);
		asignacionCabecera.setPersonalAutorizado(null);

		return asignacionCabecera;
	}

	public List<AsignarGuardia> getAsignarGuardias() {
		return this.asignarGuardias;
	}

	public void setAsignarGuardias(List<AsignarGuardia> asignarGuardias) {
		this.asignarGuardias = asignarGuardias;
	}

	public AsignarGuardia addAsignarGuardia(AsignarGuardia asignarGuardia) {
		getAsignarGuardias().add(asignarGuardia);
		asignarGuardia.setPersonalAutorizado(this);

		return asignarGuardia;
	}

	public AsignarGuardia removeAsignarGuardia(AsignarGuardia asignarGuardia) {
		getAsignarGuardias().remove(asignarGuardia);
		asignarGuardia.setPersonalAutorizado(null);

		return asignarGuardia;
	}

	public Persona getPersona() {
		return this.persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

}