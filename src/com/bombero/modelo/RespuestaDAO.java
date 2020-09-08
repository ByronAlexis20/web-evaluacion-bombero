package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class RespuestaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Respuesta> getRespuestaById(Integer idRespuesta) {
		List<Respuesta> resultado = new ArrayList<Respuesta>(); 
		Query query = getEntityManager().createNamedQuery("Respuesta.buscarPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idRespuesta", idRespuesta);
		resultado = (List<Respuesta>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Respuesta> getRespuestaByPregunta(Integer idPregunta) {
		List<Respuesta> resultado = new ArrayList<Respuesta>(); 
		Query query = getEntityManager().createNamedQuery("Respuesta.buscarPorPregunta");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPregunta", idPregunta);
		resultado = (List<Respuesta>) query.getResultList();
		return resultado;
	}
}
