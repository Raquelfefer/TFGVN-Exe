package com.RFF.VN;

import org.mindrot.jbcrypt.BCrypt;

public class Seguridad {
	// Convierte lo introducido a formato Hash
	public static String hashear(String texto) {
		return BCrypt.hashpw(texto, BCrypt.gensalt());
	}
	
	// Compara el texto introducido con el hash de base de datos
	public static boolean verificar(String texto, String hashed) {
		try {
			return BCrypt.checkpw(texto, hashed);
		} catch (Exception e) {
			return false;
		}
	}
}
