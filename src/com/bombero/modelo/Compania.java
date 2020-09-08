package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the compania database table.
 * 
 */
@Entity
@Table(name="compania")
@NamedQueries({
	@NamedQuery(name="Compania.buscarPorPatron", query="SELECT c FROM Compania c where c.nombre like :patron")
})
public class Compania implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_compania")
	private Integer idCompania;

	private String direccion;

	private String estado;

	private String nombre;

	//bi-directional many-to-one association to AsignarGuardia
	@OneToMany(mappedBy="compania")
	private List<AsignarGuardia> asignarGuardias;

	public Compania() {
	}

	public Integer getIdCompania() {
		return this.idCompania;
	}

	public void setIdCompania(Integer idCompania) {
		this.idCompania = idCompania;
	}

	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<AsignarGuardia> getAsignarGuardias() {
		return this.asignarGuardias;
	}

	public void setAsignarGuardias(List<AsignarGuardia> asignarGuardias) {
		this.asignarGuardias = asignarGuardias;
	}

	public AsignarGuardia addAsignarGuardia(AsignarGuardia asignarGuardia) {
		getAsignarGuardias().add(asignarGuardia);
		asignarGuardia.setCompania(this);

		return asignarGuardia;
	}

	public AsignarGuardia removeAsignarGuardia(AsignarGuardia asignarGuardia) {
		getAsignarGuardias().remove(asignarGuardia);
		asignarGuardia.setCompania(null);

		return asignarGuardia;
	}

}