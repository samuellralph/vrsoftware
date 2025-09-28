package com.vrsoftware.vrspedidos.controller;

import com.vrsoftware.vrspedidos.model.Pedido;
import com.vrsoftware.vrspedidos.service.interfaces.IPedidoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    @Autowired
    private IPedidoService pedidoService;

    @PostMapping
    public ResponseEntity<?> criarPedido(@Valid @RequestBody Pedido pedido, BindingResult bindingResult) {
        logger.info("Recebida requisição para criar pedido: {}", pedido);
        
        if (bindingResult.hasErrors()) {
            Map<String, String> erros = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> 
                erros.put(error.getField(), error.getDefaultMessage())
            );
            
            logger.warn("Validação falhou para pedido: {}", erros);
            return ResponseEntity.badRequest().body(Map.of(
                "erro", "Dados inválidos",
                "detalhes", erros
            ));
        }

        try {
            UUID idPedido = pedidoService.processarPedido(pedido);
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("id", idPedido);
            resposta.put("status", "ACEITO");
            resposta.put("mensagem", "Pedido recebido e será processado assincronamente");
            
            logger.info("Pedido {} criado com sucesso", idPedido);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(resposta);
            
        } catch (Exception e) {
            logger.error("Erro ao processar pedido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "erro", "Erro interno do servidor",
                "mensagem", "Não foi possível processar o pedido"
            ));
        }
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> consultarStatusPedido(@PathVariable UUID id) {
        logger.info("Consultando status do pedido: {}", id);
        
        String status = pedidoService.obterStatusPedido(id);
        
        if ("PEDIDO_NAO_ENCONTRADO".equals(status)) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("idPedido", id);
        resposta.put("status", status);
        
        return ResponseEntity.ok(resposta);
    }
}