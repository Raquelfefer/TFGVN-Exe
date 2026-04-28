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
            // 1. REGISTRAR EL DRIVER (Esto es lo que le faltaba al .exe para "despertar")
            Class.forName("org.h2.Driver");

            // 2. RUTA BASE Y CARPETA (Mantenemos tu lógica)
            String rutaBase = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Laurie's Game";
            File carpeta = new File(rutaBase);
            if (!carpeta.exists()) carpeta.mkdirs();

            // 3. COPIA DE LA BASE DE DATOS (Mantenemos tu lógica de LibGDX)
            File archivoHistoriaLocal = new File(rutaBase, "datos_juego.mv.db");
            
            if (!archivoHistoriaLocal.exists()) {
                // Buscamos dentro del JAR y copiamos fuera a Documentos
                com.badlogic.gdx.files.FileHandle assetDB = com.badlogic.gdx.Gdx.files.internal("db/datos_juego.mv.db");
                com.badlogic.gdx.files.FileHandle destinoDB = com.badlogic.gdx.Gdx.files.absolute(archivoHistoriaLocal.getAbsolutePath());
                assetDB.copyTo(destinoDB);
                System.out.println("Archivo de historia copiado a Documentos.");
            }

            // 4. LIMPIEZA DE RUTA PARA H2
            // Cambiamos las contra-barras "\" por "/" porque H2 es más estable así en las URLs
            String rutaLimpia = rutaBase.replace("\\", "/");

            // 5. CONEXIÓN A HISTORIA (Solo lectura)
            String urlHistoria = "jdbc:h2:file:" + rutaLimpia + "/datos_juego;ACCESS_MODE_DATA=r";
            connHistoria = DriverManager.getConnection(urlHistoria, "", "");

            // 6. CONEXIÓN A PROGRESO (Escritura)
            // Quitamos el DB_CLOSE_ON_EXIT para evitar el error de "Feature not supported"
            String urlProgreso = "jdbc:h2:file:" + rutaLimpia + "/progreso;AUTO_SERVER=TRUE";
            connProgreso = DriverManager.getConnection(urlProgreso, "", "");

            // 7. INICIALIZACIÓN
            crearTablasProgreso();
            System.out.println("¡Bases de datos conectadas con éxito en: " + rutaLimpia);

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
