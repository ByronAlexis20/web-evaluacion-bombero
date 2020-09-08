package com.bombero.util;

import com.bombero.modelo.Usuario;

public class Context {
	private final static Context instance = new Context();
	public static Context getInstance() {
		return instance;
	}
	private Usuario usuarioLogeado;

	public Usuario getUsuarioLogeado() {
		return usuarioLogeado;
	}

	public void setUsuarioLogeado(Usuario usuarioLogeado) {
		this.usuarioLogeado = usuarioLogeado;
	}
	
}
