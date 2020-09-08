package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class EvaluacionDAO extends ClaseDAO {
	@SuppressWarnings("unchecked")
	public List<Evaluacion> getListaEvaluacion(Integer idPeriodo, Integer idModulo) {
		List<Evaluacion> resultado = new ArrayList<Evaluacion>(); 
		Query query = getEntityManager().createNamedQuery("Evaluacion.buscarPeriodoModulo");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPeriodo", idPeriodo);
		query.setParameter("idModulo", idModulo);
		resultado = (List<Evaluacion>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Evaluacion> getEvaluacionUltimo() {
		List<Evaluacion> resultado = new ArrayList<Evaluacion>(); 
		Query query = getEntityManager().createNamedQuery("Evaluacion.buscarUltimo");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Evaluacion>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Evaluacion> getEvaluacionById(Integer idEvaluacion) {
		List<Evaluacion> resultado = new ArrayList<Evaluacion>(); 
		Query query = getEntityManager().createNamedQuery("Evaluacion.buscarPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEvaluacion", idEvaluacion);
		resultado = (List<Evaluacion>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Evaluacion> getBuscarPorPeriodo(Integer idPeriodo) {
		List<Evaluacion> resultado = new ArrayList<Evaluacion>(); 
		Query query = getEntityManager().createNamedQuery("Evaluacion.buscarPorPeriodo");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPeriodo", idPeriodo);
		resultado = (List<Evaluacion>) query.getResultList();
		return resultado;
	}
}
