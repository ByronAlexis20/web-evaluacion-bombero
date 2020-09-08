package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class PersonaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Persona> getListaAspirantesPeriodo(Integer idPeriodo) {
		List<Persona> resultado = new ArrayList<Persona>(); 
		Query query = getEntityManager().createNamedQuery("Persona.buscarPorPeriodo");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPeriodo",idPeriodo);
		resultado = (List<Persona>) query.getResultList();
		return resultado;
	}
	public Persona getPersonaId(Integer idPersona) {
		Persona persona; 
		Query consulta;
		consulta = getEntityManager().createNamedQuery("Persona.buscarPorId");
		consulta.setParameter("idPersona",idPersona);
		persona = (Persona) consulta.getSingleResult();
		return persona;
	}
	@SuppressWarnings("unchecked")
	public List<Persona> validarPersonaCedula(Integer idPersona,String cedula) {
		List<Persona> resultado = new ArrayList<Persona>(); 
		Query query = getEntityManager().createNamedQuery("Persona.validarPersonaExistente");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPersona", idPersona);
		query.setParameter("cedula", cedula);
		resultado = (List<Persona>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Persona> validarPersonaCedula(String cedula) {
		List<Persona> resultado = new ArrayList<Persona>(); 
		Query query = getEntityManager().createNamedQuery("Persona.validarPersonaCedula");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("cedula", cedula);
		resultado = (List<Persona>) query.getResultList();
		return resultado;
	}
}
