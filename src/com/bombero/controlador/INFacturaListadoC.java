package com.bombero.controlador;

import java.io.IOException;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.bombero.modelo.FacturaIngreso;
import com.bombero.modelo.FacturaIngresoDAO;

public class INFacturaListadoC {
	@Wire Window winFacturaLista;
	@Wire Listbox lstFacturas;
	@Wire Textbox txtBuscar;
	List<FacturaIngreso> facturaLista;
	FacturaIngreso facturaSeleccionado;
	FacturaIngresoDAO facturaDAO = new FacturaIngresoDAO();
	INFacturaC facturaF = new INFacturaC();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		facturaF = (INFacturaC)Executions.getCurrent().getArg().get("VENTANA");
		cargarFacturas("");
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void cargarFacturas(String numero) {
		try {
			List<FacturaIngreso> lista = facturaDAO.getListaFacturas(numero);
			facturaLista = lista;
			lstFacturas.setModel(new ListModelList(facturaLista));
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void buscarFactura() {
		cargarFacturas(txtBuscar.getText());
	}
	@Command
	public void seleccionarFactura(@BindingParam("factura") FacturaIngreso factura) {
		System.out.println("Factura seleccionada");
		System.out.println(factura.toString());
		facturaF.recuperarFactura(factura);
		salir();
	}
	@Command
	public void salir() {
		winFacturaLista.detach();
	}
	public List<FacturaIngreso> getFacturaLista() {
		return facturaLista;
	}
	public void setFacturaLista(List<FacturaIngreso> facturaLista) {
		this.facturaLista = facturaLista;
	}
	public FacturaIngreso getFacturaSeleccionado() {
		return facturaSeleccionado;
	}
	public void setFacturaSeleccionado(FacturaIngreso facturaSeleccionado) {
		this.facturaSeleccionado = facturaSeleccionado;
	}
	
}
