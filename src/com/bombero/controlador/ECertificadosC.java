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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import com.bombero.modelo.AsignarGuardiaDAO;
import com.bombero.modelo.CompaniaDAO;
import com.bombero.modelo.Periodo;
import com.bombero.modelo.PeriodoDAO;
import com.bombero.modelo.Persona;
import com.bombero.modelo.PersonaDAO;
import com.bombero.modelo.ResultadosEvaluacion;
import com.bombero.util.Constantes;
import com.bombero.util.PrintReport;
import com.ibm.icu.util.Calendar;


public class ECertificadosC {
	@Wire Window winCertificado;
	@Wire Listbox lstPersonas;
	CompaniaDAO companiaDAO = new CompaniaDAO();
	PersonaDAO personaDAO = new PersonaDAO();
	PeriodoDAO periodoDAO = new PeriodoDAO();
	AsignarGuardiaDAO guardiaDAO = new AsignarGuardiaDAO();
	
	private Periodo periodoSeleccionada;
	private Persona personaSeleccionado;
	private List<Persona> personaLista;
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void seleccionarPersonas() {
		try {
			System.out.println("seleccionar guardia");
			
			List<Persona> lista = personaDAO.getListaAspirantesPeriodo(periodoSeleccionada.getIdPeriodo());
			List<Persona> listaPresentar = new ArrayList<>();
			Integer sumaPersona = 0;
			for(Persona per : lista) {
				sumaPersona = 0;
				for(ResultadosEvaluacion res : per.getResultadosEvaluacions()) {
					if(res.getEstado().equals(Constantes.ESTADO_ACTIVO)) {
						sumaPersona = sumaPersona + res.getCalificacion();
					}
				}
				if(sumaPersona >= 15) {
					listaPresentar.add(per);
				}
			}
			lstPersonas.setModel(new ListModelList(listaPresentar));
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void imprimirCertificado() {
		try {
			System.out.println("asignar guardia");
			if(personaSeleccionado == null) {
				Clients.showNotification("Debe seleccionar una Compañía!!!!!",false);
				return;
			}
			Calendar calendar = Calendar.getInstance();
			int anios = calendar.get(Calendar.YEAR);
	        int mes = calendar.get(Calendar.MONTH);
	        int dias = calendar.get(Calendar.DAY_OF_MONTH);
	        
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("NO ORDEN","ORDEN GENERAL No. " + lstPersonas.getSelectedIndex() + 1 + "/" + anios);
			String reglamento = "El señor(a) " + personaSeleccionado.getNombre() + " " + personaSeleccionado.getApellido() + "; una vez cumplidos con los requisitos ";
			reglamento = reglamento + "de ley y de acuerdo con la facultad que me concede los Art. 19 de la Ley de Defensa contra incendios y sus Reglamentos, dispongo la incorporaciòn como Bombero voluntario al señor(a):";
			params.put("REGLAMENTO", reglamento);
			String firma = "Dado y firmado en el Cuartel del Cuerpo de Bomberos del Cantòn La Libertad, a los " + dias + " dîas del mes " + meses(mes) + " de " + anios;
			params.put("FIRMA", firma);
			PrintReport obj = new PrintReport();
			obj.showViewReport("/reportes/rptCertificado.jasper", params, personaDAO);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	private String meses(Integer mes) {
		String mesActual = "";
		switch (mes) {
		case 1:
			mesActual = "Enero";
			break;
		case 2:
			mesActual = "Febrero";
			break;
		case 3:
			mesActual = "Marzo";
			break;
		case 4:
			mesActual = "Abril";
			break;
		case 5:
			mesActual = "Mayo";
			break;
		case 6:
			mesActual = "Junio";
			break;
		case 7:
			mesActual = "Julio";
			break;
		case 8:
			mesActual = "Agosto";
			break;
		case 9:
			mesActual = "Septiembre";
			break;
		case 10:
			mesActual = "Octubre";
			break;
		case 11:
			mesActual = "Noviembre";
			break;
		case 12:
			mesActual = "Diciembre";
			break;
		}
		return mesActual;
	}
	public List<Periodo> getPeriodos(){
		try {
			List<Periodo> lista = periodoDAO.getListaPeriodos("");
			return lista;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
	}
	public Periodo getPeriodoSeleccionada() {
		return periodoSeleccionada;
	}
	public void setPeriodoSeleccionada(Periodo periodoSeleccionada) {
		this.periodoSeleccionada = periodoSeleccionada;
	}
	public List<Persona> getPersonaLista() {
		return personaLista;
	}
	public void setPersonaLista(List<Persona> personaLista) {
		this.personaLista = personaLista;
	}
	public Persona getPersonaSeleccionado() {
		return personaSeleccionado;
	}
	public void setPersonaSeleccionado(Persona personaSeleccionado) {
		this.personaSeleccionado = personaSeleccionado;
	}
}
