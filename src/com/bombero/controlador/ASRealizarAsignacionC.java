package com.bombero.controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.bombero.modelo.AsignarGuardia;
import com.bombero.modelo.AsignarGuardiaDAO;
import com.bombero.modelo.Compania;
import com.bombero.modelo.PersonalAutorizado;
import com.bombero.modelo.PersonalAutorizadoDAO;
import com.bombero.util.Constantes;

public class ASRealizarAsignacionC {
	@Wire Window winRealizarAsignacion;
	@Wire Datebox dtpFechaInicio;
	@Wire Datebox dtpFechaFin;
	
	@Wire Label lblCompania;
	private Compania compania;
	ASAsignacionGuardiaC asignacionC;
	private PersonalAutorizado bomberoSeleccionado;
	AsignarGuardiaDAO guardiaDAO = new AsignarGuardiaDAO();
	PersonalAutorizadoDAO personalAutorizadoDAO = new PersonalAutorizadoDAO();
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		compania = (Compania)Executions.getCurrent().getArg().get("COMPANIA");
		asignacionC = (ASAsignacionGuardiaC)Executions.getCurrent().getArg().get("VENTANA");
		if(compania != null) {
			lblCompania.setValue(compania.getNombre());
		}
	}
	@Command
	public void grabar() {
		try {
			if(bomberoSeleccionado == null) {
				Clients.showNotification("Debe seleccionar un bombero");
				return;
			}
			if(dtpFechaInicio.getValue() == null) {
				Clients.showNotification("Debe seleccionar fecha de inicio");
				return;
			}
			if(dtpFechaFin.getValue() == null) {
				Clients.showNotification("Debe seleccionar fecha de finalización");
				return;
			}
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						AsignarGuardia guardia = new AsignarGuardia();
						guardia.setCompania(compania);
						guardia.setEstado(Constantes.ESTADO_ACTIVO);
						guardia.setIdGuardia(null);
						guardia.setObservacion("");
						guardia.setFechaInicio(dtpFechaInicio.getValue());
						guardia.setFechaFin(dtpFechaFin.getValue());
						guardia.setPersonalAutorizado(bomberoSeleccionado);
						
						guardiaDAO.getEntityManager().getTransaction().begin();
						guardiaDAO.getEntityManager().merge(guardia);
						guardiaDAO.getEntityManager().getTransaction().commit();
						asignacionC.seleccionarGuardias();
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void salir() {
		try {
			winRealizarAsignacion.detach();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	public PersonalAutorizado getBomberoSeleccionado() {
		return bomberoSeleccionado;
	}
	public void setBomberoSeleccionado(PersonalAutorizado bomberoSeleccionado) {
		this.bomberoSeleccionado = bomberoSeleccionado;
	}
	public List<PersonalAutorizado> getListaBomberos() {
		List<PersonalAutorizado> personal = personalAutorizadoDAO.getPersonalAutorizado();
		List<PersonalAutorizado> listaAgregar = new ArrayList<>();
		List<AsignarGuardia> asignaciones = guardiaDAO.getListaAsignaciones();
		boolean bandera = false;
		for(PersonalAutorizado per : personal) {
			bandera = false;
			for(AsignarGuardia asig : asignaciones) {
				if(per.getIdPersonal() == asig.getPersonalAutorizado().getIdPersonal())
					bandera = true;
			}
			if(bandera == false)
				listaAgregar.add(per);
		}
		return listaAgregar;
	}

}
