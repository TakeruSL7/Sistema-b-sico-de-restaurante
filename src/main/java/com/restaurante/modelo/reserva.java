package com.restaurante.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class reserva {
    private int idReserva;
    private String nombreCliente;
    private String mesa;
    private int numPersonas;
    private LocalDate fecha;
    private LocalTime hora;
    private double anticipo;

    public reserva() {}

    public reserva(int idReserva, String nombreCliente, String mesa,
                   int numPersonas, LocalDate fecha, LocalTime hora, double anticipo) {
        this.idReserva     = idReserva;
        this.nombreCliente = nombreCliente;
        this.mesa          = mesa;
        this.numPersonas   = numPersonas;
        this.fecha         = fecha;
        this.hora          = hora;
        this.anticipo      = anticipo;
    }

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int id) { this.idReserva = id; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String n) { this.nombreCliente = n; }
    public String getMesa() { return mesa; }
    public void setMesa(String m) { this.mesa = m; }
    public int getNumPersonas() { return numPersonas; }
    public void setNumPersonas(int n) { this.numPersonas = n; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate f) { this.fecha = f; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime h) { this.hora = h; }
    public double getAnticipo() { return anticipo; }
    public void setAnticipo(double a) { this.anticipo = a; }

    public String getHoraFormato() {
        return (hora != null) ? hora.format(DateTimeFormatter.ofPattern("HH:mm")) : "";
    }
    public String getFechaFormato() {
        return (fecha != null) ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
    public String getAnticipoFormato() {
        return String.format("$%.2f", anticipo);
    }
}