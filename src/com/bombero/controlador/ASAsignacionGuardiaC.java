package com.bombero.controlador;

import java.io.IOException;
import java.util.ArrayList;
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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.bombero.modelo.AsignarGuardia;
import com.bombero.modelo.AsignarGuardiaDAO;
import com.bombero.modelo.Compania;
import com.bombero.modelo.CompaniaDAO;
import com.bombero.util.Constantes;

public class ASAsignacionGuardiaC {
	@Wire Window winAsignacionGuardia;
	@Wire Listbox lstGuardias;
	CompaniaDAO companiaDAO = new CompaniaDAO();
	AsignarGuardiaDAO guardiaDAO = new AsignarGuardiaDAO();
	
	Compania companiaSeleccionada;
	AsignarGuardia guardiaSeleccionado;
	List<AsignarGuardia> guardiaLista;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void seleccionarGuardias() {
		try {
			System.out.println("seleccionar guardia");
			if(companiaSeleccionada == null) {
				Clients.showNotification("Debe seleccionar una compañía");
				List<AsignarGuardia> lista = new ArrayList<>();
				lstGuardias.setModel(new ListModelList(lista));
				return;
			}
			List<AsignarGuardia> lista = guardiaDAO.getListaGuardias(companiaSeleccionada.getIdCompania());
			guardiaLista = lista;
			lstGuardias.setModel(new ListModelList(lista));
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void agregarGuardia() {
		try {
			System.out.println("asignar guardia");
			if(companiaSeleccionada == null) {
				Clients.showNotification("Debe seleccionar una Compañía!!!!!",false);
				return;
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("VENTANA", this);
			params.put("COMPANIA", companiaSeleccionada);
			Window ventanaCargar = (Window) Executions.createComponents("/formularios/asignaciones/ASRealizarAsignacion.zul", winAsignacionGuardia, params);
			ventanaCargar.doModal();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void quitarGuardia() {
		try {
			System.out.println("quitar guardia");
			if(guardiaSeleccionado == null) {
				Clients.showNotification("Debe seleccionar un guardia a eliminar");
				return;
			}
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						guardiaSeleccionado.setEstado(Constantes.ESTADO_INACTIVO);
						guardiaDAO.getEntityManager().getTransaction().begin();
						guardiaDAO.getEntityManager().merge(guardiaSeleccionado);
						guardiaDAO.getEntityManager().getTransaction().commit();
						seleccionarGuardias();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	public List<Compania> getCompanias(){
		try {
			List<Compania> lista = companiaDAO.getListaCompania("");
			return lista;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
	}
	public Compania getCompaniaSeleccionada() {
		return companiaSeleccionada;
	}
	public void setCompaniaSeleccionada(Compania companiaSeleccionada) {
		this.companiaSeleccionada = companiaSeleccionada;
	}
	public AsignarGuardia getGuardiaSeleccionado() {
		return guardiaSeleccionado;
	}
	public void setGuardiaSeleccionado(AsignarGuardia guardiaSeleccionado) {
		this.guardiaSeleccionado = guardiaSeleccionado;
	}
	public List<AsignarGuardia> getGuardiaLista() {
		return guardiaLista;
	}
	public void setGuardiaLista(List<AsignarGuardia> guardiaLista) {
		this.guardiaLista = guardiaLista;
	}
	
}