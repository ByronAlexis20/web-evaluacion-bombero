package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class PeriodoDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Periodo> getListaPeriodos(String value) {
		List<Periodo> resultado = new ArrayList<Periodo>(); 
		Query query = getEntityManager().createNamedQuery("Periodo.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Periodo>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Periodo> getPeriodoById(Integer idPeriodo) {
		List<Periodo> resultado = new ArrayList<Periodo>(); 
		Query query = getEntityManager().createNamedQuery("Periodo.buscarPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPeriodo",idPeriodo);
		resultado = (List<Periodo>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Periodo> getPeriodoEvaluacionIniciado(String estado) {
		List<Periodo> resultado = new ArrayList<Periodo>(); 
		Query query = getEntityManager().createNamedQuery("Periodo.buscarPorEvaluacionIniciado");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("estado", estado);
		resultado = (List<Periodo>) query.getResultList();
		return resultado;
	}
}
