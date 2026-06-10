package com.restaurante.dao;

import com.restaurante.modelo.reserva;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class reservaDAO {

    public void guardarReserva(reserva r) {
        String sql = "INSERT INTO reserva (nombre_cliente, id_mesa, num_personas, fecha, hora, anticipo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getNombreCliente());
            ps.setInt(2, obtenerIdMesa(r.getMesa(), con));
            ps.setInt(3, r.getNumPersonas());
            ps.setDate(4, Date.valueOf(r.getFecha()));
            ps.setTime(5, Time.valueOf(r.getHora()));
            ps.setDouble(6, r.getAnticipo());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    public List<reserva> obtenerReservas() {
        List<reserva> lista = new ArrayList<>();
        String sql = "SELECT r.id_reserva, r.nombre_cliente, m.codigo, " +
                     "r.num_personas, r.fecha, r.hora, r.anticipo " +
                     "FROM reserva r JOIN mesa m ON r.id_mesa = m.id_mesa " +
                     "ORDER BY r.fecha, r.hora";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new reserva(
                    rs.getInt("id_reserva"),
                    rs.getString("nombre_cliente"),
                    rs.getString("codigo"),
                    rs.getInt("num_personas"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getTime("hora").toLocalTime(),
                    rs.getDouble("anticipo")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas: " + e.getMessage());
        }
        return lista;
    }

    public void eliminarReserva(int id) {
        String sql = "DELETE FROM reserva WHERE id_reserva = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar: " + e.getMessage());
        }
    }

    public void eliminarTodasLasReservas() {
        String sql = "DELETE FROM reserva";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar todas: " + e.getMessage());
        }
    }

    public void actualizarReserva(reserva r) {
        String sql = "UPDATE reserva SET nombre_cliente=?, id_mesa=?, " +
                     "num_personas=?, fecha=?, hora=?, anticipo=? WHERE id_reserva=?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getNombreCliente());
            ps.setInt(2, obtenerIdMesa(r.getMesa(), con));
            ps.setInt(3, r.getNumPersonas());
            ps.setDate(4, Date.valueOf(r.getFecha()));
            ps.setTime(5, Time.valueOf(r.getHora()));
            ps.setDouble(6, r.getAnticipo());
            ps.setInt(7, r.getIdReserva());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
        }
    }

    public List<String> obtenerHorasOcupadas(String codigoMesa, LocalDate fecha) {
        List<String> horas = new ArrayList<>();
        String sql = "SELECT r.hora FROM reserva r JOIN mesa m ON r.id_mesa = m.id_mesa " +
                     "WHERE m.codigo = ? AND r.fecha = ?";
        try (Connection con = conexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoMesa);
            ps.setDate(2, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                horas.add(rs.getTime("hora").toLocalTime().toString());
        } catch (SQLException e) {
            System.err.println("Error horas ocupadas: " + e.getMessage());
        }
        return horas;
    }

    private int obtenerIdMesa(String codigo, Connection con) throws SQLException {
        String sql = "SELECT id_mesa FROM mesa WHERE codigo = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id_mesa");
        }
        return 1;
    }
}