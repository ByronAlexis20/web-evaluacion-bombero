package com.bombero.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bombero.modelo.Perfil;
import com.bombero.modelo.Permiso;
import com.bombero.modelo.Usuario;
import com.bombero.modelo.UsuarioDAO;
/**
 * Detalle de datos de los usuarios que acceden al sistema.
 * Debe implementar UserDetailsService para poder usarse como fuente de datos de Spring
 * security.
 */
public class ServicioAutenticacion implements UserDetailsService {

	/**
	 * Este metodo es invocado en el momento de la autenticacion paraa recuperar 
	 * los datos del usuario.
	 * 
	 */
	@Override
	public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
		try {
			Context.getInstance().setUsuarioLogeado(null);
			UsuarioDAO usuarioDAO = new UsuarioDAO();
			Usuario usuario; 
			User usuarioSpring;
			List<GrantedAuthority> privilegios; 
			usuario = usuarioDAO.getUsuario(nombreUsuario);
			privilegios = obtienePrivilegios(usuario.getPerfil());
			
			
			Context.getInstance().setUsuarioLogeado(usuario);
			// Construye un objeto de Spring en base a los datos del usuario de la base de datos.
			usuarioSpring = new User(usuario.getUsuario(), usuario.getClave(), privilegios);

			return usuarioSpring;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
		
	}

	/**
	 * Elabora una lista de objetos del tipo GrantedAuthority en base a los permisos
	 * del usuario.
	 * 
	 * @param usuario
	 * @return
	 */
	private List<GrantedAuthority> obtienePrivilegios(Perfil perfil) {
		List<GrantedAuthority> listaPrivilegios = new ArrayList<GrantedAuthority>(); 
		
	    for(Permiso permiso  : perfil.getPermisos())
	    	listaPrivilegios.add(new SimpleGrantedAuthority(permiso.getPerfil().getDescripcion()));

		return listaPrivilegios;
	}

}
