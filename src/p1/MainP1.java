package p1;

import modelo.*;
import persistencia.GerenciadorPersistencia;

import java.io.*;
import java.nio.file.*;

public class MainP1 {

    private static final String ARQUIVO_ENTRADA = "dados/jogadores.txt";
    private static final String ARQUIVO_SAIDA   = "dados/partida.bin";

    public static void main(String[] args) {
        System.out.println("=== P1 — Criação e Persistência da Partida ===\n");

        try {
            Partida partida = criarPartidaDoArquivo(ARQUIVO_ENTRADA);
            GerenciadorPersistencia.salvarPartida(partida, ARQUIVO_SAIDA);

            System.out.println("Partida criada com sucesso!");
            System.out.println("  Jogador Branco : " + partida.getJogadorBranco());
            System.out.println("  Jogador Preto  : " + partida.getJogadorPreto());
            System.out.println("  Peças brancas  : " + partida.getTabuleiro().contarPecas(Cor.BRANCA));
            System.out.println("  Peças pretas   : " + partida.getTabuleiro().contarPecas(Cor.PRETA));
            System.out.println("\nArquivo salvo em: " + ARQUIVO_SAIDA);

        } catch (IOException e) {
            System.err.println("Erro ao ler/escrever arquivo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Dados inválidos no arquivo: " + e.getMessage());
        }
    }

    private static Partida criarPartidaDoArquivo(String caminho) throws IOException {
        Jogador jogadorBranco = null;
        Jogador jogadorPreto  = null;

        for (String linha : Files.readAllLines(Path.of(caminho))) {
            linha = linha.trim();
            if (linha.isEmpty() || linha.startsWith("#")) continue;

            String[] partes = linha.split(",");
            if (partes.length < 2) {
                throw new IllegalArgumentException("Linha malformada: " + linha);
            }

            String nome = partes[0].trim();
            Cor cor;
            try {
                cor = Cor.valueOf(partes[1].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Cor inválida em: " + linha);
            }

            JogadorHumano jogador = new JogadorHumano(nome, cor);
            if (cor == Cor.BRANCA) jogadorBranco = jogador;
            else                   jogadorPreto  = jogador;
        }

        if (jogadorBranco == null || jogadorPreto == null) {
            throw new IllegalArgumentException(
                    "O arquivo deve conter um jogador BRANCA e um jogador PRETA.");
        }

        return new Partida(jogadorBranco, jogadorPreto);
    }
}