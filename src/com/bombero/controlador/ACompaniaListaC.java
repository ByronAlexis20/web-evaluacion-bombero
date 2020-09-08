package com.bombero.controlador;

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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.bombero.modelo.Compania;
import com.bombero.modelo.CompaniaDAO;

@SuppressWarnings("serial")
public class ACompaniaListaC extends SelectorComposer<Component>{
	@Wire private Window winCompaniaLista;
	@Wire private Textbox txtBuscar;
	@Wire private Listbox lstCompania;
	CompaniaDAO companiaDAO = new CompaniaDAO();
	List<Compania> companiaLista;
	private Compania companiaSeleccionado;
		
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		buscarCampania("");
	}
	@Listen("onClick=#btnBuscar;onOK=#txtBuscar")
	public void buscar(){
		buscarCampania(txtBuscar.getText());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buscarCampania(String dato) {
		if (companiaLista != null)
			companiaLista = null; 
		companiaLista = companiaDAO.getListaCompania(dato);
		System.out.println(companiaLista.size());
		lstCompania.setModel(new ListModelList(companiaLista));
		companiaSeleccionado = null;
	}
	@Listen("onClick=#btnNuevo")
	public void nuevo(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Compania", null);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/ACompaniaEditar.zul", winCompaniaLista, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditar")
	public void editar(){
		if (companiaSeleccionado == null) {
			Messagebox.show("Debe seleccionar una compañía");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		companiaDAO.getEntityManager().refresh(companiaSeleccionado);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Compania", companiaSeleccionado);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/ACompaniaEditar.zul", winCompaniaLista, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminar")
	public void eliminar(){
		if (companiaSeleccionado == null) {
			Messagebox.show("Debe seleccionar una compañía");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						companiaSeleccionado.setEstado("I");
						companiaDAO.getEntityManager().getTransaction().begin();
						companiaDAO.getEntityManager().merge(companiaSeleccionado);
						companiaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						buscar();
					} catch (Exception e) {
						e.printStackTrace();
						companiaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	public List<Compania> getCompaniaLista() {
		return companiaLista;
	}
	public void setCompaniaLista(List<Compania> companiaLista) {
		this.companiaLista = companiaLista;
	}
	public Compania getCompaniaSeleccionado() {
		return companiaSeleccionado;
	}
	public void setCompaniaSeleccionado(Compania companiaSeleccionado) {
		this.companiaSeleccionado = companiaSeleccionado;
	}
}
