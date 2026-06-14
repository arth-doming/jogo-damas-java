package ui;

import excecoes.MovimentoInvalidoException;
import excecoes.PosicaoOcupadaException;
import modelo.*;
import persistencia.GerenciadorLog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class JanelaPrincipal extends JFrame {

    private static final String ARQUIVO_LOG = "dados/resultado.csv";

    private final Partida partida;
    private final PainelTabuleiro painelTabuleiro;
    private final JLabel labelStatus;
    private final JLabel labelPecas;
    private final JTextArea areaLog;

    public JanelaPrincipal(Partida partida) {
        super("Jogo de Damas");
        this.partida = partida;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        painelTabuleiro = new PainelTabuleiro(partida);
        painelTabuleiro.setMovimentoListener(this::processarMovimento);

        JPanel painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));
        painelLateral.setPreferredSize(new Dimension(240, 0));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelLateral.setBackground(new Color(40, 40, 40));

        JLabel titulo = new JLabel("JOGO DE DAMAS");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelStatus = new JLabel();
        labelStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
        labelStatus.setForeground(new Color(100, 220, 100));
        labelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelPecas = new JLabel();
        labelPecas.setFont(new Font("SansSerif", Font.PLAIN, 12));
        labelPecas.setForeground(Color.LIGHT_GRAY);
        labelPecas.setAlignmentX(Component.CENTER_ALIGNMENT);

        areaLog = new JTextArea(20, 18);
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaLog.setBackground(new Color(25, 25, 25));
        areaLog.setForeground(new Color(180, 180, 180));
        JScrollPane scroll = new JScrollPane(areaLog);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Histórico",
                0, 0, new Font("SansSerif", Font.BOLD, 11), Color.GRAY));

        JButton btnDesistir = new JButton("Desistir");
        btnDesistir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDesistir.addActionListener(e -> desistir());

        JButton btnSalvar = new JButton("Salvar Log CSV");
        btnSalvar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalvar.addActionListener(e -> salvarLog());

        painelLateral.add(titulo);
        painelLateral.add(Box.createVerticalStrut(10));
        painelLateral.add(labelStatus);
        painelLateral.add(Box.createVerticalStrut(5));
        painelLateral.add(labelPecas);
        painelLateral.add(Box.createVerticalStrut(10));
        painelLateral.add(scroll);
        painelLateral.add(Box.createVerticalStrut(10));
        painelLateral.add(btnDesistir);
        painelLateral.add(Box.createVerticalStrut(5));
        painelLateral.add(btnSalvar);

        setLayout(new BorderLayout());
        add(painelTabuleiro, BorderLayout.CENTER);
        add(painelLateral,   BorderLayout.EAST);

        atualizarStatus();
        pack();
        setLocationRelativeTo(null);
    }

    private void processarMovimento(int lo, int co, int ld, int cd) {
        if (partida.isEncerrada()) return;

        try {
            Movimento mov = partida.realizarMovimento(lo, co, ld, cd);
            registrarNoLog(mov.toString());
            painelTabuleiro.repaint();
            atualizarStatus();

            if (partida.isEncerrada()) {
                exibirFimDeJogo();
            }

        } catch (MovimentoInvalidoException e) {
            registrarNoLog("[ERRO] " + e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Movimento Inválido", JOptionPane.WARNING_MESSAGE);

        } catch (PosicaoOcupadaException e) {
            registrarNoLog("[ERRO] " + e.getMessage());
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Posição Ocupada", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void atualizarStatus() {
        if (!partida.isEncerrada()) {
            Jogador atual = partida.getJogadorAtual();
            labelStatus.setText("Vez: " + atual.getNome() + " (" + atual.getCor() + ")");
            labelPecas.setText("Brancas: " + partida.getTabuleiro().contarPecas(Cor.BRANCA)
                    + "  |  Pretas: " + partida.getTabuleiro().contarPecas(Cor.PRETA));
        } else {
            labelStatus.setText("Partida encerrada!");
            labelStatus.setForeground(new Color(220, 100, 100));
        }
    }

    private void registrarNoLog(String texto) {
        areaLog.append(texto + "\n");
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    private void desistir() {
        if (partida.isEncerrada()) return;
        int op = JOptionPane.showConfirmDialog(this,
                partida.getJogadorAtual().getNome() + " deseja realmente desistir?",
                "Desistir", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            partida.desistir();
            atualizarStatus();
            registrarNoLog(">>> " + partida.getVencedor() + " venceu por desistência!");
            exibirFimDeJogo();
        }
    }

    private void salvarLog() {
        try {
            GerenciadorLog.salvarLog(partida, ARQUIVO_LOG);
            JOptionPane.showMessageDialog(this,
                    "Log salvo em: " + ARQUIVO_LOG, "Salvo", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar log: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirFimDeJogo() {
        salvarLog();
        JOptionPane.showMessageDialog(this,
                "Fim de jogo!\nVencedor: " + partida.getVencedor(),
                "Fim de Jogo", JOptionPane.INFORMATION_MESSAGE);
    }
}