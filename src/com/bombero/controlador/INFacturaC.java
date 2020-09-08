package com.bombero.controlador;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.bombero.modelo.EquiposVestimenta;
import com.bombero.modelo.EquiposVestimentaDAO;
import com.bombero.modelo.FacturaDetalle;
import com.bombero.modelo.FacturaIngreso;
import com.bombero.modelo.FacturaIngresoDAO;
import com.bombero.util.Constantes;


public class INFacturaC {
	@Wire Window winFactura;
	@Wire Listbox lstDetalle;
	@Wire Textbox txtDescripcionEquipo;
	@Wire Textbox txtPrecioUnitario;
	@Wire Textbox txtCantidad;
	@Wire Textbox txtCodigo;
	@Wire Textbox txtTotal;
	@Wire Textbox txtNoFactura;
	@Wire Datebox dtpFecha;

	private FacturaIngreso factura;
	List<FacturaDetalle> detalleLista;
	FacturaDetalle detalleSeleccionado;
	DecimalFormat df = new DecimalFormat("#0.00");
	FacturaIngresoDAO facturaDAO = new FacturaIngresoDAO();
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		txtDescripcionEquipo.setDisabled(true);
		nuevo();
	}
	@Command
	public void buscarFactura() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("VENTANA", this);
			Window ventanaCargar = (Window) Executions.createComponents("/formularios/ingresos/INFacturaListado.zul", winFactura, params);
			ventanaCargar.doModal();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@Command
	public void buscarEquipoVestimenta() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("VENTANA", this);
			Window ventanaCargar = (Window) Executions.createComponents("/formularios/ingresos/INEquiposLista.zul", winFactura, params);
			ventanaCargar.doModal();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	EquiposVestimenta equipoSeleccionado;
	public void llenarDetalle(EquiposVestimenta equipo) {
		try {
			System.out.println(equipo.toString());
			txtDescripcionEquipo.setText(equipo.getDescripcion());
			txtPrecioUnitario.setText(String.valueOf(equipo.getPrecioCompra()));
			txtCantidad.setText("0");
			equipoSeleccionado = equipo;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}

	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Command
	public void agregarEquipo() {
		try {
			Double totalFactura = 0.0;
			if(equipoSeleccionado == null) {
				Clients.showNotification("Debe seleccionar un equipo de vestimenta");
				return;
			}
			if(txtCantidad.getText().equals("") || txtCantidad.getText().equals("0")) {
				Clients.showNotification("Debe registrar la cantidad","info",txtCantidad,"end_center",2000);
				return;
			}
			if(detalleLista != null)
				detalleLista = null;

			List<FacturaDetalle> listaDetalle = new ArrayList<>();
			for(Listitem item : lstDetalle.getItems()) {
				FacturaDetalle detalle = (FacturaDetalle) item.getValue();
				totalFactura = totalFactura + Double.parseDouble(String.valueOf(detalle.getTotal()));
				listaDetalle.add(detalle);
			}
			//procedimiento para crear el nuevo detalle
			FacturaDetalle detAdd = new FacturaDetalle();
			detAdd.setIdDetalle(null);
			detAdd.setEquiposVestimenta(equipoSeleccionado);
			detAdd.setCantidad(Integer.parseInt(txtCantidad.getText()));
			detAdd.setEstado(Constantes.ESTADO_ACTIVO);
			detAdd.setFecha(new Date());
			System.out.println("Precio unitario: " + txtPrecioUnitario.getText());
			System.out.println(Double.parseDouble(txtPrecioUnitario.getText()));
			System.out.println(df.format(Double.parseDouble(txtPrecioUnitario.getText())).replace(",", ".") );
			detAdd.setPrecioUnitario(BigDecimal.valueOf(Double.parseDouble(df.format(Double.parseDouble(txtPrecioUnitario.getText())).replace(",", "."))));

			Double precio = Double.parseDouble(txtPrecioUnitario.getText());
			Double total = precio * Integer.parseInt(txtCantidad.getText());
			totalFactura = totalFactura + Double.parseDouble(String.valueOf(BigDecimal.valueOf(total)));
			txtTotal.setText(df.format(totalFactura).replace(",", "."));
			detAdd.setTotal(BigDecimal.valueOf(Double.parseDouble(df.format(total).replace(",", "."))));
			listaDetalle.add(detAdd);
			detalleLista = listaDetalle;
			lstDetalle.setModel(new ListModelList(detalleLista));

			equipoSeleccionado = null;
			txtCodigo.setText("");
			txtDescripcionEquipo.setText("");
			txtPrecioUnitario.setText("");
			txtCantidad.setText("");
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void recuperarFactura(FacturaIngreso facturaRecuperada) {
		try {
			nuevo();
			System.out.println("Factura recuperada");
			System.out.println(facturaRecuperada.toString());

			factura = facturaRecuperada;
			txtNoFactura.setText(factura.getNumeroFactura());
			dtpFecha.setValue(factura.getFecha());
			txtTotal.setText(String.valueOf(factura.getTotal()));
			List<FacturaDetalle> listaDetalle = new ArrayList<>();
			for(FacturaDetalle det : factura.getFacturaDetalles()) {
				if(det.getEstado().equals(Constantes.ESTADO_ACTIVO))
					listaDetalle.add(det);
			}
			detalleLista = listaDetalle;
			lstDetalle.setModel(new ListModelList(detalleLista));

			equipoSeleccionado = null;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Command
	public void quitarDetalle() {
		if(detalleSeleccionado == null) {
			Clients.showNotification("Debe seleccionar un detalle a eliminar");
			return;
		}
		detalleLista.remove(detalleSeleccionado);
		lstDetalle.setModel(new ListModelList(detalleLista));
		Double totalFactura = 0.0;
		for(FacturaDetalle detalle : detalleLista) {
			totalFactura = totalFactura + Double.parseDouble(String.valueOf(detalle.getTotal()));
		}
		txtTotal.setText(df.format(totalFactura).replace(",", "."));
		detalleSeleccionado = null;
	}

	@Command
	public void grabar() {
		try {
			if(txtNoFactura.getText().equals("")) {
				Clients.showNotification("Debe registrar número de factura de ingreso");
				return;
			}
			if(lstDetalle.getItems().size() == 0) {
				Clients.showNotification("No se ha registrado detalle de la factura de ingreso");
				return;
			}
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						//lista que va a almacenar las cantidades y el equipo para actualizar el stock
						List<EquiposStock> listaStock = new ArrayList<>();
						//grabar
						facturaDAO.getEntityManager().getTransaction().begin();
						if(factura == null) {//es una factura nueva
							factura = new FacturaIngreso();
							factura.setEstado(Constantes.ESTADO_ACTIVO);
							factura.setFecha(dtpFecha.getValue());
							factura.setIdFactura(null);
							factura.setNumeroFactura(txtNoFactura.getText());
							factura.setTotal(BigDecimal.valueOf(Double.parseDouble(txtTotal.getText())));
							//si es una factura nueva, los detalles tambien son nuevos
							List<FacturaDetalle> listaDetalle = new ArrayList<>();
							System.out.println("Items detalles: " + lstDetalle.getItems().size());
							for(Listitem item : lstDetalle.getItems()) {
								FacturaDetalle detalle = (FacturaDetalle) item.getValue();
								System.out.println(detalle.toString());
								FacturaDetalle det = new FacturaDetalle();
								det.setCantidad(detalle.getCantidad());
								det.setEstado(Constantes.ESTADO_ACTIVO);
								det.setFecha(detalle.getFecha());
								det.setIdDetalle(null);
								det.setEquiposVestimenta(detalle.getEquiposVestimenta());
								det.setPrecioUnitario(detalle.getPrecioUnitario());
								det.setTotal(detalle.getTotal());
								det.setFacturaIngreso(factura);
								listaDetalle.add(det);
								//objeto para agregar el equipo a actualizar, ademas se debe de actualizar el precio
								EquiposStock equipoStok = new EquiposStock();
								equipoStok.setAccion(Constantes.OPERACION_SUMA);
								equipoStok.setCantidad(det.getCantidad());
								equipoStok.setIdEquipo(det.getEquiposVestimenta().getIdEquipo());
								equipoStok.setPrecio(det.getPrecioUnitario());
								listaStock.add(equipoStok);
							}
							factura.setFacturaDetalles(listaDetalle);
							facturaDAO.getEntityManager().persist(factura);
							Clients.showNotification("Datos grabados correctamente");
						}else {
							factura.setEstado(Constantes.ESTADO_ACTIVO);
							factura.setFecha(dtpFecha.getValue());
							factura.setNumeroFactura(txtNoFactura.getText());
							factura.setTotal(BigDecimal.valueOf(Double.parseDouble(txtTotal.getText())));
							//verificar si la factura recuperada cuenta con detalles
							if(factura.getFacturaDetalles().size() > 0) {
								//primero agregar los items que se han agregado nuevos
								for(Listitem item : lstDetalle.getItems()) {
									FacturaDetalle detalle = (FacturaDetalle) item.getValue();
									if(detalle.getIdDetalle() == null) {//los id nulos son los nuevos
										detalle.setEstado(Constantes.ESTADO_ACTIVO);
										detalle.setFacturaIngreso(factura);
										factura.getFacturaDetalles().add(detalle);
										//objeto para agregar el equipo a actualizar, ademas se debe de actualizar el precio
										EquiposStock equipoStok = new EquiposStock();
										equipoStok.setAccion(Constantes.OPERACION_SUMA);
										equipoStok.setCantidad(detalle.getCantidad());
										equipoStok.setIdEquipo(detalle.getEquiposVestimenta().getIdEquipo());
										equipoStok.setPrecio(detalle.getPrecioUnitario());
										listaStock.add(equipoStok);
									}

								}
								//luego hay que verificar los detalles que se han eliminado
								System.out.println("detalles grabar");
								boolean bandera = false;
								for(FacturaDetalle det : factura.getFacturaDetalles()) {
									if(det.getIdDetalle() != null) {
										bandera = false;
										for(Listitem item : lstDetalle.getItems()) {
											FacturaDetalle detalle = (FacturaDetalle) item.getValue();
											if(detalle.getIdDetalle() != null) {
												if(detalle.getIdDetalle() == det.getIdDetalle())
													bandera = true;
											}
										}
										if(bandera == false) {
											det.setEstado(Constantes.ESTADO_INACTIVO);
											//objeto para agregar el equipo a actualizar, ademas se debe de actualizar el precio
											EquiposStock equipoStok = new EquiposStock();
											equipoStok.setAccion(Constantes.OPERACION_RESTA);
											equipoStok.setCantidad(det.getCantidad());
											equipoStok.setIdEquipo(det.getEquiposVestimenta().getIdEquipo());
											equipoStok.setPrecio(det.getPrecioUnitario());
											listaStock.add(equipoStok);
										}
									}
								}
								facturaDAO.getEntityManager().merge(factura);
								Clients.showNotification("Datos grabados correctamente");
							}else {//si no tiene nada de detalles, todos son nuevas
								List<FacturaDetalle> listaDetalle = new ArrayList<>();
								for(Listitem item : lstDetalle.getItems()) {
									FacturaDetalle detalle = (FacturaDetalle) item.getValue();
									FacturaDetalle det = new FacturaDetalle();
									det.setCantidad(detalle.getCantidad());
									det.setEstado(Constantes.ESTADO_ACTIVO);
									det.setFecha(detalle.getFecha());
									det.setIdDetalle(null);
									det.setEquiposVestimenta(detalle.getEquiposVestimenta());
									det.setPrecioUnitario(detalle.getPrecioUnitario());
									det.setTotal(detalle.getTotal());
									det.setFacturaIngreso(factura);
									listaDetalle.add(det);
									//objeto para agregar el equipo a actualizar, ademas se debe de actualizar el precio
									EquiposStock equipoStok = new EquiposStock();
									equipoStok.setAccion(Constantes.OPERACION_RESTA);
									equipoStok.setCantidad(det.getCantidad());
									equipoStok.setIdEquipo(det.getEquiposVestimenta().getIdEquipo());
									equipoStok.setPrecio(det.getPrecioUnitario());
									listaStock.add(equipoStok);
								}
								factura.setFacturaDetalles(listaDetalle);
								facturaDAO.getEntityManager().merge(factura);
								Clients.showNotification("Datos grabados correctamente");
							}
						}
						facturaDAO.getEntityManager().getTransaction().commit();
						//antes del nuevo, hay que actualizar el stock
						actualizarStock(listaStock);
						nuevo();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);

		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	private void actualizarStock(List<EquiposStock> equipos) {
		try {
			EquiposVestimentaDAO equipoDAO = new EquiposVestimentaDAO();
			for(EquiposStock eq : equipos) {
				if(eq.getAccion().equals(Constantes.OPERACION_SUMA)) {
					List<EquiposVestimenta> lista = equipoDAO.getEquipoById(eq.getIdEquipo());
					if(lista.size() > 0) {
						EquiposVestimenta equipoV = lista.get(0);
						equipoV.setPrecioCompra(eq.getPrecio());
						equipoV.setStock(equipoV.getStock() + eq.getCantidad());
						equipoDAO.getEntityManager().getTransaction().begin();
						equipoDAO.getEntityManager().merge(equipoV);
						equipoDAO.getEntityManager().getTransaction().commit();
					}
				}else {
					List<EquiposVestimenta> lista = equipoDAO.getEquipoById(eq.getIdEquipo());
					if(lista.size() > 0) {
						EquiposVestimenta equipoV = lista.get(0);
						equipoV.setPrecioCompra(eq.getPrecio());
						equipoV.setStock(equipoV.getStock() - eq.getCantidad());
						equipoDAO.getEntityManager().getTransaction().begin();
						equipoDAO.getEntityManager().merge(equipoV);
						equipoDAO.getEntityManager().getTransaction().commit();
					}
				}
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Command
	public void nuevo() {
		if(detalleLista != null) {
			detalleLista.clear();
			lstDetalle.setModel(new ListModelList(detalleLista));
			detalleLista = null;
		}
		factura = null;
		txtCantidad.setText("0");
		txtCodigo.setText("");
		txtDescripcionEquipo.setText("");
		txtPrecioUnitario.setText("");
		txtTotal.setText("0.00");
		dtpFecha.setValue(new Date());
		txtNoFactura.setText("");
	}
	public FacturaIngreso getFactura() {
		return factura;
	}
	public void setFactura(FacturaIngreso factura) {
		this.factura = factura;
	}

	public FacturaDetalle getDetalleSeleccionado() {
		return detalleSeleccionado;
	}

	public void setDetalleSeleccionado(FacturaDetalle detalleSeleccionado) {
		this.detalleSeleccionado = detalleSeleccionado;
	}

	public EquiposVestimenta getEquipoSeleccionado() {
		return equipoSeleccionado;
	}

	public void setEquipoSeleccionado(EquiposVestimenta equipoSeleccionado) {
		this.equipoSeleccionado = equipoSeleccionado;
	}
	public class EquiposStock{
		private Integer idEquipo;
		private Integer cantidad;
		private String accion;
		private BigDecimal precio;
		public Integer getIdEquipo() {
			return idEquipo;
		}
		public void setIdEquipo(Integer idEquipo) {
			this.idEquipo = idEquipo;
		}
		public Integer getCantidad() {
			return cantidad;
		}
		public void setCantidad(Integer cantidad) {
			this.cantidad = cantidad;
		}
		public String getAccion() {
			return accion;
		}
		public void setAccion(String accion) {
			this.accion = accion;
		}
		public BigDecimal getPrecio() {
			return precio;
		}
		public void setPrecio(BigDecimal precio) {
			this.precio = precio;
		}
	}
}
