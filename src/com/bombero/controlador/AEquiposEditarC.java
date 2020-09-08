package com.bombero.controlador;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.bombero.modelo.EquiposVestimenta;
import com.bombero.modelo.EquiposVestimentaDAO;
import com.bombero.util.ControllerHelper;

@SuppressWarnings("serial")
public class AEquiposEditarC extends SelectorComposer<Component>{
	@Wire private Window winEquipoEditar;
	@Wire private Textbox txtDescripcion;
	@Wire private Textbox txtPrecio;
	@Wire private Textbox txtStock;
	
	ControllerHelper helper = new ControllerHelper();
	EquiposVestimentaDAO companiaDAO = new EquiposVestimentaDAO();
	AEquiposListaC equiposLista;
	EquiposVestimenta equipo;
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		try {
			equiposLista = (AEquiposListaC)Executions.getCurrent().getArg().get("VentanaPadre");
			equipo = null;	
			if (Executions.getCurrent().getArg().get("Equipo") != null) {
				equipo = (EquiposVestimenta) Executions.getCurrent().getArg().get("Equipo");
			}else {
				equipo = new EquiposVestimenta();
				equipo.setEstado("A");
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		/*
		*/
	}
	@Listen("onClick=#btnGrabar")
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						companiaDAO.getEntityManager().getTransaction().begin();
						equipo.setEstado("A");
						if(equipo.getIdEquipo() == null) {//es un nuevo
							companiaDAO.getEntityManager().persist(equipo);
						}else {//es modificacion
							companiaDAO.getEntityManager().merge(equipo);
						}
						companiaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						equiposLista.buscarEquipo("");
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	public boolean validarDatos() {
		if(txtDescripcion.getText() == "") {
			Clients.showNotification("Debe registrar la descripcion del equipo de vestimenta","info",txtDescripcion,"end_center",2000);
			txtDescripcion.setFocus(true);
			return false;
		}
		
		if(txtPrecio.getText() == "") {
			Clients.showNotification("Campo obligatorio: precio del equipo de vestimenta","info",txtPrecio,"end_center",2000);
			txtPrecio.setFocus(true);
			return false;
		}
		if(txtStock.getText() == "") {
			Clients.showNotification("Campo obligatorio: estock inicial del equipo de vestimenta","info",txtStock,"end_center",2000);
			txtStock.setFocus(true);
			return false;
		}
		return true;
	}
	@Listen("onClick=#btnSalir")
	public void salir(){
		winEquipoEditar.detach();
	}
	public EquiposVestimenta getEquipo() {
		return equipo;
	}
	public void setEquipo(EquiposVestimenta equipo) {
		this.equipo = equipo;
	}
	

}
