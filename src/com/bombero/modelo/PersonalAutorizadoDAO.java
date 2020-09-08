package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class PersonalAutorizadoDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<PersonalAutorizado> getPersonalAutorizado() {
		List<PersonalAutorizado> resultado = new ArrayList<PersonalAutorizado>(); 
		Query query = getEntityManager().createNamedQuery("PersonalAutorizado.findAll");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<PersonalAutorizado>) query.getResultList();
		return resultado;
	}

}
