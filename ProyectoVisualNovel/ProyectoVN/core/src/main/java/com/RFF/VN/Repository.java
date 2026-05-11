package com.RFF.VN;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository {

    public int registrarUsuario(String nombre, String password, String pregunta, String respuesta) {
        String passwordHasheada = Seguridad.hashear(password);
        String respuestaHasheada = Seguridad.hashear(respuesta); 
        
        String sql = "INSERT INTO USUARIO (Nombre_usuario, Password_usuario, Pregunta_seguridad, Respuesta_seguridad) VALUES (?, ?, ?, ?)";
       
        try (PreparedStatement stmt = ConexionBD.getConnProgreso().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, nombre);
            stmt.setString(2, passwordHasheada);
            stmt.setString(3, pregunta);
            stmt.setString(4, respuestaHasheada);
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1); 
            }
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) return -1; 
            e.printStackTrace();
        }
        return -2; 
    }
    
    public int validarLogin(String nombre, String password) {
        String sql = "SELECT Id_usuario, Password_usuario FROM USUARIO WHERE Nombre_usuario = ?";
        
        try (PreparedStatement stmt = ConexionBD.getConnProgreso().prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashBD = rs.getString("Password_usuario");
                    if (Seguridad.verificar(password, hashBD)) {
                        return rs.getInt("Id_usuario");
                    } else {
                        return -1; 
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; 
    }
    
    public String obtenerPreguntaUsuario(String nombreUsuario) {
        String sql = "SELECT Pregunta_seguridad FROM USUARIO WHERE Nombre_usuario = ?";
        
        try (PreparedStatement stmt = ConexionBD.getConnProgreso().prepareStatement(sql)) {
            stmt.setString(1, nombreUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Pregunta_seguridad");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
    
    public boolean actualizarPassword(String nombre, String respuestaEntrada, String nuevaPass) {
        String sql = "SELECT Respuesta_seguridad FROM USUARIO WHERE Nombre_usuario = ?";
        
        try (PreparedStatement stmt = ConexionBD.getConnProgreso().prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String respuestaBD = rs.getString("Respuesta_seguridad");
                    if (Seguridad.verificar(respuestaEntrada, respuestaBD)) {
                        String sqlUpdate = "UPDATE USUARIO SET Password_usuario = ? WHERE Nombre_usuario = ?";
                        try (PreparedStatement stmtUp = ConexionBD.getConnProgreso().prepareStatement(sqlUpdate)) {
                            stmtUp.setString(1, Seguridad.hashear(nuevaPass));
                            stmtUp.setString(2, nombre);
                            return stmtUp.executeUpdate() > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public int obtenerUltimoCapitulo(int idUsuario) {
        String sql = "SELECT Ult_capitulo FROM USUARIO WHERE Id_usuario = ?";
        try (PreparedStatement stmt = ConexionBD.getConnProgreso().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Ult_capitulo");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; 
    }
    
    public void actualizarProgreso(int idUsuario, int idCapitulo) {
        String sql = "UPDATE USUARIO SET Ult_capitulo = ? WHERE Id_usuario = ?";
        try (PreparedStatement stmt = ConexionBD.getConnProgreso().prepareStatement(sql)) {
            stmt.setInt(1, idCapitulo);
            stmt.setInt(2, idUsuario);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
  
    
    public NarracionDTO obtenerNarracion(int id) {
        String sql = "SELECT Id_narracion, Id_capitulo, Descripcion, Id_nar_post, Fondo, Personaje_Izq, Personaje_Der, Musica, Sonido_Efecto FROM NARRACION WHERE Id_narracion = ?";
        try (PreparedStatement stmt = ConexionBD.getConnHistoria().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int sig = rs.getInt("Id_nar_post");
                    Integer idSig = rs.wasNull() ? null : sig;
                    
                    return new NarracionDTO(
                        rs.getString("Descripcion"),
                        idSig,
                        rs.getInt("Id_capitulo"),
                        rs.getString("Fondo"),
                        rs.getString("Personaje_Izq"),
                        rs.getString("Personaje_Der"),
                        rs.getString("Musica"),
                        rs.getString("Sonido_Efecto")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int obtenerIdInicialPorCapitulo(int idCapitulo) {
        String sql = "SELECT Id_narracion FROM NARRACION WHERE Id_capitulo = ? ORDER BY Id_narracion ASC LIMIT 1";
        try (PreparedStatement stmt = ConexionBD.getConnHistoria().prepareStatement(sql)) {
            stmt.setInt(1, idCapitulo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("Id_narracion");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<OpcionDTO> obtenerOpciones(int idNarracionActual) {
        List<OpcionDTO> lista = new ArrayList<>();
        String sql = "SELECT o.Id_opcion, o.Descripcion, o.Id_narracion_post, l.Id_logro FROM OPCION o LEFT JOIN LOGRO l ON o.Id_opcion = l.Id_opcion WHERE o.Id_narracion_ant = ?";
        
        try (PreparedStatement stmt = ConexionBD.getConnHistoria().prepareStatement(sql)) {
            stmt.setInt(1, idNarracionActual);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idLog = rs.getInt("Id_logro");
                    Integer idLogroFinal = rs.wasNull() ? null : idLog;
                    lista.add(new OpcionDTO(rs.getInt("Id_opcion"), rs.getString("Descripcion"), rs.getInt("Id_narracion_post"), idLogroFinal));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean registrarLogro(int idUsuario, int idLogro) {
        String sql = "INSERT INTO LOGRO_CONSEGUIDO (Id_usuario, Id_logro) VALUES (?, ?)";
        try (PreparedStatement stmt = ConexionBD.getConnProgreso().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idLogro);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public String obtenerNombreLogro(int idLogro) {
        String sql = "SELECT Nombre FROM LOGRO WHERE Id_logro = ?";
        try (PreparedStatement stmt = ConexionBD.getConnHistoria().prepareStatement(sql)) {
            stmt.setInt(1, idLogro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("Nombre");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Logro Desbloqueado";
    }

    public List<LogroDetalleDTO> obtenerListaLogros(int idUsuario) {
        List<LogroDetalleDTO> lista = new ArrayList<>();
        String sqlLogros = "SELECT Id_logro, Nombre, Descripcion FROM LOGRO ORDER BY Id_logro ASC";
        String sqlProgreso = "SELECT Id_logro, Fecha FROM LOGRO_CONSEGUIDO WHERE Id_usuario = ?";

        try {
            Map<Integer, String> fechasLogros = new HashMap<>();
            try (PreparedStatement stmtP = ConexionBD.getConnProgreso().prepareStatement(sqlProgreso)) {
                stmtP.setInt(1, idUsuario);
                try (ResultSet rsP = stmtP.executeQuery()) {
                    while (rsP.next()) {
                        fechasLogros.put(rsP.getInt("Id_logro"), rsP.getString("Fecha"));
                    }
                }
            }

            try (PreparedStatement stmtH = ConexionBD.getConnHistoria().prepareStatement(sqlLogros)) {
                try (ResultSet rsH = stmtH.executeQuery()) {
                    while (rsH.next()) {
                        int id = rsH.getInt("Id_logro");
                        String fecha = fechasLogros.get(id);
                        lista.add(new LogroDetalleDTO(rsH.getString("Nombre"), rsH.getString("Descripcion"), fecha, (fecha != null)));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public void guardarDecision(int idUsuario, int idOpcion) {
        String sql = "INSERT INTO HISTORIAL_OPCIONES (Id_usuario, Id_opcion) VALUES (?,?)";
        try (PreparedStatement stmt = ConexionBD.getConnProgreso().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idOpcion);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void borrarHistorialUsuario(int idUsuario) {
        String sql = "DELETE FROM HISTORIAL_OPCIONES WHERE Id_usuario = ?";
        try (PreparedStatement stmt = ConexionBD.getConnProgreso().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean haElegidoOpcion(int idUsuario, int idOpcionBuscada) {
        String sql = "SELECT 1 FROM HISTORIAL_OPCIONES WHERE Id_usuario = ? AND Id_opcion = ? LIMIT 1";
        try (PreparedStatement stmt = ConexionBD.getConnProgreso().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idOpcionBuscada);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    } 
}