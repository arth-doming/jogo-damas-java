package p2;

import modelo.Partida;
import persistencia.GerenciadorPersistencia;
import ui.JanelaPrincipal;

import javax.swing.*;

public class MainP2 {

    private static final String ARQUIVO_PARTIDA = "dados/partida.bin";

    public static void main(String[] args) {
        System.out.println("=== P2 — Interface Gráfica do Jogo de Damas ===\n");

        try {
            Partida partida = GerenciadorPersistencia.carregarPartida(ARQUIVO_PARTIDA);
            System.out.println("Partida restaurada: " + partida);

            SwingUtilities.invokeLater(() -> {
                JanelaPrincipal janela = new JanelaPrincipal(partida);
                janela.setVisible(true);
            });

        } catch (java.io.IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            System.err.println("Execute o P1 antes do P2!");
            JOptionPane.showMessageDialog(null,"Arquivo não encontrado.\nExecute o P1 primeiro!","Erro", JOptionPane.ERROR_MESSAGE);

        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar classe: " + e.getMessage());
        }
    }
}