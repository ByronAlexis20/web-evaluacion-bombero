package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the equipos_vestimenta database table.
 * 
 */
@Entity
@Table(name="equipos_vestimenta")
@NamedQueries({
	@NamedQuery(name="EquiposVestimenta.buscarPorPatron", query="SELECT e FROM EquiposVestimenta e where e.descripcion like :patron and e.estado = 'A'"),
	@NamedQuery(name="EquiposVestimenta.buscarPorId", query="SELECT e FROM EquiposVestimenta e where e.idEquipo = :idEquipo and e.estado = 'A'")
})
public class EquiposVestimenta implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_equipo")
	private Integer idEquipo;

	private String descripcion;

	private String estado;

	@Column(name="precio_compra")
	private BigDecimal precioCompra;

	private int stock;

	//bi-directional many-to-one association to AsignacionDetalle
	@OneToMany(mappedBy="equiposVestimenta")
	private List<AsignacionDetalle> asignacionDetalles;

	//bi-directional many-to-one association to FacturaDetalle
	@OneToMany(mappedBy="equiposVestimenta")
	private List<FacturaDetalle> facturaDetalles;

	public EquiposVestimenta() {
	}

	public Integer getIdEquipo() {
		return this.idEquipo;
	}

	public void setIdEquipo(Integer idEquipo) {
		this.idEquipo = idEquipo;
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

	public BigDecimal getPrecioCompra() {
		return this.precioCompra;
	}

	public void setPrecioCompra(BigDecimal precioCompra) {
		this.precioCompra = precioCompra;
	}

	public int getStock() {
		return this.stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public List<AsignacionDetalle> getAsignacionDetalles() {
		return this.asignacionDetalles;
	}

	public void setAsignacionDetalles(List<AsignacionDetalle> asignacionDetalles) {
		this.asignacionDetalles = asignacionDetalles;
	}

	public AsignacionDetalle addAsignacionDetalle(AsignacionDetalle asignacionDetalle) {
		getAsignacionDetalles().add(asignacionDetalle);
		asignacionDetalle.setEquiposVestimenta(this);

		return asignacionDetalle;
	}

	public AsignacionDetalle removeAsignacionDetalle(AsignacionDetalle asignacionDetalle) {
		getAsignacionDetalles().remove(asignacionDetalle);
		asignacionDetalle.setEquiposVestimenta(null);

		return asignacionDetalle;
	}

	public List<FacturaDetalle> getFacturaDetalles() {
		return this.facturaDetalles;
	}

	public void setFacturaDetalles(List<FacturaDetalle> facturaDetalles) {
		this.facturaDetalles = facturaDetalles;
	}

	public FacturaDetalle addFacturaDetalle(FacturaDetalle facturaDetalle) {
		getFacturaDetalles().add(facturaDetalle);
		facturaDetalle.setEquiposVestimenta(this);

		return facturaDetalle;
	}

	public FacturaDetalle removeFacturaDetalle(FacturaDetalle facturaDetalle) {
		getFacturaDetalles().remove(facturaDetalle);
		facturaDetalle.setEquiposVestimenta(null);

		return facturaDetalle;
	}

	@Override
	public String toString() {
		return "EquiposVestimenta [idEquipo=" + idEquipo + ", descripcion=" + descripcion + ", estado=" + estado
				+ ", precioCompra=" + precioCompra + ", stock=" + stock + "]";
	}

}