package com.bombero.controlador;

import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.bombero.modelo.Periodo;
import com.bombero.modelo.PeriodoDAO;
import com.bombero.util.Constantes;
import com.bombero.util.ControllerHelper;

@SuppressWarnings("serial")
public class APeriodoEditarC extends SelectorComposer<Component>{
	@Wire private Window winPeriodoEditar;
	@Wire private Textbox txtDescripcion;
	@Wire private Datebox dtpFechaInicio;
	@Wire private Datebox dtpFechaFin;
	
	ControllerHelper helper = new ControllerHelper();
	PeriodoDAO periodoDAO = new PeriodoDAO();
	APeriodoListaC periodoLista;
	Periodo periodo;
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		dtpFechaFin.setValue(new Date());
		dtpFechaInicio.setValue(new Date());
		try {
			periodoLista = (APeriodoListaC)Executions.getCurrent().getArg().get("VentanaPadre");
			periodo = null;	
			if (Executions.getCurrent().getArg().get("Periodo") != null) {
				periodo = (Periodo) Executions.getCurrent().getArg().get("Periodo");
			}else {
				periodo = new Periodo();
				periodo.setEstado("A");
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
						
						periodoDAO.getEntityManager().getTransaction().begin();
						periodo.setEstado(Constantes.ESTADO_ACTIVO);
						periodo.setFechaFin(dtpFechaFin.getValue());
						periodo.setFechaInicio(dtpFechaInicio.getValue());
						if(periodo.getIdPeriodo() == null) {//es un nuevo
							periodo.setEstadoPeriodo(Constantes.ESTADO_PERIODO_EN_PROCESO);
							periodo.setEstadovaluacion(Constantes.ESTADO_EVALUACION_NO_INICIADO);
							periodoDAO.getEntityManager().persist(periodo);
						}else {//es modificacion
							periodoDAO.getEntityManager().merge(periodo);
						}
						periodoDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						periodoLista.buscarPeriodo("");
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
			Clients.showNotification("Debe registrar descripción del periodo","info",txtDescripcion,"end_center",2000);
			txtDescripcion.setFocus(true);
			return false;
		}
		return true;
	}
	@Listen("onClick=#btnSalir")
	public void salir(){
		winPeriodoEditar.detach();
	}
	public Periodo getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
	}

}
