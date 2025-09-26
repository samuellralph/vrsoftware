package com.vrsoftware.pedidos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Classe modelo para representar um pedido
 */
public class Pedido {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("produto")
    private String produto;
    
    @JsonProperty("quantidade")
    private int quantidade;
    
    @JsonProperty("status")
    private String status;
    
    // Construtor padrão para Jackson
    public Pedido() {
        this.id = UUID.randomUUID().toString();
        this.status = "ENVIADO, AGUARDANDO PROCESSO";
    }
    
    // Construtor com parâmetros
    public Pedido(String produto, int quantidade) {
        this();
        this.produto = produto;
        this.quantidade = quantidade;
    }
    
    // Getters e Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getProduto() {
        return produto;
    }
    
    public void setProduto(String produto) {
        this.produto = produto;
    }
    
    public int getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Pedido{" +
                "id='" + id + '\'' +
                ", produto='" + produto + '\'' +
                ", quantidade=" + quantidade +
                ", status='" + status + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Pedido pedido = (Pedido) o;
        return id != null ? id.equals(pedido.id) : pedido.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}