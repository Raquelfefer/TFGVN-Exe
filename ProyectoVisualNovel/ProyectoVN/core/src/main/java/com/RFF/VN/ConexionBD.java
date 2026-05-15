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

            com.badlogic.gdx.files.FileHandle assetDB = com.badlogic.gdx.Gdx.files.internal("db/datos_juego.mv.db");
            
            File archivoTemporal = File.createTempFile("historia_tmp_", ".mv.db");
            
            archivoTemporal.deleteOnExit(); 

            com.badlogic.gdx.files.FileHandle destinoTemp = com.badlogic.gdx.Gdx.files.absolute(archivoTemporal.getAbsolutePath());
            assetDB.copyTo(destinoTemp);
            
            String rutaTemporal = archivoTemporal.getAbsolutePath().replace(".mv.db", "").replace("\\", "/");
           
            String urlHistoria = "jdbc:h2:file:" + rutaTemporal + ";ACCESS_MODE_DATA=r";
            connHistoria = DriverManager.getConnection(urlHistoria, "", "");

            String rutaBase = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Laurie's Game";
            File carpeta = new File(rutaBase);
            if (!carpeta.exists()) carpeta.mkdirs();

            String rutaLimpia = rutaBase.replace("\\", "/");

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
                    + "Ult_narracion INT DEFAULT 0"
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
