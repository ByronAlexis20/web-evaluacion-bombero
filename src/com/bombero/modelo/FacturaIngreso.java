package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the factura_ingreso database table.
 * 
 */
@Entity
@Table(name="factura_ingreso")
@NamedQuery(name="FacturaIngreso.buscarPorNumero", query="SELECT f FROM FacturaIngreso f where f.numeroFactura like :numero and f.estado = 'A'")
public class FacturaIngreso implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_factura")
	private Integer idFactura;

	private String estado;

	@Temporal(TemporalType.DATE)
	private Date fecha;

	@Column(name="numero_factura")
	private String numeroFactura;

	private BigDecimal subtotal;

	private BigDecimal total;

	//bi-directional many-to-one association to FacturaDetalle
	@OneToMany(mappedBy="facturaIngreso", cascade = CascadeType.ALL)
	private List<FacturaDetalle> facturaDetalles;

	public FacturaIngreso() {
	}

	public Integer getIdFactura() {
		return this.idFactura;
	}

	public void setIdFactura(Integer idFactura) {
		this.idFactura = idFactura;
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

	public String getNumeroFactura() {
		return this.numeroFactura;
	}

	public void setNumeroFactura(String numeroFactura) {
		this.numeroFactura = numeroFactura;
	}

	public BigDecimal getSubtotal() {
		return this.subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getTotal() {
		return this.total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<FacturaDetalle> getFacturaDetalles() {
		return this.facturaDetalles;
	}

	public void setFacturaDetalles(List<FacturaDetalle> facturaDetalles) {
		this.facturaDetalles = facturaDetalles;
	}

	public FacturaDetalle addFacturaDetalle(FacturaDetalle facturaDetalle) {
		getFacturaDetalles().add(facturaDetalle);
		facturaDetalle.setFacturaIngreso(this);

		return facturaDetalle;
	}

	public FacturaDetalle removeFacturaDetalle(FacturaDetalle facturaDetalle) {
		getFacturaDetalles().remove(facturaDetalle);
		facturaDetalle.setFacturaIngreso(null);

		return facturaDetalle;
	}

	@Override
	public String toString() {
		return "FacturaIngreso [idFactura=" + idFactura + ", estado=" + estado + ", fecha=" + fecha + ", numeroFactura="
				+ numeroFactura + ", subtotal=" + subtotal + ", total=" + total + "]";
	}

}