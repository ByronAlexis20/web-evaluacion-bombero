package com.bombero.controlador;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.zkoss.io.Files;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.bombero.modelo.Perfil;
import com.bombero.modelo.PerfilDAO;
import com.bombero.modelo.Periodo;
import com.bombero.modelo.Persona;
import com.bombero.modelo.PersonaDAO;
import com.bombero.modelo.Usuario;
import com.bombero.modelo.UsuarioDAO;
import com.bombero.util.Constantes;
import com.bombero.util.ControllerHelper;

@SuppressWarnings("serial")
public class IAspirantesEditarC extends SelectorComposer<Component>{
	@Wire private Window winAspirantesEditar;
	@Wire private Textbox txtCedula;
	@Wire private Textbox txtTelefono;
	@Wire private Textbox txtNombres;
	@Wire private Textbox txtApellidos;
	@Wire private Textbox txtEmail;
	@Wire private Datebox dtpFechaNacimiento;
	@Wire private Combobox cboTipoSangre;
	@Wire private Combobox cboAntecedentes;
	@Wire private Combobox cboGenero;
	@Wire private Checkbox chkPruebaFisica;
	@Wire private Textbox txtCertificadoMedico;
	Media mediaCertificadoMedico;
	@Wire private Textbox txtTituloBachiller;
	Media mediaTituloBachiller;
	@Wire private Textbox txtDireccion;
	@Wire private Textbox txtReferencia;
	
	
	private IAspirantesListaC aspiranteLista;
	PersonaDAO personaDAO = new PersonaDAO();
	private Persona aspirante;
	private Periodo periodo;
	ControllerHelper helper = new ControllerHelper();
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		System.out.println("Entra al metodo");
		try {
			aspiranteLista = (IAspirantesListaC)Executions.getCurrent().getArg().get("VENTANA");
			if (Executions.getCurrent().getArg().get("PERIODO") != null) {
				periodo = (Periodo) Executions.getCurrent().getArg().get("PERIODO");
			}
			if (Executions.getCurrent().getArg().get("PERSONA") != null) {
				System.out.println("Es un aspirante recuperado");
				aspirante = (Persona) Executions.getCurrent().getArg().get("PERSONA");
				cargarDatosAspirante(aspirante);
			}else {
				System.out.println("Es un aspirante nuevo");
				aspirante = new Persona();
				aspirante.setIdPersona(null);
				aspirante.setEstado("A");
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	private void cargarDatosAspirante(Persona per) {
		try {
			if(per.getCedula() != null)
				txtCedula.setText(per.getCedula());
			if(per.getTelefono() != null)
				txtTelefono.setText(per.getTelefono());
			if(per.getNombre() != null)
				txtNombres.setText(per.getNombre());
			if(per.getApellido() != null)
				txtApellidos.setText(per.getApellido());
			if(per.getEmail() != null)
				txtEmail.setText(per.getEmail());
			if(per.getFechaNacimiento() != null)
				dtpFechaNacimiento.setValue(per.getFechaNacimiento());
			if(per.getTipoSangre() != null)
				cboTipoSangre.setText(per.getTipoSangre());
			if(per.getAntecedentesPenales() != null) {
				if(per.getAntecedentesPenales().equals("S"))
					cboAntecedentes.setText("SI");
				else
					cboAntecedentes.setText("NO");
			}
				
			if(per.getGenero() != null)
				cboGenero.setText(per.getGenero());
			if(per.getAptoPruebaFisica() != null) {
				if(per.getAptoPruebaFisica().equals("S"))
					chkPruebaFisica.setChecked(true);
				else
					chkPruebaFisica.setChecked(false);
			}
			if(per.getDireccion() != null)
				txtDireccion.setText(per.getDireccion());
			if(per.getReferenciaDomiciliaria() != null)
				txtReferencia.setText(per.getReferenciaDomiciliaria());
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Listen("onClick=#btnGrabar")
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						cargarDatos();
						personaDAO.getEntityManager().getTransaction().begin();
						if(aspirante.getIdPersona() == null) {//es nuevo
							personaDAO.getEntityManager().persist(aspirante);
						}else {
							personaDAO.getEntityManager().merge(aspirante);
						}
						personaDAO.getEntityManager().getTransaction().commit();
						System.out.println("id Del aspirante despues de grabar: " + aspirante.getIdPersona());
						Messagebox.show("Datos grabados con exito");
						//grabar el usuario
						//bandera = true -> es un usuario nuevo; bandera = false -> usuario recuperado
						grabarUsuario(aspirante.getIdPersona());
						aspiranteLista.buscarAspirantes(periodo.getIdPeriodo());
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	private void grabarUsuario(Integer idPersona) {
		UsuarioDAO usuarioDAO = new UsuarioDAO();
		PerfilDAO perfilDAO = new PerfilDAO();
		ControllerHelper helper = new ControllerHelper();
		//primero preguntar si existe un usuario para la persona
		List<Usuario> usuarios = usuarioDAO.getBuscarPorIdPersona(idPersona);
		if(usuarios.size() > 0) {// si es mayor a cero, la persona si tiene un usuario, no se realiza ninguna accion
		}else {
			Persona personaRecuperada = personaDAO.getPersonaId(idPersona);
			if(personaRecuperada != null) {
				//se crea el usuario para la persona registrada
				Usuario usuario = new Usuario();
				usuario.setUsuario(txtCedula.getText());
				usuario.setClave(helper.encriptar(txtCedula.getText()));
				usuario.setEstado("A");
				usuario.setIdUsuario(null);
				usuario.setIdPersona(idPersona);
				//se recupera el perfil estudiante
				List<Perfil> listaPerfil = perfilDAO.getPerfilPorId(Constantes.ID_PERFIL_ESTUDIANTE);
				if(listaPerfil.size() > 0)
					usuario.setPerfil(listaPerfil.get(0));
				usuarioDAO.getEntityManager().getTransaction().begin();
				usuarioDAO.getEntityManager().persist(usuario);
				usuarioDAO.getEntityManager().getTransaction().commit();
			}
		}
	}
	private void cargarDatos() throws IOException {
		String rutaProyecto = Executions.getCurrent()
				.getDesktop().getWebApp()
				.getRealPath("/");
		
		aspirante.setCedula(txtCedula.getText());
		aspirante.setIdPeriodo(periodo.getIdPeriodo());
		aspirante.setTelefono(txtTelefono.getText());
		aspirante.setNombre(txtNombres.getText());
		aspirante.setApellido(txtApellidos.getText());
		aspirante.setEmail(txtEmail.getText());
		aspirante.setFechaNacimiento(dtpFechaNacimiento.getValue());
		String tipoSangre = (String)cboTipoSangre.getSelectedItem().getValue();
		aspirante.setTipoSangre(tipoSangre);
		String antecedentes = (String)cboAntecedentes.getSelectedItem().getValue();
		aspirante.setAntecedentesPenales(antecedentes);
		String genero = (String)cboGenero.getSelectedItem().getValue();
		aspirante.setGenero(genero);
		if(chkPruebaFisica.isChecked())
			aspirante.setAptoPruebaFisica("S");
		else
			aspirante.setAptoPruebaFisica("N");
		rutaProyecto = rutaProyecto + "temp";
		
		if(mediaCertificadoMedico != null) {
			String rutaCertificado = rutaProyecto + "\\" + Constantes.NOMBRE_CERTIFICADO + txtCedula.getText() + ".pdf";
			aspirante.setCertificadoMedico(rutaCertificado);
			//copiar el archivo a la carpeta
			File folder = new File(rutaProyecto);
			if (folder.exists()) {
			}else {
				folder.mkdir();
			}
			Files.copy(new File(rutaCertificado),mediaCertificadoMedico.getStreamData());
		}
		if(mediaTituloBachiller != null) {
			String rutaTitulo = rutaProyecto + "\\" + Constantes.NOMBRE_TITULO + txtCedula.getText() + ".pdf";
			aspirante.setTituloBachiller(rutaTitulo);
			//copiar el archivo a la carpeta
			File folder = new File(rutaProyecto);
			if (folder.exists()) {
			}else {
				folder.mkdir();
			}
			Files.copy(new File(rutaTitulo),mediaTituloBachiller.getStreamData());
		}
		aspirante.setDireccion(txtDireccion.getText());
		aspirante.setReferenciaDomiciliaria(txtReferencia.getText());
		aspirante.setEstado("A");
	}
	private boolean validarDatos() {
		if(txtCedula.getText().equals("")) {
			Clients.showNotification("Campo Obligatorio: Cédula del aspirante","info",txtCedula,"end_center",2000);
			return false;
		}
		if(!helper.validarDeCedula(txtCedula.getText())) {
			Clients.showNotification("Cédula registrada incorrecta!","info",txtCedula,"end_center",2000);
			return false;
		}
		if(validarCedulaExistente(txtCedula.getText()) == false) {
			Clients.showNotification("Cédula ya existe entre los registros!","info",txtCedula,"end_center",2000);
			return false;
		}
		if(txtNombres.getText().equals("")) {
			Clients.showNotification("Campo Obligatorio: Nombres del aspirante","info",txtNombres,"end_center",2000);
			return false;
		}
		if(txtApellidos.getText().equals("")) {
			Clients.showNotification("Campo Obligatorio: Apellidos del aspirante","info",txtApellidos,"end_center",2000);
			return false;
		}
		if(dtpFechaNacimiento.getValue() == null) {
			Clients.showNotification("Campo Obligatorio: Fecha de nacimiento","info",dtpFechaNacimiento,"end_center",2000);
			return false;	
		}
		if(cboTipoSangre.getSelectedItem() == null) {
			Clients.showNotification("Campo Obligatorio: Tipo de sangre","info",cboTipoSangre,"end_center",2000);
			return false;
		}
		if(cboAntecedentes.getSelectedItem() == null) {
			Clients.showNotification("Campo Obligatorio: Antecedentes penales","info",cboAntecedentes,"end_center",2000);
			return false;
		}
		if(cboGenero.getSelectedItem() == null) {
			Clients.showNotification("Campo Obligatorio: Género del aspirante","info",cboGenero,"end_center",2000);
			return false;
		}
		if(!txtEmail.getText().equals("")) {
			if(!ControllerHelper.validarEmail(txtEmail.getText())) {
				Clients.showNotification("Advertencia: Email ingresado no es correcto","info",txtEmail,"end_center",2000);
				
				return false;
			}
		}
		//edad entre 18 a 35 años
		if(dtpFechaNacimiento.getValue() != null) {
			Calendar fecha = Calendar.getInstance();
			fecha.setTime(dtpFechaNacimiento.getValue());
			System.out.println(helper.calcularEdad(fecha));
			if(helper.calcularEdad(fecha) < 18 || helper.calcularEdad(fecha) > 35) {
				Messagebox.show("Advertencia: Solo es permitido registrar a personas entre 18 a 35 años");
				//Clients.showNotification("Advertencia: Solo es permitido registrar a personas entre 18 a 35 años","info",dtpFechaNacimiento,"end_center",2000);
				return false;
			}
		}
		
		return true;
	}
	//retorna falso cuando la cedula existe en otro registro
	private boolean validarCedulaExistente(String cedula) {
		List<Persona> listadoPersonas = new ArrayList<>();
		if(aspirante.getIdPersona() != null)
			listadoPersonas = personaDAO.validarPersonaCedula(aspirante.getIdPersona(), cedula);
		else
			listadoPersonas = personaDAO.validarPersonaCedula(cedula);
		if(listadoPersonas.size() > 0)
			return false;
		return true;
	}
	@Listen("onClick=#btnSalir")
	public void salir(){
		winAspirantesEditar.detach();
	}
	@Listen("onUpload = button#uploadCertificado")
	public void onUploadCertificadoMedico(UploadEvent event) {
		try {
			System.out.println("before upload " + event.getMedia().getName());
			mediaCertificadoMedico = event.getMedia();
			txtCertificadoMedico.setValue(mediaCertificadoMedico.getName());
			System.out.println(mediaCertificadoMedico);
		} catch (Exception e) {
			e.printStackTrace();
			Messagebox.show("Upload failed");
		}
	}
	@Listen("onUpload = button#uploadTitulo")
	public void onUploadTituloBachiller(UploadEvent event) {
		try {
			System.out.println("before upload " + event.getMedia().getName());
			mediaTituloBachiller = event.getMedia();
			txtTituloBachiller.setValue(mediaTituloBachiller.getName());
			System.out.println(mediaTituloBachiller);
		} catch (Exception e) {
			e.printStackTrace();
			Messagebox.show("Upload failed");
		}
	}
	public Persona getAspirante() {
		return aspirante;
	}
	public void setAspirante(Persona aspirante) {
		this.aspirante = aspirante;
	}
	public Periodo getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
	}
	
}
