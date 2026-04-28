package com.RFF.VN;

public class LogroDetalleDTO {
	public String nombre;
	public String descripcion;
	public String fechaConseguido;
	public boolean conseguido;
	
	public LogroDetalleDTO(String nombre, String descripcion, String fechaConseguido, boolean conseguido) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fechaConseguido = fechaConseguido;
		this.conseguido = conseguido;
	}
}
