package com.bombero.controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.bombero.modelo.Evaluacion;
import com.bombero.modelo.EvaluacionDAO;
import com.bombero.modelo.Modulo;
import com.bombero.modelo.ModuloDAO;
import com.bombero.modelo.Periodo;
import com.bombero.modelo.PeriodoDAO;
import com.bombero.modelo.Persona;
import com.bombero.modelo.PersonaDAO;
import com.bombero.modelo.Pregunta;
import com.bombero.modelo.ResultadosEvaluacion;
import com.bombero.modelo.ResultadosEvaluacionDAO;
import com.bombero.modelo.Usuario;
import com.bombero.modelo.UsuarioDAO;
import com.bombero.util.Constantes;
import com.bombero.util.SecurityUtil;

public class EConfirmacionC {
	@Wire Include iPreguntas;
	@Wire Label lblPeriodo;
	@Wire Label lblCedula;
	@Wire Label lblEstudiante;
	@Wire Label lblObservacion;
	@Wire Label lblEtiquetaPregunta;
	@Wire Label lblCantidadPreguntas;
	@Wire Button btnIniciar;
	PeriodoDAO periodoDAO = new PeriodoDAO();
	UsuarioDAO usuarioDAO = new UsuarioDAO();
	PersonaDAO personaDAO = new PersonaDAO();
	EvaluacionDAO evaluacionDAO = new EvaluacionDAO();
	ResultadosEvaluacionDAO resultadoDAO = new ResultadosEvaluacionDAO();
	ModuloDAO moduloDAO = new ModuloDAO();
	Persona aspirante;
	//src="/formularios/evaluacion/ERendirEvaluacion.zul"
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		validarPeriodo();
		
	}
	
	private void validarPeriodo() {
		try {
			List<Periodo> periodo = periodoDAO.getPeriodoEvaluacionIniciado(Constantes.ESTADO_EVALUACION_INICIADO);
			if(periodo.size() > 0) {//tiene periodo en estado de evaluacion
				lblPeriodo.setValue("Evaluación final del periodo " + periodo.get(0).getDescripcion());
				lblPeriodo.setStyle("font-size:20px;font-weight:bold;color:#000000");
				//tambien preguntar por el usuario logeado a ver si es un estudiante
				Usuario usuarios = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername());
				if(usuarios != null) {
					// ahora preguntar si es estudiante
					if(usuarios.getIdPersona() != null) {//si es diferente de null es un estudiante
						//buscar la persona
						Persona personas = personaDAO.getPersonaId(usuarios.getIdPersona());
						if(personas != null) {//es una persona
							lblCedula.setVisible(true);
							lblEstudiante.setVisible(true);
							lblObservacion.setVisible(true);
							
							lblCedula.setValue("Cédula: " + personas.getCedula());
							lblEstudiante.setValue("Estudiante: " + personas.getNombre() + " " + personas.getApellido());
							//ahora buscar si se encuentra registrada en el periodo actual
							if(personas.getIdPeriodo() == periodo.get(0).getIdPeriodo()) {
								//validar si esa persona ya realizo la prueba
								List<ResultadosEvaluacion> resultado = resultadoDAO.getResultadosPersonas(personas.getIdPersona());
								if(resultado.size() > 0) {
									lblObservacion.setValue("Ud ya terminó de realizar la evaluación");
								}else {
									lblObservacion.setValue("Recuerde solo tiene una oportunidad para rendir la evaluación");
									btnIniciar.setVisible(true);
								}
							}else {
								lblObservacion.setValue("Estudiante no se encuentra matriculado en el periodo");
								lblObservacion.setStyle("font-size:15px;font-weight:bold;color:#000000");
							}
						}else {
							lblObservacion.setVisible(true);
							lblObservacion.setValue("Usuario logeado no es un estudiante");
							lblObservacion.setStyle("font-size:15px;font-weight:bold;color:#000000");
							lblCedula.setValue("");
							lblEstudiante.setValue("");
							
							lblCedula.setVisible(false);
							lblEstudiante.setVisible(false);
						}
					}
				}
			}else {
				lblPeriodo.setValue("No existe periodo de evaluación");
				lblPeriodo.setStyle("font-size:20px;font-weight:bold;color:#FF0000");
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	private void contarPreguntas() {
		List<Periodo> periodo = periodoDAO.getPeriodoEvaluacionIniciado(Constantes.ESTADO_EVALUACION_INICIADO);
		if(periodo.size() > 0) {
			Usuario usuarios = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername());
			if(usuarios != null) {
				Persona personas = personaDAO.getPersonaId(usuarios.getIdPersona());
				if(personas != null) {//es una persona
					if(personas.getIdPeriodo() == periodo.get(0).getIdPeriodo()) {//la persona si pertenece al periodo en estado de evaluacion
						//la lista nos sirve para guardar el listado de preguntas que se van a presentar para resolver
						List<Pregunta> preguntasPresentar = new ArrayList<>();
						//vamos a armar las preguntas
						List<Evaluacion> evaluaciones = evaluacionDAO.getBuscarPorPeriodo(personas.getIdPeriodo());//recuperamos la evaluacion del periodo
						//modulos registrados
						List<Modulo> modulos = moduloDAO.getListaModulo("");
						for(Modulo mod : modulos) {
							List<Pregunta> preguntasModulo = new ArrayList<>();
							for(Evaluacion ev : evaluaciones) {
								//por cada modulo hay una evaluacion
								if(ev.getModulo().getIdModulo() == mod.getIdModulo()) {// si son iguales.. quiero recuperar las preguntas que tienen
									for(Pregunta pre : ev.getPreguntas()) {
										if(pre.getEstado().equals(Constantes.ESTADO_ACTIVO)) {
											preguntasModulo.add(pre);
										}
									}
								}
							}
							//al finalizar el ciclo, tendre todas las preguntas del modulo
							//en el listado de preguntasModulo
							if(preguntasModulo.size() <= Constantes.NUMERO_PREGUNTAS) {
								//si el numero de preguntas es igual o menor que el numero de preguntas por modulo, presenta todas
								for(Pregunta pre : preguntasModulo) {
									preguntasPresentar.add(pre);
								}
							}else {//caso contrario se toma preguntas al ramdon
								//aqui se juega por posiciones
								List<Integer> itemsPreguntas = new ArrayList<>();//declaro un arreglo del item de la pregunta a grabar
								boolean bandera = false;
								Integer cantidadDisponible = preguntasModulo.size();//esta es la cantidad de preguntas disponibles
								Integer i = 0;//para recorrer y q se incremente cada vez que elije una pregunta, sirve para romper el ciclo
								while(true) {//hago un ciclo infinitp, se rompe cuando ya se seleccionan las cantidades de preguntas permitidas
									bandera = false;
									Random r = new Random();
									int numero = r.nextInt(cantidadDisponible);
									for(Integer num : itemsPreguntas) {
										if(num == numero)
											bandera = true;
									}
									if(bandera == false) {//si la bandera sigue siendo falso.. es xq no se encuentra en el listado
										itemsPreguntas.add(numero);
										i = i + 1;//incremento el contador
									}
									if(i == Constantes.NUMERO_PREGUNTAS) {
										break;//si ya se ha alcanzdo el numero de preguntas.. se rompe el ciclo
									}
								}
								//cuando se rompe el ciclo infinito, ya tenemos los items de las preguntas seleccionadas al random
								//recorremoe la lista de items
								for(Integer item : itemsPreguntas) {
									preguntasPresentar.add(preguntasModulo.get(item));//agrego a la lista el item perteneciente a la pregunta
								}
								
							}
						}
						//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
						//cuando ya se han seleccionado todas las preguntas, lo siguiente es presentar las preguntas por pantalla
						lblCantidadPreguntas.setValue(String.valueOf(preguntasPresentar.size()));
					}
				}
			}
		}
	}
	@Command
	public void iniciarEvaluacion() {
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					lblCantidadPreguntas.setVisible(true);
					lblEtiquetaPregunta.setVisible(true);
					btnIniciar.setDisabled(true);
					contarPreguntas();
					iPreguntas.setSrc("/formularios/evaluacion/ERendirEvaluacion.zul");
				}
			}
		};
		Messagebox.show("¿Desea realizar la evaluación? \nContinuar (Ok)", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
	}
	public Persona getAspirante() {
		return aspirante;
	}
	public void setAspirante(Persona aspirante) {
		this.aspirante = aspirante;
	}
	
}
