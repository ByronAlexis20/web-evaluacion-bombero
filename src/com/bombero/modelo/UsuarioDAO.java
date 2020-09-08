package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


public class UsuarioDAO extends ClaseDAO{
	public Usuario getUsuario(String nombreUsuario) {
		Usuario usuario; 
		Query consulta;
		consulta = getEntityManager().createNamedQuery("Usuario.buscaUsuario");
		consulta.setParameter("nombreUsuario", nombreUsuario);
		usuario = (Usuario) consulta.getSingleResult();
		return usuario;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> getListausuarioBuscar(String value) {
		List<Usuario> resultado = new ArrayList<Usuario>(); 
		Query query = getEntityManager().createNamedQuery("Usuario.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Usuario>) query.getResultList();
		return resultado;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Usuario> getBuscarUsuario(String value,Integer idUsuario) {
		List<Usuario> resultado = new ArrayList<Usuario>(); 
		Query query = getEntityManager().createNamedQuery("Usuario.buscarPorUsuario");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron", value.toLowerCase());
		query.setParameter("idUsuario", idUsuario);
		resultado = (List<Usuario>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> getBuscarPorIdPersona(Integer idPersona) {
		List<Usuario> resultado = new ArrayList<Usuario>(); 
		Query query = getEntityManager().createNamedQuery("Usuario.buscarPorIdPersona");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPersona",idPersona);
		resultado = (List<Usuario>) query.getResultList();
		return resultado;
	}
}
