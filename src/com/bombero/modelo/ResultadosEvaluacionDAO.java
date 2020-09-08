package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class ResultadosEvaluacionDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<ResultadosEvaluacion> getResultadosPersonas(Integer idPersona) {
		List<ResultadosEvaluacion> resultado = new ArrayList<ResultadosEvaluacion>(); 
		Query query = getEntityManager().createNamedQuery("ResultadosEvaluacion.buscarPorPersona");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPersona", idPersona);
		resultado = (List<ResultadosEvaluacion>) query.getResultList();
		return resultado;
	}

}
