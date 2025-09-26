package com.vrsoftware.vrspedidos.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public class StatusPedido {
    
    private UUID idPedido;
    private StatusEnum status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataProcessamento;
    
    private String mensagemErro;
    
    public StatusPedido() {
        this.dataProcessamento = LocalDateTime.now();
    }
    
    public StatusPedido(UUID idPedido, StatusEnum status) {
        this();
        this.idPedido = idPedido;
        this.status = status;
    }
    
    public StatusPedido(UUID idPedido, StatusEnum status, String mensagemErro) {
        this(idPedido, status);
        this.mensagemErro = mensagemErro;
    }
    
    // Getters and Setters
    public UUID getIdPedido() {
        return idPedido;
    }
    
    public void setIdPedido(UUID idPedido) {
        this.idPedido = idPedido;
    }
    
    public StatusEnum getStatus() {
        return status;
    }
    
    public void setStatus(StatusEnum status) {
        this.status = status;
    }
    
    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }
    
    public void setDataProcessamento(LocalDateTime dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }
    
    public String getMensagemErro() {
        return mensagemErro;
    }
    
    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }
    
    @Override
    public String toString() {
        return "StatusPedido{" +
                "idPedido=" + idPedido +
                ", status=" + status +
                ", dataProcessamento=" + dataProcessamento +
                ", mensagemErro='" + mensagemErro + '\'' +
                '}';
    }
}