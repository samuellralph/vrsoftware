package com.vrsoftware.vrspedidos.service.interfaces;

import com.vrsoftware.vrspedidos.model.Pedido;
import com.vrsoftware.vrspedidos.model.StatusEnum;

import java.util.UUID;

public interface IPedidoService {

    UUID processarPedido(Pedido pedido);

    String obterStatusPedido(UUID idPedido);

    void atualizarStatusPedido(UUID idPedido, StatusEnum status);
}