package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class PerfilDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Perfil> getListaPerfiles() {
		List<Perfil> retorno = new ArrayList<Perfil>();
		Query query = getEntityManager().createNamedQuery("Perfil.findAll");
		retorno = (List<Perfil>) query.getResultList();
		return retorno;
	}
	@SuppressWarnings("unchecked")
	public List<Perfil> getListaPerfilesActivos() {
		List<Perfil> retorno = new ArrayList<Perfil>();
		Query query = getEntityManager().createNamedQuery("Perfil.findPerfilesActivos");
		retorno = (List<Perfil>) query.getResultList();
		return retorno;
	}
	@SuppressWarnings("unchecked")
	public List<Perfil> getListaPerfilesBuscar(String value) {
		List<Perfil> resultado = new ArrayList<Perfil>(); 
		Query query = getEntityManager().createNamedQuery("Perfil.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Perfil>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Perfil> getPerfilPorId(Integer idPerfil) {
		List<Perfil> resultado = new ArrayList<Perfil>(); 
		Query query = getEntityManager().createNamedQuery("Perfil.buscarId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPerfil", idPerfil);
		resultado = (List<Perfil>) query.getResultList();
		return resultado;
	}
}
