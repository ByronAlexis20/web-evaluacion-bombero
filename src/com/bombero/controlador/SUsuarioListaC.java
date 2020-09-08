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

import com.bombero.modelo.Usuario;
import com.bombero.modelo.UsuarioDAO;

@SuppressWarnings("serial")
public class SUsuarioListaC extends SelectorComposer<Component>{
	@Wire private Window winUsuarioLista;
	@Wire private Textbox txtBuscar;
	@Wire private Listbox lstUsuario;
	UsuarioDAO usuarioDAO = new UsuarioDAO();
	//SegUsuario usuario;
	List<Usuario> usuarioLista;
	private Usuario usuarioSeleccionado;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//usuario= new SegUsuario();
		buscar();
	}
	@Listen("onClick=#btnBuscar;onOK=#txtBuscar")
	public void buscar(){
		buscarUsuario(txtBuscar.getText());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buscarUsuario(String dato) {
		if (usuarioLista != null)
			usuarioLista = null; 
		usuarioLista = usuarioDAO.getListausuarioBuscar(dato);
		lstUsuario.setModel(new ListModelList(usuarioLista));
		usuarioSeleccionado = null;
	}
	@Listen("onClick=#btnNuevo")
	public void nuevo(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Usuario", null);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/seguridad/SUsuarioEditar.zul", winUsuarioLista, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditar")
	public void editar(){
		if (usuarioSeleccionado == null) {
			Messagebox.show("Debe seleccionar un usuario");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		usuarioDAO.getEntityManager().refresh(usuarioSeleccionado);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Usuario", usuarioSeleccionado);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/seguridad/SUsuarioEditar.zul", winUsuarioLista, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminar")
	public void eliminar(){
		if (usuarioSeleccionado == null) {
			Messagebox.show("Debe seleccionar un usuario");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						usuarioSeleccionado.setEstado("I");
						usuarioDAO.getEntityManager().getTransaction().begin();
						usuarioDAO.getEntityManager().merge(usuarioSeleccionado);
						usuarioDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						buscar();
					} catch (Exception e) {
						e.printStackTrace();
						usuarioDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}

	public List<Usuario> getUsuarioLista() {
		return usuarioLista;
	}

	public void setUsuarioLista(List<Usuario> usuarioLista) {
		this.usuarioLista = usuarioLista;
	}

	public Usuario getUsuarioSeleccionado() {
		return usuarioSeleccionado;
	}

	public void setUsuarioSeleccionado(Usuario usuarioSeleccionado) {
		this.usuarioSeleccionado = usuarioSeleccionado;
	}

}
