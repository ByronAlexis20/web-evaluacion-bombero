package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class PreguntaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Pregunta> getPreguntaId(Integer idPregunta) {
		List<Pregunta> resultado = new ArrayList<Pregunta>(); 
		Query query = getEntityManager().createNamedQuery("Pregunta.buscarPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPregunta",idPregunta);
		resultado = (List<Pregunta>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Pregunta> getPreguntaUltima() {
		List<Pregunta> resultado = new ArrayList<Pregunta>(); 
		Query query = getEntityManager().createNamedQuery("Pregunta.buscarUltimo");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Pregunta>) query.getResultList();
		return resultado;
	}
}
