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

import com.bombero.modelo.Modulo;
import com.bombero.modelo.ModuloDAO;

@SuppressWarnings("serial")
public class AModuloListaC extends SelectorComposer<Component>{
	@Wire private Window winModuloLista;
	@Wire private Textbox txtBuscar;
	@Wire private Listbox lstModulo;
	ModuloDAO moduloDAO = new ModuloDAO();
	List<Modulo> moduloLista;
	private Modulo moduloSeleccionado;
		
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		buscarModulo("");
	}
	@Listen("onClick=#btnBuscar;onOK=#txtBuscar")
	public void buscar(){
		buscarModulo(txtBuscar.getText());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buscarModulo(String dato) {
		if (moduloLista != null)
			moduloLista = null; 
		moduloLista = moduloDAO.getListaModulo(dato);
		System.out.println(moduloLista.size());
		lstModulo.setModel(new ListModelList(moduloLista));
		moduloSeleccionado = null;
	}
	@Listen("onClick=#btnNuevo")
	public void nuevo(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Modulo", null);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/AModuloEditar.zul", winModuloLista, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditar")
	public void editar(){
		if (moduloSeleccionado == null) {
			Messagebox.show("Debe seleccionar un módulo");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		moduloDAO.getEntityManager().refresh(moduloSeleccionado);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Modulo", moduloSeleccionado);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/AModuloEditar.zul", winModuloLista, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminar")
	public void eliminar(){
		if (moduloSeleccionado == null) {
			Messagebox.show("Debe seleccionar un módulo");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						moduloSeleccionado.setEstado("I");
						moduloDAO.getEntityManager().getTransaction().begin();
						moduloDAO.getEntityManager().merge(moduloSeleccionado);
						moduloDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						buscar();
					} catch (Exception e) {
						e.printStackTrace();
						moduloDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	public List<Modulo> getModuloLista() {
		return moduloLista;
	}
	public void setModuloLista(List<Modulo> moduloLista) {
		this.moduloLista = moduloLista;
	}
	public Modulo getModuloSeleccionado() {
		return moduloSeleccionado;
	}
	public void setModuloSeleccionado(Modulo moduloSeleccionado) {
		this.moduloSeleccionado = moduloSeleccionado;
	}
	
}
