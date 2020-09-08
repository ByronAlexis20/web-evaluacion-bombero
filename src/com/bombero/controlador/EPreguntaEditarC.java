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
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
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
import com.bombero.modelo.RespuestaDAO;
import com.bombero.util.Constantes;


public class EPreguntaEditarC{
	@Wire Listbox lstRespuestas;
	@Wire Textbox txtPregunta;
	@Wire Window winPregunta;
	@Wire Button btnEditar;
	@Wire Button btnGrabarPregunta;
	@Wire Button btnAgregar;
	
	Evaluacion evaluacion;
	Pregunta pregunta;
	EvaluacionDAO evaluacionDAO = new EvaluacionDAO();
	PreguntaDAO preguntaDAO = new PreguntaDAO();
	PeriodoDAO periodoDAO = new PeriodoDAO();
	ModuloDAO moduloDAO = new ModuloDAO();
	ERegistroPreguntasC registroPregunta;
	Respuesta respuestaSeleccionada;
	List<Respuesta> listaRespuestas;
	
	
	Periodo periodo;
	Modulo modulo;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		//bloquear los botones para grabar pregunta
		btnEditar.setDisabled(true);btnGrabarPregunta.setDisabled(true);
		Integer idPeriodo = 0;
		Integer idModulo = 0;
		Integer idPregunta = 0;
		registroPregunta = (ERegistroPreguntasC)Executions.getCurrent().getArg().get("VENTANA");
		if (Executions.getCurrent().getArg().get("ID_PERIODO") != null) {
			idPeriodo = (Integer) Executions.getCurrent().getArg().get("ID_PERIODO");
		}
		if (Executions.getCurrent().getArg().get("ID_MODULO") != null) {
			idModulo = (Integer) Executions.getCurrent().getArg().get("ID_MODULO");
		}
		if (Executions.getCurrent().getArg().get("ID_PREGUNTA") != null) {
			idPregunta = (Integer) Executions.getCurrent().getArg().get("ID_PREGUNTA");
		}
		List<Periodo> listaPeriodo = periodoDAO.getPeriodoById(idPeriodo);
		if(listaPeriodo.size() > 0)
			periodo = listaPeriodo.get(0);
		List<Modulo> listaModulo = moduloDAO.getListaModuloById(idModulo);
		if(listaModulo.size() > 0)
			modulo = listaModulo.get(0);
		
		
		List<Evaluacion> listaEvaluacion = evaluacionDAO.getListaEvaluacion(idPeriodo, idModulo);
		if(listaEvaluacion.size() > 0)
			evaluacion = listaEvaluacion.get(0);
		else {
			evaluacion = new Evaluacion();
			evaluacion.setIdEvaluacion(null);
		}
		if(evaluacion.getIdEvaluacion() != null) {
			if(idPregunta != 0) {
				List<Pregunta> listaPregunta = preguntaDAO.getPreguntaId(idPregunta);
				if(listaPregunta.size() > 0) {
					pregunta = listaPregunta.get(0);
					txtPregunta.setText(pregunta.getPregunta());
					btnEditar.setDisabled(false);
					txtPregunta.setDisabled(true);//se bloquea el cuadro de texto de la pregunta para cuando presione el boton editar se desbloquee
					recuperarRespuestas(pregunta);
				}
				else {
					pregunta = new Pregunta();
					pregunta.setIdPregunta(null);
					btnEditar.setDisabled(true);
					txtPregunta.setDisabled(false);
					btnGrabarPregunta.setDisabled(false);
					btnAgregar.setDisabled(true);
					
				}
			}else {
				pregunta = new Pregunta();
				pregunta.setIdPregunta(null);
				btnEditar.setDisabled(true);
				txtPregunta.setDisabled(false);
				btnGrabarPregunta.setDisabled(false);
				btnAgregar.setDisabled(true);
			}
		}else {
			pregunta = new Pregunta();
			pregunta.setIdPregunta(null);
			btnEditar.setDisabled(true);
			txtPregunta.setDisabled(false);
			btnGrabarPregunta.setDisabled(false);
			btnAgregar.setDisabled(true);
		}
	}
	
	@Command
	public void editar() {
		btnEditar.setDisabled(true);
		txtPregunta.setDisabled(false);
		btnGrabarPregunta.setDisabled(false);
	}
	
	@Command
	public void grabarPregunta() {
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					if(pregunta.getIdPregunta() != null) {//si es diferente de null, es una pregunta recuperada, solo se modifica
						pregunta.setPregunta(txtPregunta.getText());
						preguntaDAO.getEntityManager().getTransaction().begin();
						preguntaDAO.getEntityManager().merge(pregunta);
						preguntaDAO.getEntityManager().getTransaction().commit();
					}else {//caso contrario es una pregunta nueva
						//cuando es una pregunta nueva hay que preguntar primero si tiene una evaluacion
						if(evaluacion.getIdEvaluacion() != null) {//si tiene una evaluacion
							//si tiene evaluacion hay que preguntar si tiene preguntas 
							pregunta.setEstado("A");
							pregunta.setIdPregunta(null);
							pregunta.setPregunta(txtPregunta.getText());
							pregunta.setEvaluacion(evaluacion);
							if(evaluacion.getPreguntas().size() > 0) {//si es mayor a cero tiene preguntas entre sus registros
								evaluacion.addPregunta(pregunta);
							}else {//no tiene preguntas y se crea una lista
								List<Pregunta> listaPreg = new ArrayList<>();
								listaPreg.add(pregunta);
								evaluacion.setPreguntas(listaPreg);
							}
							evaluacionDAO.getEntityManager().getTransaction().begin();
							evaluacionDAO.getEntityManager().merge(evaluacion);
							evaluacionDAO.getEntityManager().getTransaction().commit();
							//recuperar la evaluacion por id ya que si esta recuperada
							List<Evaluacion> listaEvaluacion = evaluacionDAO.getEvaluacionById(evaluacion.getIdEvaluacion());
							if(listaEvaluacion.size() > 0) {
								evaluacion = listaEvaluacion.get(0);
							}
							//de igual manera se recupera la pregunta
							List<Pregunta> listaPregunta = preguntaDAO.getPreguntaUltima();
							if(listaPregunta.size() > 0) {
								pregunta = listaPregunta.get(0);
							}
						}else {//no tiene evaluacion
							evaluacion.setEstado("A");
							evaluacion.setDescripcion("Evaluacion del periodo " + periodo.getDescripcion() + " Modulo " + modulo.getModulo());
							evaluacion.setModulo(modulo);
							evaluacion.setPeriodo(periodo);
							evaluacion.setIdEvaluacion(null);
							
							pregunta.setEstado("A");
							pregunta.setIdPregunta(null);
							pregunta.setPregunta(txtPregunta.getText());
							pregunta.setEvaluacion(evaluacion);
							
							List<Pregunta> listaPreg = new ArrayList<>();
							listaPreg.add(pregunta);
							evaluacion.setPreguntas(listaPreg);
							
							evaluacionDAO.getEntityManager().getTransaction().begin();
							evaluacionDAO.getEntityManager().persist(evaluacion);
							evaluacionDAO.getEntityManager().getTransaction().commit();
							
							//recuperar la ultima evaluacion para que tenga todo los datos
							List<Evaluacion> listaEvaluacion = evaluacionDAO.getEvaluacionUltimo();
							if(listaEvaluacion.size() > 0) {
								evaluacion = listaEvaluacion.get(0);
							}
							//de igual manera se recupera la pregunta
							List<Pregunta> listaPregunta = preguntaDAO.getPreguntaUltima();
							if(listaPregunta.size() > 0) {
								pregunta = listaPregunta.get(0);
							}
						}
					}
					Clients.showNotification("Datos Grabados con exito");
					btnEditar.setDisabled(false);
					btnGrabarPregunta.setDisabled(true);
					txtPregunta.setDisabled(true);		
					btnAgregar.setDisabled(false);
				}
			}
		};
		Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void recuperarRespuestas(Pregunta pre) {
		try {
			RespuestaDAO respuestaDAO = new RespuestaDAO();
			List<Respuesta> listaRespuestasPregunta = respuestaDAO.getRespuestaByPregunta(pre.getIdPregunta());
			listaRespuestas = listaRespuestasPregunta;
			lstRespuestas.setModel(new ListModelList(listaRespuestas));
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void agregarRespuesta(){
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("VENTANA", this);
			params.put("PREGUNTA", pregunta);
			Window ventanaCargar = (Window) Executions.createComponents("/formularios/evaluacion/ERespuestas.zul", winPregunta, params);
			ventanaCargar.doModal();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void agregar(String respuesta, String correcta) {
		try {
			System.out.println("Respuesta: " + respuesta + " Correcto: " + correcta);
			Respuesta res = new Respuesta();
			res.setCorrecta(correcta);
			res.setIdRespuesta(0);
			res.setPregunta(pregunta);
			res.setEstado("A");
			res.setRespuesta(respuesta);
			List<Respuesta> lista = new ArrayList<>();
			for(Listitem item : lstRespuestas.getItems()) {
				Respuesta resp = (Respuesta)item.getValue();
				System.out.println("respuestas: " + resp.getRespuesta());
				lista.add(resp);
			}
			lista.add(res);
			System.out.println("Agregadas");
			for(Respuesta preg : lista) {
				System.out.println(preg.getRespuesta() + " id " + preg.getIdRespuesta());
			}
			lstRespuestas.getItems().clear();
			listaRespuestas = lista;
			System.out.println(lista.size());
			lstRespuestas.setModel(new ListModelList(lista));
			
			
			
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	@Command
	public void salir(){
		boolean bandera = false;
		for(Listitem item : lstRespuestas.getItems()) {
			Respuesta res = (Respuesta)item.getValue();
			if(res.getCorrecta().equals(Constantes.RESPUESTA_CORRECTA))
				bandera = true;
		}
		if(bandera == false) {
			Messagebox.show("Debe registrar la respuesta correcta");
		}
		registroPregunta.cargarPreguntas();
		winPregunta.detach();
	}
	@Command
	public void quitarRespuesta() {
		try {
			if(respuestaSeleccionada == null) {
				Messagebox.show("Debe seleccionar una respuesta a eliminar");
				return;
			}
			RespuestaDAO respuestaDAO = new RespuestaDAO();
			for(Respuesta item : listaRespuestas) {
				System.out.println(item.toString());
			}
			System.out.println("Seleccionada"); System.out.println("selecionada a: " + respuestaSeleccionada.toString());
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						if(respuestaSeleccionada.getIdRespuesta() != null) {
							respuestaSeleccionada.setEstado(Constantes.ESTADO_INACTIVO);
							respuestaDAO.getEntityManager().getTransaction().begin();
							respuestaDAO.getEntityManager().merge(respuestaSeleccionada);
							respuestaDAO.getEntityManager().getTransaction().commit();
						}
						respuestaSeleccionada = null;
						recuperarRespuestas(pregunta);
						Clients.showNotification("Transaccion ejecutada con exito.");
					}
				}
			};
			Messagebox.show("¿Seguro desea quitar la respuesta?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
			
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	public Evaluacion getEvaluacion() {
		return evaluacion;
	}
	public void setEvaluacion(Evaluacion evaluacion) {
		this.evaluacion = evaluacion;
	}
	public Pregunta getPregunta() {
		return pregunta;
	}
	public void setPregunta(Pregunta pregunta) {
		this.pregunta = pregunta;
	}
	public ERegistroPreguntasC getRegistroPregunta() {
		return registroPregunta;
	}
	public void setRegistroPregunta(ERegistroPreguntasC registroPregunta) {
		this.registroPregunta = registroPregunta;
	}

	public Respuesta getRespuestaSeleccionada() {
		return respuestaSeleccionada;
	}

	public void setRespuestaSeleccionada(Respuesta respuestaSeleccionada) {
		this.respuestaSeleccionada = respuestaSeleccionada;
	}
	public List<Respuesta> getListaRespuestas() {
		return listaRespuestas;
	}
	public void setListaRespuestas(List<Respuesta> listaRespuestas) {
		this.listaRespuestas = listaRespuestas;
	}
	
	
}
