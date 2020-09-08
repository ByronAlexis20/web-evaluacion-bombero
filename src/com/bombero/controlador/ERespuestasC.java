package com.bombero.controlador;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.bombero.modelo.Pregunta;
import com.bombero.modelo.PreguntaDAO;
import com.bombero.modelo.Respuesta;
import com.bombero.modelo.RespuestaDAO;
import com.bombero.util.Constantes;

@SuppressWarnings("serial")
public class ERespuestasC extends SelectorComposer<Component>{
	@Wire Window winRespuestas;
	@Wire Textbox txtRespuesta;
	@Wire Checkbox chkCorrecta;
	EPreguntaEditarC preguntaEditar;
	Pregunta pregunta;
	RespuestaDAO respuestaDAO = new RespuestaDAO();
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		PreguntaDAO preguntaDAO = new PreguntaDAO();
		preguntaEditar = (EPreguntaEditarC)Executions.getCurrent().getArg().get("VENTANA");
		pregunta = (Pregunta)Executions.getCurrent().getArg().get("PREGUNTA");
		System.out.println("Pregunta en la etapa de ingreso de respuestas: " + pregunta.toString());
		List<Pregunta> listaPreguntas = preguntaDAO.getPreguntaId(pregunta.getIdPregunta());
		if(listaPreguntas.size() > 0)
			pregunta = listaPreguntas.get(0);
		
		System.out.println("Respuestas recuperadas");
		for(Respuesta res : pregunta.getRespuestas()) {
			System.out.println(res.getIdRespuesta() + " " + res.getRespuesta());
		}
		
	}
	@Listen("onClick=#btnAgregar")
	public void agregarRespuesta(){
		try {
			if(txtRespuesta.getText().equals("")) {
				Clients.showNotification("Debe registrar la respuesta","info",txtRespuesta,"end_center",2000);
				return;
			}
			//verificar si ya hau una respuesta correcta
			boolean respuestaCorrecta = false;
			String correcta = Constantes.RESPUESTA_INCORRECTA;
			if(chkCorrecta.isChecked())
				correcta = Constantes.RESPUESTA_CORRECTA;
			if(pregunta.getRespuestas().size() > 0) {
				for(Respuesta res : pregunta.getRespuestas()) {
					if(res.getEstado().equals(Constantes.ESTADO_ACTIVO)) {
						if(res.getCorrecta().equals(Constantes.RESPUESTA_CORRECTA)){
							respuestaCorrecta = true;
						}
					}
				}
			}
			
			if(chkCorrecta.isChecked()) {
				if(respuestaCorrecta == true) {
					Clients.showNotification("Ya existe una respuesta correctra");
					return;
				}
			}
			
			
			Respuesta respuestaGrabar = new Respuesta();
			respuestaGrabar.setCorrecta(correcta);
			respuestaGrabar.setEstado("A");
			respuestaGrabar.setIdRespuesta(null);
			respuestaGrabar.setRespuesta(txtRespuesta.getText());
			respuestaGrabar.setPregunta(pregunta);
			if(pregunta.getRespuestas().size() > 0)
				pregunta.addRespuesta(respuestaGrabar);
			else {
				List<Respuesta> listaR = new ArrayList<>();
				listaR.add(respuestaGrabar);
				pregunta.setRespuestas(listaR);
			}
			
			PreguntaDAO preguntaDAO = new PreguntaDAO();
			preguntaDAO.getEntityManager().getTransaction().begin();
			preguntaDAO.getEntityManager().merge(pregunta);
			preguntaDAO.getEntityManager().getTransaction().commit();
			//actualizar la pregunta 
			System.out.println("Antes de hacer el refresh");
			for(Respuesta res : pregunta.getRespuestas()) {
				System.out.println(res.getIdRespuesta() + " " + res.getRespuesta());
			}
			
			List<Pregunta> listaPreguntas = preguntaDAO.getPreguntaId(pregunta.getIdPregunta());
			if(listaPreguntas.size() > 0)
				pregunta = listaPreguntas.get(0);
			
			System.out.println("Despues de hacer el refresh");
			for(Respuesta res : pregunta.getRespuestas()) {
				System.out.println(res.getIdRespuesta() + " " + res.getRespuesta());
			}
			preguntaEditar.recuperarRespuestas(pregunta);
			salir();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	@Listen("onClick=#btnSalir")
	public void salir(){
		try {
			winRespuestas.detach();
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
}
