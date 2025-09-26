package com.vrsoftware.pedidos.gui;

import com.vrsoftware.pedidos.model.Pedido;
import com.vrsoftware.pedidos.service.PedidoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface gráfica principal da aplicação de pedidos
 */
public class PedidosFrame extends JFrame {
    
    private JTextField produtoField;
    private JTextField quantidadeField;
    private JButton enviarButton;
    private JTable pedidosTable;
    private DefaultTableModel tableModel;
    private PedidoService pedidoService;
    private List<Pedido> pedidosEnviados;
    private Timer statusUpdateTimer;
    
    public PedidosFrame() {
        this.pedidoService = new PedidoService();
        this.pedidosEnviados = new ArrayList<>();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        startStatusUpdateTimer();
    }
    
    private void initializeComponents() {
        setTitle("VRS Pedidos - Sistema de Gerenciamento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Campos de entrada
        produtoField = new JTextField(20);
        quantidadeField = new JTextField(10);
        enviarButton = new JButton("Enviar Pedido");
        
        // Configurar tabela para exibir pedidos
        String[] columnNames = {"ID", "Produto", "Quantidade", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
        };
        pedidosTable = new JTable(tableModel);
        pedidosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pedidosTable.getColumnModel().getColumn(0).setPreferredWidth(200); // ID column wider
        pedidosTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Produto
        pedidosTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Quantidade
        pedidosTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Status
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel superior com formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Novo Pedido"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Produto
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1;
        formPanel.add(produtoField, gbc);
        
        // Quantidade
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1;
        formPanel.add(quantidadeField, gbc);
        
        // Botão
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(enviarButton, gbc);
        
        add(formPanel, BorderLayout.NORTH);
        
        // Panel central com tabela de pedidos
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Pedidos Enviados"));
        
        JScrollPane scrollPane = new JScrollPane(pedidosTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
        
        // Panel inferior com informações
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Status são atualizados automaticamente a cada 5 segundos"));
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarPedido();
            }
        });
        
        // Permitir envio com Enter nos campos de texto
        ActionListener enterListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarPedido();
            }
        };
        
        produtoField.addActionListener(enterListener);
        quantidadeField.addActionListener(enterListener);
    }
    
    private void enviarPedido() {
        String produto = produtoField.getText().trim();
        String quantidadeStr = quantidadeField.getText().trim();
        
        // Validação básica
        if (produto.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, informe o produto.", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            produtoField.requestFocus();
            return;
        }
        
        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeStr);
            if (quantidade <= 0) {
                throw new NumberFormatException("Quantidade deve ser positiva");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, informe uma quantidade válida (número inteiro positivo ex:123).", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            quantidadeField.requestFocus();
            quantidadeField.selectAll();
            return;
        }
        
        // Criar pedido
        Pedido pedido = new Pedido(produto, quantidade);
        
        // Desabilitar botão temporariamente
        enviarButton.setEnabled(false);
        enviarButton.setText("Enviando...");
        
        // Enviar pedido em thread separada para não bloquear a UI
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return pedidoService.enviarPedido(pedido);
            }
            
            @Override
            protected void done() {
                try {
                    boolean sucesso = get();
                    if (sucesso) {
                        // Adicionar à lista e tabela
                        pedidosEnviados.add(pedido);
                        adicionarPedidoNaTabela(pedido);
                        
                        // Limpar campos
                        produtoField.setText("");
                        quantidadeField.setText("");
                        produtoField.requestFocus();
                        
                        JOptionPane.showMessageDialog(PedidosFrame.this, 
                            "Pedido enviado com sucesso!\nID: " + pedido.getId(), 
                            "Sucesso", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(PedidosFrame.this, 
                            "Erro ao enviar pedido. Verifique a conexão com o servidor.", 
                            "Erro de Comunicação", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PedidosFrame.this, 
                        "Erro inesperado: " + ex.getMessage(), 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Reabilitar botão
                    enviarButton.setEnabled(true);
                    enviarButton.setText("Enviar Pedido");
                }
            }
        };
        
        worker.execute();
    }
    
    private void adicionarPedidoNaTabela(Pedido pedido) {
        SwingUtilities.invokeLater(() -> {
            Object[] rowData = {
                pedido.getId(),
                pedido.getProduto(),
                pedido.getQuantidade(),
                pedido.getStatus()
            };
            tableModel.addRow(rowData);
        });
    }
    
    private void atualizarStatusNaTabela(int rowIndex, String novoStatus) {
        SwingUtilities.invokeLater(() -> {
            if (rowIndex >= 0 && rowIndex < tableModel.getRowCount()) {
                tableModel.setValueAt(novoStatus, rowIndex, 3);
            }
        });
    }
    
    private void startStatusUpdateTimer() {
        // Timer para atualizar status a cada 5 segundos
        statusUpdateTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarStatusPedidos();
            }
        });
        statusUpdateTimer.start();
    }
    
    private void atualizarStatusPedidos() {
        // Executar em thread separada para não bloquear a UI
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i < pedidosEnviados.size(); i++) {
                    Pedido pedido = pedidosEnviados.get(i);
                    
                    // Só verificar pedidos que ainda não foram finalizados
                    if (!"SUCESSO".equals(pedido.getStatus()) && !"FALHA".equals(pedido.getStatus())) {
                        String novoStatus = pedidoService.consultarStatusPedido(pedido.getId());
                        if (novoStatus != null && !novoStatus.equals(pedido.getStatus())) {
                            pedido.setStatus(novoStatus);
                            final int rowIndex = i;
                            atualizarStatusNaTabela(rowIndex, novoStatus);
                        }
                    }
                }
                return null;
            }
        };
        
        worker.execute();
    }
    
    @Override
    public void dispose() {
        if (statusUpdateTimer != null) {
            statusUpdateTimer.stop();
        }
        super.dispose();
    }
}