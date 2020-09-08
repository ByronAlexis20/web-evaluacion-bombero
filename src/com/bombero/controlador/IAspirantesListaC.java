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
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.bombero.modelo.Periodo;
import com.bombero.modelo.PeriodoDAO;
import com.bombero.modelo.Persona;
import com.bombero.modelo.PersonaDAO;

@SuppressWarnings("serial")
public class IAspirantesListaC extends SelectorComposer<Component>{
	@Wire private Window winAspirantesLista;
	@Wire private Listbox lstPeriodo;
	@Wire private Listbox lstAspirantes;
	@Wire private Label lblPeriodoSeleccionado;
	@Wire private Label lblEstado;
	List<PeriodoPresentar> periodoLista;
	List<AspirantePresentar> aspiranteLista;
	private AspirantePresentar personaSeleccionado;
	private PeriodoPresentar periodoSeleccionado;
	SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");
	PeriodoDAO periodoDAO = new PeriodoDAO();
	PersonaDAO personaDAO = new PersonaDAO();
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//usuario= new SegUsuario();
		buscarPeriodo("");
		buscarAspirantes(0);
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
			lblPeriodoSeleccionado.setValue(periodoSeleccionado.getPresentacion());
			lblEstado.setValue(periodoSeleccionado.getEstadoPeriodo());
			buscarAspirantes(periodoSeleccionado.getIdPeriodo());
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	//***********************************************************************************************************************************************
	//***********************************************************************************************************************************************
	//*********************************seccion para el registro de aspirantes a bomberos ************************************************************
	//***********************************************************************************************************************************************
	//***********************************************************************************************************************************************
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buscarAspirantes(Integer idPeriodo) {
		List<Persona> listado = personaDAO.getListaAspirantesPeriodo(idPeriodo);
		List<AspirantePresentar> listaPresentar = new ArrayList<>();
		for(Persona per : listado) {
			AspirantePresentar aspirante = new AspirantePresentar();
			aspirante.setIdPersona(per.getIdPersona());
			aspirante.setAntecedentes(per.getAntecedentesPenales());
			aspirante.setCedula(per.getCedula());
			aspirante.setFechaNacimiento(formateador.format(per.getFechaNacimiento()));
			aspirante.setGenero(per.getGenero());
			aspirante.setNombres(per.getNombre() + " " + per.getApellido());
			aspirante.setTipoSangre(per.getTipoSangre());
			listaPresentar.add(aspirante);
		}
		lstAspirantes.setModel(new ListModelList(listaPresentar));
		personaSeleccionado = null;
	}
	
	@Listen("onClick=#btnNuevoAspirante")
	public void nuevoAspirante() {
		if(periodoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un periodo");
			return;
		}
		if(!lblEstado.getValue().equals("EN PROCESO")) {
			Messagebox.show("El periodo se no se encuentra \"EN PROCESO\", por lo tanto no se permite registrar aspirantes a bomberos!");
			return;
		}
		List<Periodo> listado = periodoDAO.getPeriodoById(periodoSeleccionado.getIdPeriodo());
		Map<String, Object> params = new HashMap<String, Object>();
		if(listado.size() > 0)
			params.put("PERIODO", listado.get(0));
		else
			params.put("PERIODO", null);
		params.put("PERSONA", null);
		params.put("VENTANA", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/inscripcion/IAspirantesEditar.zul", winAspirantesLista, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditarAspirante")
	public void editarAspirante() {
		if(personaSeleccionado == null) {
			Messagebox.show("Debe seleccionar una persona");
			return;
		}
		List<Periodo> listado = periodoDAO.getPeriodoById(periodoSeleccionado.getIdPeriodo());
		Map<String, Object> params = new HashMap<String, Object>();
		if(listado.size() > 0)
			params.put("PERIODO", listado.get(0));
		else
			params.put("PERIODO", null);
		Persona per = personaDAO.getPersonaId(personaSeleccionado.getIdPersona());
		if(per != null)
			params.put("PERSONA", per);
		else
			params.put("PERSONA", null);
		params.put("VENTANA", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/inscripcion/IAspirantesEditar.zul", winAspirantesLista, params);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminarAspirante")
	public void eliminarAspirante() {
		if(personaSeleccionado == null) {
			Messagebox.show("Debe seleccionar una persona");
			return;
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						Persona per = personaDAO.getPersonaId(personaSeleccionado.getIdPersona());
						if(per != null) {
							per.setEstado("I");
							personaDAO.getEntityManager().getTransaction().begin();
							periodoDAO.getEntityManager().merge(per);
							personaDAO.getEntityManager().getTransaction().commit();
							Messagebox.show("Transaccion ejecutada con exito");
							buscarAspirantes(periodoSeleccionado.getIdPeriodo());
						}
					} catch (Exception e) {
						e.printStackTrace();
						personaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	
	
	//getters and setters de variables globales utilizadas
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
	public List<AspirantePresentar> getAspiranteLista() {
		return aspiranteLista;
	}
	public void setAspiranteLista(List<AspirantePresentar> aspiranteLista) {
		this.aspiranteLista = aspiranteLista;
	}
	public AspirantePresentar getPersonaSeleccionado() {
		return personaSeleccionado;
	}
	public void setPersonaSeleccionado(AspirantePresentar personaSeleccionado) {
		this.personaSeleccionado = personaSeleccionado;
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
	public class AspirantePresentar{
		private String cedula;
		private Integer idPersona;
		private String nombres;
		private String tipoSangre;
		private String antecedentes;
		private String genero;
		private String fechaNacimiento;
		public String getCedula() {
			return cedula;
		}
		public void setCedula(String cedula) {
			this.cedula = cedula;
		}
		public String getNombres() {
			return nombres;
		}
		public void setNombres(String nombres) {
			this.nombres = nombres;
		}
		public String getTipoSangre() {
			return tipoSangre;
		}
		public void setTipoSangre(String tipoSangre) {
			this.tipoSangre = tipoSangre;
		}
		public String getAntecedentes() {
			return antecedentes;
		}
		public void setAntecedentes(String antecedentes) {
			this.antecedentes = antecedentes;
		}
		public String getFechaNacimiento() {
			return fechaNacimiento;
		}
		public void setFechaNacimiento(String fechaNacimiento) {
			this.fechaNacimiento = fechaNacimiento;
		}
		public Integer getIdPersona() {
			return idPersona;
		}
		public void setIdPersona(Integer idPersona) {
			this.idPersona = idPersona;
		}
		public String getGenero() {
			return genero;
		}
		public void setGenero(String genero) {
			this.genero = genero;
		}
	}
}
