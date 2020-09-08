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

import com.bombero.modelo.EquiposVestimenta;
import com.bombero.modelo.EquiposVestimentaDAO;

@SuppressWarnings("serial")
public class AEquiposListaC extends SelectorComposer<Component>{
	@Wire private Window winEquipoLista;
	@Wire private Textbox txtBuscar;
	@Wire private Listbox lstEquipo;
	EquiposVestimentaDAO equipoDAO = new EquiposVestimentaDAO();
	List<EquiposVestimenta> equipoLista;
	private EquiposVestimenta equipoSeleccionado;
		
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		buscarEquipo("");
	}
	@Listen("onClick=#btnBuscar;onOK=#txtBuscar")
	public void buscar(){
		buscarEquipo(txtBuscar.getText());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buscarEquipo(String dato) {
		if (equipoLista != null)
			equipoLista = null; 
		equipoLista = equipoDAO.getListaEquipos(dato);
		System.out.println(equipoLista.size());
		lstEquipo.setModel(new ListModelList(equipoLista));
		equipoSeleccionado = null;
	}
	@Listen("onClick=#btnNuevo")
	public void nuevo(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Equipo", null);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/AEquiposEditar.zul", winEquipoLista, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditar")
	public void editar(){
		if (equipoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un equipo de vestimenta");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		equipoDAO.getEntityManager().refresh(equipoSeleccionado);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Equipo", equipoSeleccionado);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/AEquiposEditar.zul", winEquipoLista, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminar")
	public void eliminar(){
		if (equipoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un equipo de vestimenta");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						equipoSeleccionado.setEstado("I");
						equipoDAO.getEntityManager().getTransaction().begin();
						equipoDAO.getEntityManager().merge(equipoSeleccionado);
						equipoDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						buscar();
					} catch (Exception e) {
						e.printStackTrace();
						equipoDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	public List<EquiposVestimenta> getEquipoLista() {
		return equipoLista;
	}
	public void setEquipoLista(List<EquiposVestimenta> equipoLista) {
		this.equipoLista = equipoLista;
	}
	public EquiposVestimenta getEquipoSeleccionado() {
		return equipoSeleccionado;
	}
	public void setEquipoSeleccionado(EquiposVestimenta equipoSeleccionado) {
		this.equipoSeleccionado = equipoSeleccionado;
	}
	
	

}
