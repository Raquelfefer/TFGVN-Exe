package com.RFF.VN;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

	private static Connection connHistoria;
    private static Connection connProgreso;

    public static void conectar() {
        try {
    
            Class.forName("org.h2.Driver");

            String rutaBase = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Laurie's Game";
            File carpeta = new File(rutaBase);
            if (!carpeta.exists()) carpeta.mkdirs();
            File archivoHistoriaLocal = new File(rutaBase, "datos_juego.mv.db");
            
            if (!archivoHistoriaLocal.exists()) {
                com.badlogic.gdx.files.FileHandle assetDB = com.badlogic.gdx.Gdx.files.internal("db/datos_juego.mv.db");
                com.badlogic.gdx.files.FileHandle destinoDB = com.badlogic.gdx.Gdx.files.absolute(archivoHistoriaLocal.getAbsolutePath());
                assetDB.copyTo(destinoDB);
            }

            String rutaLimpia = rutaBase.replace("\\", "/");

            String urlHistoria = "jdbc:h2:file:" + rutaLimpia + "/datos_juego;ACCESS_MODE_DATA=r";
            connHistoria = DriverManager.getConnection(urlHistoria, "", "");

            String urlProgreso = "jdbc:h2:file:" + rutaLimpia + "/progreso;AUTO_SERVER=TRUE";
            connProgreso = DriverManager.getConnection(urlProgreso, "", "");

            crearTablasProgreso();

        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el Driver de H2. Revisa las dependencias de Gradle.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error al conectar las bases de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void crearTablasProgreso() throws SQLException {
        try (Statement stmt = connProgreso.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS USUARIO ("
                    + "Id_usuario INT PRIMARY KEY AUTO_INCREMENT,"
                    + "Nombre_usuario VARCHAR(50) UNIQUE,"
                    + "Password_usuario VARCHAR(100),"
                    + "Pregunta_seguridad VARCHAR(255),"
                    + "Respuesta_seguridad VARCHAR(255),"
                    + "Fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "Ult_capitulo INT DEFAULT 1"
                    + ")");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS LOGRO_CONSEGUIDO ("
                    + "Id_logro_conseguido INT PRIMARY KEY AUTO_INCREMENT,"
                    + "Id_usuario INT NOT NULL,"
                    + "Id_logro INT NOT NULL,"
                    + "Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "CONSTRAINT logro_unico_por_usuario UNIQUE (Id_usuario, Id_logro),"
                    + "FOREIGN KEY (Id_usuario) REFERENCES USUARIO(Id_usuario)"
                    + ")");
            
           
            stmt.execute("CREATE TABLE IF NOT EXISTS HISTORIAL_OPCIONES ("
                    + "Id_historial INT PRIMARY KEY AUTO_INCREMENT,"
                    + "Id_usuario INT,"
                    + "Id_opcion INT,"
                    + "Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (Id_usuario) REFERENCES USUARIO(Id_usuario)"
                    + ")");
     
        }
    }

    public static Connection getConnHistoria() { return connHistoria; }
    public static Connection getConnProgreso() { return connProgreso; }
	
}
