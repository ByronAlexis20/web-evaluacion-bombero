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

import com.bombero.modelo.Compania;
import com.bombero.modelo.CompaniaDAO;
import com.bombero.util.ControllerHelper;


@SuppressWarnings("serial")
public class ACompaniaEditarC extends SelectorComposer<Component>{
	@Wire private Window winCompaniaEditar;
	@Wire private Textbox txtNombre;
	@Wire private Textbox txtDireccion;
	
	ControllerHelper helper = new ControllerHelper();
	CompaniaDAO companiaDAO = new CompaniaDAO();
	ACompaniaListaC companiaLista;
	Compania compania;
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		try {
			companiaLista = (ACompaniaListaC)Executions.getCurrent().getArg().get("VentanaPadre");
			compania = null;	
			if (Executions.getCurrent().getArg().get("Compania") != null) {
				compania = (Compania) Executions.getCurrent().getArg().get("Compania");
			}else {
				compania = new Compania();
				compania.setEstado("A");
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
						compania.setEstado("A");
						if(compania.getIdCompania() == null) {//es un nuevo
							companiaDAO.getEntityManager().persist(compania);
						}else {//es modificacion
							companiaDAO.getEntityManager().merge(compania);
						}
						companiaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						companiaLista.buscarCampania("");
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	public boolean validarDatos() {
		if(txtNombre.getText() == "") {
			Clients.showNotification("Debe registrar nombre de la compañía","info",txtNombre,"end_center",2000);
			txtNombre.setFocus(true);
			return false;
		}
		
		if(txtDireccion.getText() == "") {
			Clients.showNotification("Campo obligatorio: dirección","info",txtDireccion,"end_center",2000);
			txtDireccion.setFocus(true);
			return false;
		}
		return true;
	}
	@Listen("onClick=#btnSalir")
	public void salir(){
		winCompaniaEditar.detach();
	}
	public Compania getCompania() {
		return compania;
	}
	public void setCompania(Compania compania) {
		this.compania = compania;
	}

}
