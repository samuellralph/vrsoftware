package com.vrsoftware.pedidos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrsoftware.pedidos.model.Pedido;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Serviço para comunicação HTTP com o backend
 */
public class PedidoService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    
    public PedidoService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        // URL base do backend - pode ser configurada via properties
        this.baseUrl = "http://localhost:8080/api/pedidos";
    }
    
    public PedidoService(String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
    }
    
    /**
     * Envia um pedido para o backend
     * @param pedido O pedido a ser enviado
     * @return true se o envio foi bem-sucedido, false caso contrário
     */
    public boolean enviarPedido(Pedido pedido) {
        try {
            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Criar entidade HTTP com o pedido
            HttpEntity<Pedido> requestEntity = new HttpEntity<>(pedido, headers);
            
            // Fazer requisição POST
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
            );
            
            // Verificar se a resposta foi bem-sucedida
            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            } else {
                return false;
            }
            
        } catch (RestClientException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Consulta o status de um pedido no backend
     * @param pedidoId O ID do pedido
     * @return O status atual do pedido, ou null se houver erro
     */
    public String consultarStatusPedido(String pedidoId) {
        try {
            String url = baseUrl + "/status/" + pedidoId;
            
            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            // Fazer requisição GET
            ResponseEntity<StatusResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                StatusResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String status = response.getBody().getStatus();
                return status;
            } else {
                return null;
            }
            
        } catch (RestClientException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Testa a conectividade com o backend
     * @return true se o backend está acessível, false caso contrário
     */
    public boolean testarConectividade() {
        try {
            // Tentar fazer uma requisição simples para testar conectividade
            restTemplate.getForEntity(baseUrl.replace("/api/pedidos", "/health"), String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Classe interna para mapear a resposta de status
     */
    public static class StatusResponse {
        private String status;
        private String pedidoId;
        
        public StatusResponse() {}
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getPedidoId() {
            return pedidoId;
        }
        
        public void setPedidoId(String pedidoId) {
            this.pedidoId = pedidoId;
        }
    }
}