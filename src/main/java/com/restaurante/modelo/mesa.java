package com.restaurante.modelo;

public class mesa {
    private int idMesa;
    private String codigo;
    private int capacidad;

    public mesa() {}

    public mesa(int idMesa, String codigo, int capacidad) {
        this.idMesa    = idMesa;
        this.codigo    = codigo;
        this.capacidad = capacidad;
    }

    public int getIdMesa() { return idMesa; }
    public void setIdMesa(int idMesa) { this.idMesa = idMesa; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
}