package com.RFF.VN;

public class OpcionDTO {
	public int idOpcion;
	public String texto;
	public int idDestino;
	public Integer idLogro;
	
	public OpcionDTO(int idOpcion, String texto, int idDestino, Integer idLogro) {
		this.idOpcion = idOpcion;
		this.texto = texto;
		this.idDestino = idDestino;
		this.idLogro = idLogro;
	}
}
