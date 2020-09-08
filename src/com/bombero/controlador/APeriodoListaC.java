package com.bombero.controlador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.bombero.modelo.Periodo;
import com.bombero.modelo.PeriodoDAO;
import com.bombero.util.Constantes;

@SuppressWarnings("serial")
public class APeriodoListaC extends SelectorComposer<Component>{
	@Wire private Window winPeriodoLista;
	@Wire private Textbox txtBuscar;
	@Wire private Listbox lstPeriodo;
	@Wire private Button btnEvaluacion;
	
	PeriodoDAO periodoDAO = new PeriodoDAO();
	List<PeriodoPresentar> periodoLista;
	private PeriodoPresentar periodoSeleccionado;
	SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//usuario= new SegUsuario();
		buscarPeriodo("");
		btnEvaluacion.setDisabled(true);
		btnEvaluacion.setLabel(Constantes.ESTADO_EVALUACION_SIN_ACCION);
	}
	@Listen("onClick=#btnBuscar;onOK=#txtBuscar")
	public void buscar(){
		buscarPeriodo(txtBuscar.getText());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buscarPeriodo(String dato) {
		if (periodoLista != null)
			periodoLista = null; 
		List<Periodo> listado = periodoDAO.getListaPeriodos(dato);
		List<PeriodoPresentar> listaPresentar = new ArrayList<>();
		for(Periodo per : listado) {
			PeriodoPresentar perPre = new PeriodoPresentar();
			perPre.setEstadoEvaluacion(per.getEstadovaluacion());
			perPre.setEstadoPeriodo(per.getEstadoPeriodo());
			perPre.setFechaFin(formateador.format(per.getFechaFin()));
			perPre.setFechaInicio(formateador.format(per.getFechaInicio()));
			perPre.setIdPeriodo(per.getIdPeriodo());
			perPre.setDescripcion(per.getDescripcion());
			listaPresentar.add(perPre);
		}
		periodoLista = listaPresentar;
		lstPeriodo.setModel(new ListModelList(periodoLista));
		periodoSeleccionado = null;
	}
	
	@Listen("onSelect=#lstPeriodo")
	public void seleccionarPeriodo() {
		if(periodoSeleccionado != null) {
			List<Periodo> listado = periodoDAO.getPeriodoById(periodoSeleccionado.getIdPeriodo());
			Periodo perSel = new Periodo();
			if(listado.size() > 0)
				perSel = listado.get(0);
			if(perSel.getEstadovaluacion().equals(Constantes.ESTADO_EVALUACION_FINALIZADO)) {
				btnEvaluacion.setDisabled(true);
				btnEvaluacion.setLabel(Constantes.ESTADO_EVALUACION_SIN_ACCION);
			}else {
				if(perSel.getEstadovaluacion().equals(Constantes.ESTADO_EVALUACION_NO_INICIADO)) {
					btnEvaluacion.setDisabled(false);
					btnEvaluacion.setLabel(Constantes.ESTADO_EVALUACION_INICIAR_EVALUACION);
				}else {
					btnEvaluacion.setDisabled(false);
					btnEvaluacion.setLabel(Constantes.ESTADO_EVALUACION_FINALIZAR_EVALUACION);
				}
			}
		}
	}
	
	@Listen("onClick=#btnFinalizar")
	public void finalizarPeriodo(){
		if (periodoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un periodo");
			return; 
		}
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					List<Periodo> listado = periodoDAO.getPeriodoById(periodoSeleccionado.getIdPeriodo());
					Periodo perSel = new Periodo();
					if(listado.size() > 0)
						perSel = listado.get(0);
					periodoDAO.getEntityManager().getTransaction().begin();
					perSel.setEstadoPeriodo(Constantes.ESTADO_PERIODO_FINALIZADO);
					perSel.setEstadovaluacion(Constantes.ESTADO_EVALUACION_FINALIZADO);
					periodoDAO.getEntityManager().merge(perSel);
					periodoDAO.getEntityManager().getTransaction().commit();
					Messagebox.show("Periodo Finalizado con éxito");
					buscarPeriodo("");
				}
			}
		};
		Messagebox.show("¿Desea finalizar el periodo?", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
	}
	@Listen("onClick=#btnEvaluacion")
	public void periodoEvaluación(){
		if (periodoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un periodo");
			return; 
		}
		List<Periodo> listado = periodoDAO.getPeriodoById(periodoSeleccionado.getIdPeriodo());
		Periodo perSel = new Periodo();
		if(listado.size() > 0)
			perSel = listado.get(0);
		
		String mensaje = "";
		if(!perSel.getEstadovaluacion().equals(Constantes.ESTADO_EVALUACION_FINALIZADO)) {
			if(perSel.getEstadovaluacion().equals(Constantes.ESTADO_EVALUACION_NO_INICIADO)) {
				mensaje = "¿El periodo de evaluación se iniciara, No podrá modificar ni agregar más preguntas, Desea continuar?";
			}else {
				mensaje = "¿El periodo de evaluación se finalizara, Se cerrará el proceso de Evaluación, Desea continuar?";
			}
		}
		
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					List<Periodo> listado = periodoDAO.getPeriodoById(periodoSeleccionado.getIdPeriodo());
					Periodo perSel = new Periodo();
					if(listado.size() > 0)
						perSel = listado.get(0);
					periodoDAO.getEntityManager().getTransaction().begin();
					if(!perSel.getEstadovaluacion().equals(Constantes.ESTADO_EVALUACION_FINALIZADO)) {
						if(perSel.getEstadovaluacion().equals(Constantes.ESTADO_EVALUACION_NO_INICIADO)) {
							perSel.setEstadovaluacion(Constantes.ESTADO_EVALUACION_INICIADO);
							periodoDAO.getEntityManager().merge(perSel);
						}else {
							perSel.setEstadovaluacion(Constantes.ESTADO_EVALUACION_FINALIZADO);
							periodoDAO.getEntityManager().merge(perSel);
						}
					}
					periodoDAO.getEntityManager().getTransaction().commit();
					Messagebox.show("Proceso ejecutado con éxito");
					buscarPeriodo("");
					btnEvaluacion.setDisabled(true);
					btnEvaluacion.setLabel(Constantes.ESTADO_EVALUACION_SIN_ACCION);
				}
			}
		};
		Messagebox.show(mensaje, "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
	}
	@Listen("onClick=#btnNuevo")
	public void nuevo(){
		boolean bandera = false;
		for(Listitem item : lstPeriodo.getItems()) {
			PeriodoPresentar per = (PeriodoPresentar)item.getValue();
			if(per.getEstadoPeriodo().equals("EN PROCESO")) {
				bandera = true;
			}
		}
		if(bandera == true) {
			Messagebox.show("Existe un periodo EN PROCESO, se debe finalizar antes de crear uno nuevo");
			return;
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Periodo", null);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/APeriodoEditar.zul", winPeriodoLista, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditar")
	public void editar(){
		if (periodoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un periodo");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		//periodoDAO.getEntityManager().refresh(periodoSeleccionado);

		List<Periodo> listado = periodoDAO.getPeriodoById(periodoSeleccionado.getIdPeriodo());
		Periodo perSel = new Periodo();
		if(listado.size() > 0)
			perSel = listado.get(0);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Periodo", perSel);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/APeriodoEditar.zul", winPeriodoLista, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminar")
	public void eliminar(){
		if (periodoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un periodo");
			return; 
		}
		
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						List<Periodo> listado = periodoDAO.getPeriodoById(periodoSeleccionado.getIdPeriodo());
						Periodo perSel = new Periodo();
						if(listado.size() > 0)
							perSel = listado.get(0);
						perSel.setEstado("I");
						periodoDAO.getEntityManager().getTransaction().begin();
						periodoDAO.getEntityManager().merge(perSel);
						periodoDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						buscar();
					} catch (Exception e) {
						e.printStackTrace();
						periodoDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	public List<PeriodoPresentar> getPeriodoLista() {
		return periodoLista;
	}
	public void setPeriodoLista(List<PeriodoPresentar> periodoLista) {
		this.periodoLista = periodoLista;
	}
	public PeriodoPresentar getPeriodoSeleccionado() {
		return periodoSeleccionado;
	}
	public void setPeriodoSeleccionado(PeriodoPresentar periodoSeleccionado) {
		this.periodoSeleccionado = periodoSeleccionado;
	}
	
	public class PeriodoPresentar{
		private Integer idPeriodo;
		private String fechaInicio;
		private String fechaFin;
		private String estadoPeriodo;
		private String estadoEvaluacion;
		private String descripcion;
		public Integer getIdPeriodo() {
			return idPeriodo;
		}
		public void setIdPeriodo(Integer idPeriodo) {
			this.idPeriodo = idPeriodo;
		}
		public String getFechaInicio() {
			return fechaInicio;
		}
		public void setFechaInicio(String fechaInicio) {
			this.fechaInicio = fechaInicio;
		}
		public String getFechaFin() {
			return fechaFin;
		}
		public void setFechaFin(String fechaFin) {
			this.fechaFin = fechaFin;
		}
		public String getEstadoPeriodo() {
			return estadoPeriodo;
		}
		public void setEstadoPeriodo(String estadoPeriodo) {
			this.estadoPeriodo = estadoPeriodo;
		}
		public String getDescripcion() {
			return descripcion;
		}
		public void setDescripcion(String descripcion) {
			this.descripcion = descripcion;
		}
		public String getEstadoEvaluacion() {
			return estadoEvaluacion;
		}
		public void setEstadoEvaluacion(String estadoEvaluacion) {
			this.estadoEvaluacion = estadoEvaluacion;
		}
	}

}
