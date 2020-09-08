package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the factura_detalle database table.
 * 
 */
@Entity
@Table(name="factura_detalle")
@NamedQuery(name="FacturaDetalle.findAll", query="SELECT f FROM FacturaDetalle f")
public class FacturaDetalle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_detalle")
	private Integer idDetalle;

	private int cantidad;

	@Column(name="precio_unitario")
	private BigDecimal precioUnitario;
	
	private String estado;

	@Temporal(TemporalType.DATE)
	private Date fecha;

	private BigDecimal total;

	//bi-directional many-to-one association to FacturaIngreso
	@ManyToOne
	@JoinColumn(name="id_factura")
	private FacturaIngreso facturaIngreso;

	//bi-directional many-to-one association to EquiposVestimenta
	@ManyToOne
	@JoinColumn(name="id_equipo")
	private EquiposVestimenta equiposVestimenta;

	public FacturaDetalle() {
	}

	public Integer getIdDetalle() {
		return this.idDetalle;
	}

	public void setIdDetalle(Integer idDetalle) {
		this.idDetalle = idDetalle;
	}

	public int getCantidad() {
		return this.cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public BigDecimal getTotal() {
		return this.total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public FacturaIngreso getFacturaIngreso() {
		return this.facturaIngreso;
	}

	public void setFacturaIngreso(FacturaIngreso facturaIngreso) {
		this.facturaIngreso = facturaIngreso;
	}

	public EquiposVestimenta getEquiposVestimenta() {
		return this.equiposVestimenta;
	}

	public void setEquiposVestimenta(EquiposVestimenta equiposVestimenta) {
		this.equiposVestimenta = equiposVestimenta;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	@Override
	public String toString() {
		return "FacturaDetalle [idDetalle=" + idDetalle + ", cantidad=" + cantidad + ", precioUnitario="
				+ precioUnitario + ", estado=" + estado + ", fecha=" + fecha + ", total=" + total + "]";
	}

}