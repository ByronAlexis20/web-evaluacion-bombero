package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class CompaniaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Compania> getListaCompania(String value) {
		List<Compania> resultado = new ArrayList<Compania>(); 
		Query query = getEntityManager().createNamedQuery("Compania.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Compania>) query.getResultList();
		return resultado;
	}
}
