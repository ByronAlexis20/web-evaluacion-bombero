package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class EquiposVestimentaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<EquiposVestimenta> getListaEquipos(String value) {
		List<EquiposVestimenta> resultado = new ArrayList<EquiposVestimenta>(); 
		Query query = getEntityManager().createNamedQuery("EquiposVestimenta.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<EquiposVestimenta>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<EquiposVestimenta> getEquipoById(Integer idEquipo) {
		List<EquiposVestimenta> resultado = new ArrayList<EquiposVestimenta>(); 
		Query query = getEntityManager().createNamedQuery("EquiposVestimenta.buscarPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEquipo",idEquipo);
		resultado = (List<EquiposVestimenta>) query.getResultList();
		return resultado;
	}
}
