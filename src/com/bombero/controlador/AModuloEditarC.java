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

import com.bombero.modelo.Modulo;
import com.bombero.modelo.ModuloDAO;
import com.bombero.util.ControllerHelper;

@SuppressWarnings("serial")
public class AModuloEditarC extends SelectorComposer<Component>{
	@Wire private Window winModuloEditar;
	@Wire private Textbox txtModulo;
	
	ControllerHelper helper = new ControllerHelper();
	ModuloDAO moduloDAO = new ModuloDAO();
	AModuloListaC moduloLista;
	Modulo modulo;
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		try {
			moduloLista = (AModuloListaC)Executions.getCurrent().getArg().get("VentanaPadre");
			modulo = null;	
			if (Executions.getCurrent().getArg().get("Modulo") != null) {
				modulo = (Modulo) Executions.getCurrent().getArg().get("Modulo");
			}else {
				modulo = new Modulo();
				modulo.setEstado("A");
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
						moduloDAO.getEntityManager().getTransaction().begin();
						modulo.setEstado("A");
						if(modulo.getIdModulo() == null) {//es un nuevo
							moduloDAO.getEntityManager().persist(modulo);
						}else {//es modificacion
							moduloDAO.getEntityManager().merge(modulo);
						}
						moduloDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						moduloLista.buscarModulo("");
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	public boolean validarDatos() {
		if(txtModulo.getText() == "") {
			Clients.showNotification("Debe registrar nombre del módulo","info",txtModulo,"end_center",2000);
			txtModulo.setFocus(true);
			return false;
		}
		
		return true;
	}
	@Listen("onClick=#btnSalir")
	public void salir(){
		winModuloEditar.detach();
	}
	public Modulo getModulo() {
		return modulo;
	}
	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}
	

}
