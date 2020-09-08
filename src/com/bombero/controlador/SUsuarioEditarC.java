package com.bombero.controlador;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.bombero.modelo.Perfil;
import com.bombero.modelo.PerfilDAO;
import com.bombero.modelo.Usuario;
import com.bombero.modelo.UsuarioDAO;
import com.bombero.util.ControllerHelper;

@SuppressWarnings("serial")
public class SUsuarioEditarC extends SelectorComposer<Component>{
	@Wire private Window winUsuarioRegistro;
	@Wire private Textbox txtNombre;
	@Wire private Textbox txtApellido;
	@Wire private Textbox txtCedula;
	@Wire private Textbox txtTelefono;
	@Wire private Textbox txtUsuario;
	@Wire private Textbox txtClave;
	@Wire private Combobox cboPerfil;
	ControllerHelper helper = new ControllerHelper();
	PerfilDAO perfilDAO = new PerfilDAO();
	SUsuarioListaC usuarioLista;
	UsuarioDAO usuarioDAO = new UsuarioDAO();
	Usuario usuario;
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		try {
			usuarioLista = (SUsuarioListaC)Executions.getCurrent().getArg().get("VentanaPadre");
			usuario = null;	
			if (Executions.getCurrent().getArg().get("Usuario") != null) {
				usuario = (Usuario) Executions.getCurrent().getArg().get("Usuario");
			}else {
				usuario = new Usuario();
				usuario.setEstado("A");
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		
		/*
		*/
	}
	@Listen("onClick=#btnGrabar")
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						usuarioDAO.getEntityManager().getTransaction().begin();
						if(usuario.getIdUsuario() == null) {//es un nuevo
							usuario.setClave(helper.encriptar(txtClave.getText()));
							usuarioDAO.getEntityManager().persist(usuario);
						}else {//es modificacion
							usuario.setClave(helper.encriptar(txtClave.getText()));
							usuarioDAO.getEntityManager().merge(usuario);
						}
						usuarioDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						usuarioLista.buscarUsuario("");
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	public boolean validarDatos() {
		if(txtCedula.getText() == "") {
			Clients.showNotification("Debe registrar cédula","info",txtCedula,"end_center",2000);
			txtCedula.setFocus(true);
			return false;
		}
		if(helper.validarDeCedula(txtCedula.getText()) == false) {
			Clients.showNotification("Cédula no valida","info",txtCedula,"end_center",2000);
			txtCedula.setFocus(true);
			return false;
		}
		if(txtNombre.getText() == "") {
			Clients.showNotification("Debe registrar nombres","info",txtNombre,"end_center",2000);
			txtNombre.setFocus(true);
			return false;
		}
		if(txtApellido.getText() == "") {
			Clients.showNotification("Debe registrar apellidos","info",txtApellido,"end_center",2000);
			txtApellido.setFocus(true);
			return false;
		}
		if(cboPerfil.getSelectedIndex() == -1) {
			Clients.showNotification("Debe seleccionar Perfil","info",cboPerfil,"end_center",2000);
			return false;
		}
		if(txtUsuario.getText() == "") {
			Clients.showNotification("Campo obligatorio: usuario","info",txtUsuario,"end_center",2000);	
			return false;
		}
		if(txtClave.getText() == "") {
			Clients.showNotification("Campo obligatorio: clave","info",txtClave,"end_center",2000);
			return false;
		}
		int idUsuario;
		if(usuario.getIdUsuario() != null)
			idUsuario = usuario.getIdUsuario();
		else
			idUsuario = 0;
		if(usuarioDAO.getBuscarUsuario(txtUsuario.getText(),idUsuario).size() > 0) {
			Clients.showNotification("Usuario ya existe","info",txtUsuario,"end_center",2000);
			return false;
		}
		return true;
	}
	public List<Perfil> getPerfiles(){
		return perfilDAO.getListaPerfilesActivos();
	}
	@Listen("onClick=#btnSalir")
	public void salir(){
		winUsuarioRegistro.detach();
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
