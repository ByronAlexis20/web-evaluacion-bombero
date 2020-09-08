package com.bombero.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class FacturaIngresoDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<FacturaIngreso> getListaFacturas(String numero) {
		List<FacturaIngreso> resultado = new ArrayList<FacturaIngreso>(); 
		Query query = getEntityManager().createNamedQuery("FacturaIngreso.buscarPorNumero");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("numero","%" + numero.toLowerCase() + "%");
		resultado = (List<FacturaIngreso>) query.getResultList();
		return resultado;
	}
}
