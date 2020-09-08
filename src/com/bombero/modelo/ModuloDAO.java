package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class ModuloDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Modulo> getListaModulo(String value) {
		List<Modulo> resultado = new ArrayList<Modulo>(); 
		Query query = getEntityManager().createNamedQuery("Modulo.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Modulo>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Modulo> getListaModuloById(Integer idModulo) {
		List<Modulo> resultado = new ArrayList<Modulo>(); 
		Query query = getEntityManager().createNamedQuery("Modulo.buscarPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idModulo",idModulo);
		resultado = (List<Modulo>) query.getResultList();
		return resultado;
	}
}
