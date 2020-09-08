package com.bombero.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the persona database table.
 * 
 */
@Entity
@Table(name="persona")
@NamedQueries({
	@NamedQuery(name="Persona.buscarPorPeriodo", query="SELECT p FROM Persona p where p.estado = 'A' and p.idPeriodo = :idPeriodo"),
	@NamedQuery(name="Persona.buscarPorId", query="SELECT p FROM Persona p where p.idPersona = :idPersona"),
	@NamedQuery(name="Persona.validarPersonaExistente", query="SELECT p FROM Persona p where p.idPersona <> :idPersona and p.cedula = :cedula and p.estado = 'A'"),
	@NamedQuery(name="Persona.validarPersonaCedula", query="SELECT p FROM Persona p where p.cedula = :cedula and p.estado = 'A'")
})
public class Persona implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_persona")
	private Integer idPersona;
	
	@Column(name="id_periodo")
	private Integer idPeriodo;

	@Column(name="antecedentes_penales")
	private String antecedentesPenales;

	private String apellido;

	@Column(name="apto_prueba_fisica")
	private String aptoPruebaFisica;

	private String cedula;

	private String celular;

	@Column(name="certificado_medico")
	private String certificadoMedico;

	private String direccion;

	private String email;

	private String estado;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_creacion")
	private Date fechaCreacion;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_modifica")
	private Date fechaModifica;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_nacimiento")
	private Date fechaNacimiento;

	private String foto;

	private String genero;

	private String nombre;

	@Column(name="referencia_domiciliaria")
	private String referenciaDomiciliaria;

	private String telefono;

	@Column(name="tipo_sangre")
	private String tipoSangre;

	@Column(name="titulo_bachiller")
	private String tituloBachiller;

	@Column(name="usuario_crea")
	private String usuarioCrea;

	@Column(name="usuario_modifica")
	private String usuarioModifica;

	//bi-directional many-to-one association to PersonalAutorizado
	@OneToMany(mappedBy="persona")
	private List<PersonalAutorizado> personalAutorizados;

	//bi-directional many-to-one association to ResultadosEvaluacion
	@OneToMany(mappedBy="persona")
	private List<ResultadosEvaluacion> resultadosEvaluacions;

	public Persona() {
	}

	public Integer getIdPersona() {
		return this.idPersona;
	}

	public void setIdPersona(Integer idPersona) {
		this.idPersona = idPersona;
	}

	public String getAntecedentesPenales() {
		return this.antecedentesPenales;
	}

	public void setAntecedentesPenales(String antecedentesPenales) {
		this.antecedentesPenales = antecedentesPenales;
	}

	public String getApellido() {
		return this.apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getAptoPruebaFisica() {
		return this.aptoPruebaFisica;
	}

	public void setAptoPruebaFisica(String aptoPruebaFisica) {
		this.aptoPruebaFisica = aptoPruebaFisica;
	}

	public String getCedula() {
		return this.cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getCelular() {
		return this.celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getCertificadoMedico() {
		return this.certificadoMedico;
	}

	public void setCertificadoMedico(String certificadoMedico) {
		this.certificadoMedico = certificadoMedico;
	}

	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Date getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaModifica() {
		return this.fechaModifica;
	}

	public void setFechaModifica(Date fechaModifica) {
		this.fechaModifica = fechaModifica;
	}

	public Date getFechaNacimiento() {
		return this.fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getFoto() {
		return this.foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getGenero() {
		return this.genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getReferenciaDomiciliaria() {
		return this.referenciaDomiciliaria;
	}

	public void setReferenciaDomiciliaria(String referenciaDomiciliaria) {
		this.referenciaDomiciliaria = referenciaDomiciliaria;
	}

	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getTipoSangre() {
		return this.tipoSangre;
	}

	public void setTipoSangre(String tipoSangre) {
		this.tipoSangre = tipoSangre;
	}

	public String getTituloBachiller() {
		return this.tituloBachiller;
	}

	public void setTituloBachiller(String tituloBachiller) {
		this.tituloBachiller = tituloBachiller;
	}

	public String getUsuarioCrea() {
		return this.usuarioCrea;
	}

	public void setUsuarioCrea(String usuarioCrea) {
		this.usuarioCrea = usuarioCrea;
	}

	public String getUsuarioModifica() {
		return this.usuarioModifica;
	}

	public void setUsuarioModifica(String usuarioModifica) {
		this.usuarioModifica = usuarioModifica;
	}

	public List<PersonalAutorizado> getPersonalAutorizados() {
		return this.personalAutorizados;
	}

	public void setPersonalAutorizados(List<PersonalAutorizado> personalAutorizados) {
		this.personalAutorizados = personalAutorizados;
	}

	public PersonalAutorizado addPersonalAutorizado(PersonalAutorizado personalAutorizado) {
		getPersonalAutorizados().add(personalAutorizado);
		personalAutorizado.setPersona(this);

		return personalAutorizado;
	}

	public PersonalAutorizado removePersonalAutorizado(PersonalAutorizado personalAutorizado) {
		getPersonalAutorizados().remove(personalAutorizado);
		personalAutorizado.setPersona(null);

		return personalAutorizado;
	}



	public List<ResultadosEvaluacion> getResultadosEvaluacions() {
		return this.resultadosEvaluacions;
	}

	public void setResultadosEvaluacions(List<ResultadosEvaluacion> resultadosEvaluacions) {
		this.resultadosEvaluacions = resultadosEvaluacions;
	}

	public ResultadosEvaluacion addResultadosEvaluacion(ResultadosEvaluacion resultadosEvaluacion) {
		getResultadosEvaluacions().add(resultadosEvaluacion);
		resultadosEvaluacion.setPersona(this);

		return resultadosEvaluacion;
	}

	public ResultadosEvaluacion removeResultadosEvaluacion(ResultadosEvaluacion resultadosEvaluacion) {
		getResultadosEvaluacions().remove(resultadosEvaluacion);
		resultadosEvaluacion.setPersona(null);

		return resultadosEvaluacion;
	}

	public Integer getIdPeriodo() {
		return idPeriodo;
	}

	public void setIdPeriodo(Integer idPeriodo) {
		this.idPeriodo = idPeriodo;
	}

}