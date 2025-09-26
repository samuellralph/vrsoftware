package com.vrsoftware.pedidos;

import com.vrsoftware.pedidos.gui.PedidosFrame;

import javax.swing.*;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Classe principal da aplicação VRS Pedidos GUI
 */
public class PedidosApplication {
    
    public static void main(String[] args) {
        System.out.println("Iniciando aplicação VRS Pedidos GUI...");
        
        // Configurar Look and Feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println("Não foi possível definir o Look and Feel do sistema: " + e.getMessage());
        }
        
        // Executar na Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Criar e exibir a janela principal
                    PedidosFrame frame = new PedidosFrame();
                    frame.setVisible(true);
                    
                    System.out.println("Aplicação iniciada com sucesso!");
                    
                } catch (Exception e) {
                    System.err.println("Erro ao iniciar a aplicação: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Exibir mensagem de erro para o usuário
                    JOptionPane.showMessageDialog(
                        null,
                        "Erro ao iniciar a aplicação:\n" + e.getMessage(),
                        "Erro de Inicialização",
                        JOptionPane.ERROR_MESSAGE
                    );
                    
                    System.exit(1);
                }
            }
        });
    }
}