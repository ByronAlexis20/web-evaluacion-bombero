package com.bombero.controlador;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.bombero.modelo.Menu;
import com.bombero.modelo.MenuDAO;
import com.bombero.modelo.Perfil;
import com.bombero.modelo.PerfilDAO;
import com.bombero.modelo.Permiso;
import com.bombero.modelo.PermisoDAO;


@SuppressWarnings("serial")
public class SPermisosC extends SelectorComposer<Component>{
	PerfilDAO perfilDAO = new PerfilDAO();
	@Wire private Combobox cboPerfiles;
	MenuDAO menuDAO = new MenuDAO();
	PermisoDAO permisoDAO = new PermisoDAO();
	
	@Wire private Listbox lstPermisosTodos;
	@Wire private Listbox lstPermisoPerfil;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		cargarMenuDisponibles();
		
	}
	@Listen("onClick=#btnAgregar")
	public void agregar() {
		if(cboPerfiles.getSelectedItem() == null) {
			Clients.showNotification("Debe seleccionar un perfil");
			return;
		}
		if(lstPermisosTodos.getSelectedItem().getValue() == null){
			Clients.showNotification("Debe seleccionar un menu para agreagar");
			return;
		}
		Perfil perfilSeleccionado = (Perfil)cboPerfiles.getSelectedItem().getValue();
		Menu menuSeleccionado = (Menu)lstPermisosTodos.getSelectedItem().getValue();
		System.out.println(perfilSeleccionado.getPerfil());
		System.out.println(menuSeleccionado.getDescripcion());
		//grabar 

		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					perfilDAO.getEntityManager().getTransaction().begin();
					Permiso permisoGrabar = new Permiso();
					permisoGrabar.setIdPermiso(null);
					permisoGrabar.setEstado("A");
					permisoGrabar.setMenu(menuSeleccionado);
					permisoGrabar.setPerfil(perfilSeleccionado);
					perfilDAO.getEntityManager().persist(permisoGrabar);
					perfilDAO.getEntityManager().getTransaction().commit();
					Messagebox.show("Datos grabados con exito");
					cargarPermisosPerfil(perfilSeleccionado.getIdPerfil());
				}
			}
		};
		Messagebox.show("Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);


	}
	@Listen("onChange = #cboPerfiles")
    public void cambioPerfil() {
		if(cboPerfiles.getSelectedItem() == null) {
			Clients.showNotification("Debe seleccionar un perfil");
			return;
		}
		Perfil perfilSeleccionado = (Perfil)cboPerfiles.getSelectedItem().getValue();
		cargarPermisosPerfil(perfilSeleccionado.getIdPerfil());
    }
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void cargarPermisosPerfil(Integer idPerfil) {
		try {
			List<Permiso> listaPermisos = permisoDAO.getListaPermisosHijo(idPerfil);
			for(Permiso per : listaPermisos) {
				List<Menu> listadoPadre = menuDAO.getMenuPadre(per.getMenu().getIdMenuPadre());
				if(listadoPadre.size() > 0) {						
					per.getMenu().setPresentacion(listadoPadre.get(0).getDescripcion() + " --> " + per.getMenu().getDescripcion());
				}
			}
			lstPermisoPerfil.setModel(new ListModelList(listaPermisos));
			boolean bandera = false;
			//tambien se deben cargar los menu que quedan
			List<Menu> listaMenuDisponibles = menuDAO.getListaHijos();
			List<Menu> listaSobrentes = new ArrayList<>();
			for(Menu menu : listaMenuDisponibles) {
				bandera = false;
				for(Permiso per : listaPermisos) {
					if(menu.getIdMenu() == per.getMenu().getIdMenu()) {
						bandera = true;
					}
				}
				//no eciste
				if(bandera == false) {
					listaSobrentes.add(menu);
				}
			}
			
			if(listaSobrentes.size() > 0) {
				for(Menu mnu : listaSobrentes) {
					List<Menu> listadoPadre = menuDAO.getMenuPadre(mnu.getIdMenuPadre());
					if(listadoPadre.size() > 0) {						
						mnu.setPresentacion(listadoPadre.get(0).getDescripcion() + " --> " + mnu.getDescripcion());
					}
				}
			}
			lstPermisosTodos.setModel(new ListModelList(listaSobrentes));
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	@Listen("onClick=#btnQuitar")
	public void quitar() {
		if(cboPerfiles.getSelectedItem() == null) {
			Clients.showNotification("Debe seleccionar un perfil");
			return;
		}
		if(lstPermisoPerfil.getSelectedItem().getValue() == null){
			Clients.showNotification("Debe seleccionar un menu para quitar");
			return;
		}
		Perfil perfilSeleccionado = (Perfil)cboPerfiles.getSelectedItem().getValue();
		Permiso menuSeleccionado = (Permiso)lstPermisosTodos.getSelectedItem().getValue();
		System.out.println(menuSeleccionado.getMenu().getDescripcion());
		System.out.println(perfilSeleccionado.getPerfil());
		
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					perfilDAO.getEntityManager().getTransaction().begin();
					Permiso permisoGrabar = menuSeleccionado;
					permisoGrabar.setEstado("I");
					perfilDAO.getEntityManager().persist(permisoGrabar);
					perfilDAO.getEntityManager().getTransaction().commit();
					Messagebox.show("Menu Quitado con exito");
					cargarPermisosPerfil(perfilSeleccionado.getIdPerfil());
				}
			}
		};
		Messagebox.show("Desea quitar el menu?", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void cargarMenuDisponibles() {
		try {
			List<Menu> listaMenuDisponibles = menuDAO.getListaHijos();
			if(listaMenuDisponibles.size() > 0) {
				for(Menu mnu : listaMenuDisponibles) {
					List<Menu> listadoPadre = menuDAO.getMenuPadre(mnu.getIdMenuPadre());
					if(listadoPadre.size() > 0) {						
						mnu.setPresentacion(listadoPadre.get(0).getDescripcion() + " --> " + mnu.getDescripcion());
					}
				}
			}
			lstPermisosTodos.setModel(new ListModelList(listaMenuDisponibles));
		}catch(Exception ex) {

		}
	}
	public List<Perfil> getListaPerfiles(){
		return perfilDAO.getListaPerfilesActivos();
	}

}
