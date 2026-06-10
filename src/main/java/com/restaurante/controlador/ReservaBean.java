package com.restaurante.controlador;

import com.restaurante.dao.reservaDAO;
import com.restaurante.modelo.reserva;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;

@Named("reservaBean")
@SessionScoped
public class ReservaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String  nombreCliente    = "";
    private String  mesaSeleccionada = "";
    private int     numPersonas      = 0;
    private String  fecha            = "";
    private String  hora             = "";
    private double  anticipo         = 0;
    private double  costoMesa        = 0;
    private int     idEdicion        = -1;
    private boolean mostrarRegistros = false;

    private List<reserva> listaReservas;
    private reservaDAO dao = new reservaDAO();

    @PostConstruct
    public void init() {
        dao.eliminarReservasExpiradas();
        cargarReservas();
    }

    // ── GUARDAR / ACTUALIZAR ──────────────────────────────
    public void guardar() {
        if (nombreCliente == null || nombreCliente.trim().isEmpty()
         || mesaSeleccionada == null || mesaSeleccionada.isEmpty()
         || fecha == null || fecha.isEmpty()
         || hora  == null || hora.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }

        nombreCliente = formatearNombre(nombreCliente);

        try {
            LocalDate fechaLD = LocalDate.parse(fecha);
            LocalTime horaLT  = LocalTime.parse(hora);

            if (fechaLD.isBefore(LocalDate.now())) {
                mostrarError("No se pueden hacer reservas en fechas pasadas.");
                return;
            }

            if (horaLT.isBefore(LocalTime.of(12, 0)) || horaLT.isAfter(LocalTime.of(22, 0))) {
                mostrarError("Horario permitido: 12:00 a 22:00");
                return;
            }

            if (costoMesa > 0 && anticipo > costoMesa) {
                mostrarError("El anticipo ($" + anticipo + ") no puede ser mayor al costo de la mesa ($" + costoMesa + ")");
                return;
            }

            if (idEdicion == -1) {
                List<String> ocupadas = dao.obtenerHorasOcupadas(mesaSeleccionada, fechaLD);
                for (String h : ocupadas) {
                    LocalTime hExist = LocalTime.parse(h);
                    if (horaLT.isBefore(hExist.plusHours(2)) && horaLT.plusHours(2).isAfter(hExist)) {
                        mostrarError("La mesa " + mesaSeleccionada + " está ocupada en ese rango de 2 horas.");
                        return;
                    }
                }
            }

            reserva r = new reserva();
            r.setNombreCliente(nombreCliente.trim());
            r.setMesa(mesaSeleccionada);
            r.setNumPersonas(numPersonas);
            r.setFecha(fechaLD);
            r.setHora(horaLT);
            r.setAnticipo(anticipo);

            if (idEdicion == -1) {
                dao.guardarReserva(r);
                mostrarExito("✅ Reserva guardada con éxito para " + nombreCliente);
            } else {
                r.setIdReserva(idEdicion);
                dao.actualizarReserva(r);
                mostrarExito("✅ Reserva actualizada con éxito.");
                idEdicion = -1;
            }

            mostrarRegistros = true;
cargarReservas();
limpiarCampos();

        } catch (Exception e) {
            mostrarError("Formato inválido. Fecha: yyyy-MM-dd  Hora: HH:mm");
        }
    }

    private String formatearNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) return nombre;
        String[] palabras = nombre.split(" ", -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < palabras.length; i++) {
            String p = palabras[i];
            if (!p.isEmpty()) {
                sb.append(p.substring(0, 1).toUpperCase());
                if (p.length() > 1) sb.append(p.substring(1).toLowerCase());
            }
            if (i < palabras.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    public void consultar() {
        cargarReservas();
        mostrarRegistros = true;
    }

    public void ocultarRegistros() {
        mostrarRegistros = false;
    }

    public void seleccionar(reserva r) {
        idEdicion        = r.getIdReserva();
        nombreCliente    = r.getNombreCliente();
        mesaSeleccionada = r.getMesa();
        numPersonas      = r.getNumPersonas();
        fecha            = r.getFecha().toString();
        hora             = r.getHoraFormato();
        anticipo         = r.getAnticipo();
        switch (r.getMesa()) {
            case "M7":            costoMesa = 100.0; break;
            default:              costoMesa = 50.0; break;
        }
        mostrarRegistros = false;
    }

    public void eliminar(int id) {
        dao.eliminarReserva(id);
        cargarReservas();
        mostrarExito("Reserva eliminada.");
    }

    public void eliminarTodas() {
        dao.eliminarTodasLasReservas();
        cargarReservas();
        mostrarExito("Todas las reservas eliminadas.");
    }

    public void limpiar() {
        limpiarCampos();
        mostrarRegistros = false;
    }

    private void limpiarCampos() {
        nombreCliente    = "";
        mesaSeleccionada = "";
        numPersonas      = 0;
        fecha            = "";
        hora             = "";
        anticipo         = 0;
        costoMesa        = 0;
        idEdicion        = -1;
    }

    private void cargarReservas() {
        listaReservas = dao.obtenerReservas();
    }

    private void mostrarError(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private void mostrarExito(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    public String getFechaMinima() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // Getters y Setters
    public String  getNombreCliente()    { return nombreCliente; }
    public void    setNombreCliente(String v)    { this.nombreCliente = v; }
    public String  getMesaSeleccionada() { return mesaSeleccionada; }
    public void    setMesaSeleccionada(String v) { this.mesaSeleccionada = v; }
    public int     getNumPersonas()      { return numPersonas; }
    public void    setNumPersonas(int v)      { this.numPersonas = v; }
    public String  getFecha()            { return fecha; }
    public void    setFecha(String v)    { this.fecha = v; }
    public String  getHora()             { return hora; }
    public void    setHora(String v)     { this.hora = v; }
    public double  getAnticipo()         { return anticipo; }
    public void    setAnticipo(double v) { this.anticipo = v; }
    public double  getCostoMesa()        { return costoMesa; }
    public void    setCostoMesa(double v){ this.costoMesa = v; }
    public boolean isMostrarRegistros()  { return mostrarRegistros; }
    public void    setMostrarRegistros(boolean v) { this.mostrarRegistros = v; }
public List<reserva> getListaReservas() {
    if (listaReservas == null) cargarReservas();
    return listaReservas;
}
}