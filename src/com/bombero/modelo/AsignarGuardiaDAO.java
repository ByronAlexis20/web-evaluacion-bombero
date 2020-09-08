package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class AsignarGuardiaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<AsignarGuardia> getListaGuardias(Integer idCompania) {
		List<AsignarGuardia> resultado = new ArrayList<AsignarGuardia>(); 
		Query query = getEntityManager().createNamedQuery("AsignarGuardia.buscarPorCompania");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idCompania", idCompania);
		resultado = (List<AsignarGuardia>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<AsignarGuardia> getListaAsignaciones() {
		List<AsignarGuardia> resultado = new ArrayList<AsignarGuardia>(); 
		Query query = getEntityManager().createNamedQuery("AsignarGuardia.buscarAsignacion");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<AsignarGuardia>) query.getResultList();
		return resultado;
	}
}
