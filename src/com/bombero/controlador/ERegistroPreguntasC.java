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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.bombero.modelo.Evaluacion;
import com.bombero.modelo.EvaluacionDAO;
import com.bombero.modelo.Modulo;
import com.bombero.modelo.ModuloDAO;
import com.bombero.modelo.Periodo;
import com.bombero.modelo.PeriodoDAO;
import com.bombero.modelo.Pregunta;
import com.bombero.modelo.PreguntaDAO;
import com.bombero.modelo.Respuesta;
import com.bombero.util.Constantes;

@SuppressWarnings("serial")
public class ERegistroPreguntasC extends SelectorComposer<Component>{
	@Wire private Window winRegistroLista;
	@Wire private Listbox lstPeriodo;
	@Wire private Listbox lstModulos;
	@Wire private Listbox lstPreguntas;
	@Wire private Label lblPeriodoSeleccionado;
	@Wire private Label lblDuracion;
	@Wire private Label lblEstado;
	List<PeriodoPresentar> periodoLista;
	List<PreguntaPresentar> preguntaLista;
	PreguntaPresentar preguntaSeleccionada;
	private Modulo moduloSeleccionado;
	private PeriodoPresentar periodoSeleccionado;
	SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
	PeriodoDAO periodoDAO = new PeriodoDAO();
	ModuloDAO moduloDAO = new ModuloDAO();
	PreguntaDAO preguntaDAO = new PreguntaDAO();
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//usuario= new SegUsuario();
		buscarPeriodo("");
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void cargarModulos() {
		List<Modulo> listado = moduloDAO.getListaModulo("");
		System.out.println(listado.size());
		lstModulos.setModel(new ListModelList(listado));
		moduloSeleccionado = null;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buscarPeriodo(String dato) {
		if (periodoLista != null)
			periodoLista = null; 
		List<Periodo> listado = periodoDAO.getListaPeriodos(dato);
		List<PeriodoPresentar> listaPresentar = new ArrayList<>();
		for(Periodo per : listado) {
			PeriodoPresentar perPre = new PeriodoPresentar();
			perPre.setEstadoPeriodo(per.getEstadoPeriodo());
			perPre.setFechaFin(formateador.format(per.getFechaFin()));
			perPre.setFechaInicio(formateador.format(per.getFechaInicio()));
			perPre.setIdPeriodo(per.getIdPeriodo());
			perPre.setDescripcion(per.getDescripcion());
			perPre.setPresentacion(per.getDescripcion() + " Fecha Inicio: " + formateador.format(per.getFechaInicio()) + " Fecha Fin: " + formateador.format(per.getFechaFin()));
			listaPresentar.add(perPre);
		}
		periodoLista = listaPresentar;
		lstPeriodo.setModel(new ListModelList(periodoLista));
		periodoSeleccionado = null;
	}
	
	@Listen("onSelect=#lstPeriodo")
	public void seleccionarPeriodo() {
		try {
			if(periodoSeleccionado == null) {
				return;
			}
			lblPeriodoSeleccionado.setValue(periodoSeleccionado.getDescripcion());
			lblDuracion.setValue("Inicio: " + periodoSeleccionado.getFechaInicio() + " - Fin: " + periodoSeleccionado.getFechaFin());
			lblEstado.setValue(periodoSeleccionado.getEstadoPeriodo());
			cargarModulos();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Listen("onSelect=#lstModulos")
	public void cargarPreguntas() {
		try {
			EvaluacionDAO evaluacionDAO = new EvaluacionDAO();
			preguntaSeleccionada = null;
			if(moduloSeleccionado == null) {
				return;
			}
			if (preguntaLista != null)
				preguntaLista = null; 
			System.out.println("Modulo seleccionado: " + moduloSeleccionado.getModulo());
			List<Evaluacion> listaEvaluacion = evaluacionDAO.getListaEvaluacion(periodoSeleccionado.getIdPeriodo(), moduloSeleccionado.getIdModulo());
			List<PreguntaPresentar> listaPreguntas = new ArrayList<>();
			for(Evaluacion eval : listaEvaluacion) {
				//preguntas
				for(Pregunta pre : eval.getPreguntas()) {
					if(pre.getEstado().equals(Constantes.ESTADO_ACTIVO)) {
						PreguntaPresentar pregunta = new PreguntaPresentar();
						pregunta.setIdEvaluacion(eval.getIdEvaluacion());
						pregunta.setIdPregunta(pre.getIdPregunta());
						pregunta.setPregunta(pre.getPregunta());
						//respuestas
						for(Respuesta res : pre.getRespuestas()) {
							if(res.getEstado().equals(Constantes.ESTADO_ACTIVO)) {
								if(res.getCorrecta().equals(Constantes.RESPUESTA_CORRECTA)) {
									pregunta.setRespuesta(res.getRespuesta());
								}
							}
						}
						//agregar al listado
						listaPreguntas.add(pregunta);
					}
				}
			}
			System.out.println(listaPreguntas.size());
			preguntaLista = listaPreguntas;
			System.out.println("Preguntas ----");
			for(PreguntaPresentar pre : preguntaLista) {
				System.out.println(pre.getPregunta());
			}
			lstPreguntas.setModel(new ListModelList(preguntaLista));
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	@Listen("onClick=#btnNuevoPregunta")
	public void nuevaPregunta() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ID_PERIODO", periodoSeleccionado.getIdPeriodo());
		params.put("ID_MODULO", moduloSeleccionado.getIdModulo());
		params.put("ID_PREGUNTA", null);
		params.put("VENTANA", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/evaluacion/EPreguntaEditar.zul", winRegistroLista, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditarPregunta")
	public void editarPregunta() {
		if(preguntaSeleccionada == null) {
			Clients.showNotification("Debe seleccionar Una pregunta");
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ID_PERIODO", periodoSeleccionado.getIdPeriodo());
		params.put("ID_MODULO", moduloSeleccionado.getIdModulo());
		params.put("ID_PREGUNTA", preguntaSeleccionada.getIdPregunta());
		params.put("VENTANA", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/evaluacion/EPreguntaEditar.zul", winRegistroLista, params);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminarPregunta")
	public void eliminarPregunta(){
		if(preguntaSeleccionada == null) {
			Clients.showNotification("Debe seleccionar Una pregunta");
			return;
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						List<Pregunta> listaPregunta = preguntaDAO.getPreguntaId(preguntaSeleccionada.getIdPregunta());
						if(listaPregunta.size() > 0) {
							Pregunta seleccionado = listaPregunta.get(0);
							seleccionado.setEstado("I");
							preguntaDAO.getEntityManager().getTransaction().begin();
							preguntaDAO.getEntityManager().merge(seleccionado);
							preguntaDAO.getEntityManager().getTransaction().commit();
							Clients.showNotification("Pregunta eliminada");
							cargarPreguntas();
						}
					} catch (Exception e) {
						e.printStackTrace();
						preguntaDAO.getEntityManager().getTransaction().rollback();
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
	public Modulo getModuloSeleccionado() {
		return moduloSeleccionado;
	}
	public void setModuloSeleccionado(Modulo moduloSeleccionado) {
		this.moduloSeleccionado = moduloSeleccionado;
	}
	public PeriodoPresentar getPeriodoSeleccionado() {
		return periodoSeleccionado;
	}
	public void setPeriodoSeleccionado(PeriodoPresentar periodoSeleccionado) {
		this.periodoSeleccionado = periodoSeleccionado;
	}
	public PreguntaPresentar getPreguntaSeleccionada() {
		return preguntaSeleccionada;
	}
	public void setPreguntaSeleccionada(PreguntaPresentar preguntaSeleccionada) {
		this.preguntaSeleccionada = preguntaSeleccionada;
	}
	public List<PreguntaPresentar> getPreguntaLista() {
		return preguntaLista;
	}
	public void setPreguntaLista(List<PreguntaPresentar> preguntaLista) {
		this.preguntaLista = preguntaLista;
	}


	public class PeriodoPresentar{
		private Integer idPeriodo;
		private String fechaInicio;
		private String fechaFin;
		private String estadoPeriodo;
		private String descripcion;
		private String presentacion;
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
		public String getPresentacion() {
			return presentacion;
		}
		public void setPresentacion(String presentacion) {
			this.presentacion = presentacion;
		}
	}
	public class PreguntaPresentar {
		private Integer idPregunta;
		private Integer idEvaluacion;
		private String pregunta;
		private Integer idRespuesta;
		private String respuesta;
		public Integer getIdPregunta() {
			return idPregunta;
		}
		public void setIdPregunta(Integer idPregunta) {
			this.idPregunta = idPregunta;
		}
		public String getPregunta() {
			return pregunta;
		}
		public void setPregunta(String pregunta) {
			this.pregunta = pregunta;
		}
		public Integer getIdRespuesta() {
			return idRespuesta;
		}
		public void setIdRespuesta(Integer idRespuesta) {
			this.idRespuesta = idRespuesta;
		}
		public String getRespuesta() {
			return respuesta;
		}
		public void setRespuesta(String respuesta) {
			this.respuesta = respuesta;
		}
		public Integer getIdEvaluacion() {
			return idEvaluacion;
		}
		public void setIdEvaluacion(Integer idEvaluacion) {
			this.idEvaluacion = idEvaluacion;
		}
		
	}
}
