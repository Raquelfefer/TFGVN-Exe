package com.RFF.VN;

public class NarracionDTO {
	public String descripcion;
	public Integer idSiguiente;
	public int idCapitulo;
	public String fondo;
	public String personajeIzq;
	public String personajeDer;
	public String musica;
	public String sonidoEfecto;
	
	public NarracionDTO(String descripcion, Integer idSiguiente, int idCapitulo, String fondo, String personajeIzq, String personajeDer, String musica, String sonidoEfecto) {
		this.descripcion = descripcion;
		this.idSiguiente = idSiguiente;
		this.idCapitulo = idCapitulo;
		this.fondo = fondo;
		this.personajeIzq = personajeIzq;
		this.personajeDer = personajeDer;
		this.musica = musica;
		this.sonidoEfecto = sonidoEfecto;
	}
}
