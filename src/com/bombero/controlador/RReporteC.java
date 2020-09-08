package com.bombero.controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;

import com.bombero.modelo.Periodo;
import com.bombero.modelo.PeriodoDAO;
import com.bombero.util.PrintReport;

@SuppressWarnings("serial")
public class RReporteC extends SelectorComposer<Component>{
	PeriodoDAO periodoDAO = new PeriodoDAO();
	@Wire Combobox cboPeriodoAnual;
	@Wire Combobox cboPeriodoMejores;
	@Wire Combobox cboPeriodoMejor;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
	}
	@Listen("onClick=#btnImprimirReporteAnual")
	public void imprimirReporteAnual() {
		try {
			if(cboPeriodoAnual.getSelectedItem() == null) {
				Clients.showNotification("Debe seleccionar un periodo");
				return;
			}
			Periodo periodo = (Periodo)cboPeriodoAnual.getSelectedItem().getValue();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ID_PERIODO",periodo.getIdPeriodo());
			params.put("PERIODO","PERIODO: " + periodo.getDescripcion());
			PrintReport obj = new PrintReport();
			obj.showViewReport("/reportes/rptReporteAnual.jasper", params, periodoDAO);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Listen("onClick=#btnImprimirReporteMejores")
	public void imprimirReporteMejores() {
		try {
			if(cboPeriodoMejores.getSelectedItem() == null) {
				Clients.showNotification("Debe seleccionar un periodo");
				return;
			}
			Periodo periodo = (Periodo)cboPeriodoMejores.getSelectedItem().getValue();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ID_PERIODO",periodo.getIdPeriodo());
			params.put("PERIODO","PERIODO: " + periodo.getDescripcion());
			PrintReport obj = new PrintReport();
			obj.showViewReport("/reportes/rptReporteMejores.jasper", params, periodoDAO);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Listen("onClick=#btnImprimirReporteMejor")
	public void imprimirReporteMejor() {
		try {
			if(cboPeriodoMejor.getSelectedItem() == null) {
				Clients.showNotification("Debe seleccionar un periodo");
				return;
			}
			Periodo periodo = (Periodo)cboPeriodoMejor.getSelectedItem().getValue();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ID_PERIODO",periodo.getIdPeriodo());
			params.put("PERIODO","PERIODO: " + periodo.getDescripcion());
			PrintReport obj = new PrintReport();
			obj.showViewReport("/reportes/rptReporteMejor.jasper", params, periodoDAO);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	public List<Periodo> getListaPeriodoAnual(){
		List<Periodo> lista = periodoDAO.getListaPeriodos("");
		return lista;
	}
	public List<Periodo> getListaPeriodoMejores(){
		List<Periodo> lista = periodoDAO.getListaPeriodos("");
		return lista;
	}
	public List<Periodo> getListaPeriodoMejor(){
		List<Periodo> lista = periodoDAO.getListaPeriodos("");
		return lista;
	}
}
