package com.bombero.controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.bombero.modelo.Evaluacion;
import com.bombero.modelo.EvaluacionDAO;
import com.bombero.modelo.Modulo;
import com.bombero.modelo.ModuloDAO;
import com.bombero.modelo.Periodo;
import com.bombero.modelo.PeriodoDAO;
import com.bombero.modelo.Persona;
import com.bombero.modelo.PersonaDAO;
import com.bombero.modelo.PersonalAutorizado;
import com.bombero.modelo.Pregunta;
import com.bombero.modelo.PreguntaDAO;
import com.bombero.modelo.Respuesta;
import com.bombero.modelo.RespuestaSeleccionada;
import com.bombero.modelo.ResultadosEvaluacion;
import com.bombero.modelo.Usuario;
import com.bombero.modelo.UsuarioDAO;
import com.bombero.util.Constantes;
import com.bombero.util.SecurityUtil;

public class ERendirEvaluacionC {
	@Wire Vlayout vlPreguntas;
	@Wire Label lblRealizado;
	@Wire Vlayout vlRealizado; 
	UsuarioDAO usuarioDAO = new UsuarioDAO();
	PeriodoDAO periodoDAO = new PeriodoDAO();
	PersonaDAO personaDAO = new PersonaDAO();
	ModuloDAO moduloDAO = new ModuloDAO();
	EvaluacionDAO evaluacionDAO = new EvaluacionDAO();
	PreguntaDAO preguntaDAO = new PreguntaDAO();

	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		//agregar el componente pregunta
		cargarPreguntas();
	}
	private void cargarPreguntas() {
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
						Integer i = 1;
						for(Pregunta pre : preguntasPresentar) {
							establecerPregunta(String.valueOf(pre.getIdPregunta()),i,pre.getPregunta(),pre.getRespuestas());
							i += 1;
						}
					}
				}
			}
		}
	}
	private void establecerPregunta(String idPregunta,Integer item, String pregunta, List<Respuesta> respuestas) {
		Groupbox grupoPregunta = new Groupbox();
		Radiogroup grupoRespuestas = new Radiogroup();
		grupoRespuestas.setId("grupo" + item);
		grupoRespuestas.setOrient("vertical");
		grupoRespuestas.setVisible(false);

		Vbox vRespuestas = new Vbox();
		vRespuestas.getChildren().add(grupoRespuestas);
		//numero de pregunta
		Label lblNoPregunta = new Label();
		lblNoPregunta.setValue("PREGUNTA NO " + item);
		vRespuestas.getChildren().add(lblNoPregunta);
		//pregunta
		Label lblPregunta = new Label();
		lblPregunta.setValue(pregunta);
		lblPregunta.setId(idPregunta);
		vRespuestas.getChildren().add(lblPregunta);
		//respuestas
		for(Respuesta res : respuestas) {
			if(res.getEstado().equals(Constantes.ESTADO_ACTIVO)) {
				Radio rRes = new Radio();
				rRes.setRadiogroup("grupo" + item);
				rRes.setId(String.valueOf(res.getIdRespuesta()));
				rRes.setLabel(res.getRespuesta());
				vRespuestas.getChildren().add(rRes);
			}
		}
		grupoPregunta.getChildren().add(vRespuestas);
		vlPreguntas.getChildren().add(grupoPregunta);
	}

	@Command
	public void enviarEvaluacion() {
		try {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						System.out.println("Listado de hijos preguntas" + vlPreguntas.getChildren().size());
						List<PreguntaRespuesta> listaRespuestas = new ArrayList<>();
						List<Integer> listaEvaluacion = new ArrayList<>();//para grabar el listado de las evaluaciones
						boolean bandera = false;//para validar si la evaluacion ya ha sido ingresado a la lista
						for(int i = 0 ; i < vlPreguntas.getChildren().size() ; i ++) {
							Groupbox grupo = (Groupbox) vlPreguntas.getChildren().get(i);
							System.out.println("Listado hijos grupo: " + grupo.getChildren().size());
							PreguntaRespuesta respuestaSeleccionada = new PreguntaRespuesta();
							for(int j = 0 ; j < grupo.getChildren().size() ; j ++) {
								Vbox vbox = (Vbox) grupo.getChildren().get(j);
								System.out.println("Listado hijos vbox: " + vbox.getChildren().size());
								for(int h = 0 ; h < vbox.getChildren().size() ; h ++) {
									//la primera posicion es un Radiogroup
									if(h == 0) {
									}else {
										//la segunda posicion es Label del No pregunta
										if(h == 1) {
										}else {
											//la tercera es Label de la pregunta
											if(h == 2) {
												Label pregunta = (Label) vbox.getChildren().get(h);
												System.out.println("Id Pregunta: " + pregunta.getId());
												System.out.println("Pregunta: " + pregunta.getValue());
												respuestaSeleccionada.setIdPregunta(Integer.parseInt(pregunta.getId()));
												List<Pregunta> lstPregunta = preguntaDAO.getPreguntaId(Integer.parseInt(pregunta.getId()));
												if(lstPregunta.size() > 0) {
													bandera = false;
													respuestaSeleccionada.setIdEvaluacion(lstPregunta.get(0).getEvaluacion().getIdEvaluacion());
													for(Integer ev : listaEvaluacion) {
														if(ev == lstPregunta.get(0).getEvaluacion().getIdEvaluacion())
															bandera = true;
													}
													if(bandera == false)//si la bandera sigue siendo falso es xq no la encontro en el listado, entonces se agrega
														listaEvaluacion.add(lstPregunta.get(0).getEvaluacion().getIdEvaluacion());
												}
											}else {
												//las demas posiciones son Radio de respuestas
												Radio respuesta = (Radio) vbox.getChildren().get(h);
												System.out.print("Id Respuesta: " + respuesta.getId() + " Respuesta: " + respuesta.getLabel());
												if(respuesta.isSelected()) {
													System.out.print(" <-- Seleccionada");
													respuestaSeleccionada.setIdSelecionada(Integer.parseInt(respuesta.getId()));
												}
												System.out.println("");
											}
										}
									}
								}
								listaRespuestas.add(respuestaSeleccionada);
							}
						}
						Integer total = 0;
						for(Integer ev : listaEvaluacion) {
							List<Evaluacion> evaluacion = evaluacionDAO.getEvaluacionById(ev);
							if(evaluacion.size() > 0) {
								System.out.println("Id: " + evaluacion.get(0).getIdEvaluacion() + " Evaluacion: " + evaluacion.get(0).getDescripcion());
								ResultadosEvaluacion resultados = new ResultadosEvaluacion();
								resultados.setEstado(Constantes.ESTADO_ACTIVO);
								//proceso para calcular las respuestas
								Integer suma = 0;
								for(PreguntaRespuesta res : listaRespuestas) {
									if(res.getIdEvaluacion() == ev) {
										System.out.println("Respuestas:  Id Ev: " + res.getIdEvaluacion() + " Preg: " + res.getIdPregunta() + " Res: " + res.getIdSelecionada());
										Integer califica = calificacion(res.getIdPregunta(),res.getIdSelecionada());
										System.out.println("Califica: " + califica);
										suma = suma + califica;
										total = total + califica;
									}
								}
								resultados.setCalificacion(suma);
								resultados.setEvaluacion(evaluacion.get(0));
								resultados.setIdResultado(null);
								//la persona es la que se ecuentra loggeada
								Usuario usuarios = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername());
								if(usuarios != null) {
									Persona personas = personaDAO.getPersonaId(usuarios.getIdPersona());
									if(personas != null){
										resultados.setPersona(personas);
									}
								}
								//luego se pone los reultados dependiendo de las preguntas
								List<RespuestaSeleccionada> seleccionados = new ArrayList<>();
								for(PreguntaRespuesta res : listaRespuestas) {
									if(res.getIdEvaluacion() == ev) {
										RespuestaSeleccionada sel = new RespuestaSeleccionada();//graba las respuestas seleccionadas
										sel.setEstado(Constantes.ESTADO_ACTIVO);
										sel.setIdPregunta(res.getIdPregunta());
										sel.setIdRespuesta(res.getIdSelecionada());
										sel.setIdRespuestaSeleccionada(null);
										sel.setResultadoEvaluacion(resultados);

										seleccionados.add(sel);
									}
								}
								resultados.setRespuestaSeleccionadas(seleccionados);
								evaluacionDAO.getEntityManager().getTransaction().begin();
								evaluacionDAO.getEntityManager().persist(resultados);
								evaluacionDAO.getEntityManager().getTransaction().commit();
							}
						}

						//15 es lo minimo para aprobar
						if(total >= 15) {
							Messagebox.show("Su calificacion es: " + total + "\nFelicitaciones! ud ha aprobado el curso");
							//grabar la persona el personal autrizado
							PersonalAutorizado personaAutorizada = new PersonalAutorizado();
							personaAutorizada.setIdPersonal(null);
							personaAutorizada.setEstado("A");
							Usuario usuarios = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername());
							if(usuarios != null) {
								Persona personas = personaDAO.getPersonaId(usuarios.getIdPersona());
								if(personas != null){
									personaAutorizada.setPersona(personas);
								}
							}
							personaAutorizada.setFechaIngreso(new Date());
							evaluacionDAO.getEntityManager().getTransaction().begin();
							evaluacionDAO.getEntityManager().persist(personaAutorizada);
							evaluacionDAO.getEntityManager().getTransaction().commit();
							vlRealizado.setVisible(true);
							vlPreguntas.setVisible(false);
						}else {
							Messagebox.show("Su calificacion es: " + total +  "\nLa calificación no le alcanzo para aprobar el curso, puede intentar en una proxima ocacion");
							vlRealizado.setVisible(true);
							vlPreguntas.setVisible(false);
						}

					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	private Integer calificacion(Integer idPregunta, Integer idRespuesta) {
		try {
			Integer cali = 0;
			List<Pregunta> lstPregunta = preguntaDAO.getPreguntaId(idPregunta);
			if(lstPregunta.size() > 0) {
				System.out.println("Tamanio de pregunta seleccionada " + lstPregunta.size());
				Pregunta pregunta = lstPregunta.get(0);
				System.out.println("Id pregunta seleccionada " + pregunta.getIdPregunta());
				
				for(Respuesta res : pregunta.getRespuestas()) {
					if(res.getEstado().equals(Constantes.ESTADO_ACTIVO)) {
						System.out.println("Opciones de respuestas " + res.getIdRespuesta() + " " + res.getRespuesta() + " " + res.getCorrecta());
						if(res.getCorrecta().equals(Constantes.RESPUESTA_CORRECTA)) {
							System.out.println("Corecta la respuesta " + res.getIdRespuesta() + " " + res.getRespuesta() + " id " + idRespuesta);
							if(idRespuesta.equals(res.getIdRespuesta()))
								cali = 1;
						}
					}
				}
				
			}
			System.out.println("Calificacion " + cali);
			return cali;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
			return 0;
		}
	}
	public class PreguntaRespuesta {
		private Integer idEvaluacion;
		private Integer idPregunta;
		private Integer idSelecionada;
		public Integer getIdPregunta() {
			return idPregunta;
		}
		public void setIdPregunta(Integer idPregunta) {
			this.idPregunta = idPregunta;
		}
		public Integer getIdSelecionada() {
			return idSelecionada;
		}
		public void setIdSelecionada(Integer idSelecionada) {
			this.idSelecionada = idSelecionada;
		}
		public Integer getIdEvaluacion() {
			return idEvaluacion;
		}
		public void setIdEvaluacion(Integer idEvaluacion) {
			this.idEvaluacion = idEvaluacion;
		}
	}
}
