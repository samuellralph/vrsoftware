package com.vrsoftware.vrspedidos.model;

public enum StatusEnum {
    RECEBIDO("Pedido recebido"),
    PROCESSANDO("Pedido em processamento"),
    SUCESSO("Pedido processado com sucesso"),
    FALHA("Falha no processamento do pedido");
    
    private final String descricao;
    
    StatusEnum(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}