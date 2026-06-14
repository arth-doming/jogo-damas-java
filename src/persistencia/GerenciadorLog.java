package persistencia;

import modelo.Movimento;
import modelo.Partida;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GerenciadorLog {

    private static final String CABECALHO = "timestamp,jogador,cor,origem,destino,tipo,observacao";

    private GerenciadorLog() { }

    public static void salvarLog(Partida partida, String caminhoArquivo) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminhoArquivo))) {
            pw.println(CABECALHO);

            for (Movimento mov : partida.getHistorico()) {
                pw.println(mov.toCSV());
            }

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String resultado = partida.isEncerrada()
                    ? "VENCEDOR: " + partida.getVencedor()
                    : "PARTIDA EM ANDAMENTO";

            pw.println(timestamp + ",SISTEMA,,,,RESULTADO_FINAL," + resultado);
        }
    }

    public static void salvarMovimentosParciais(List<Movimento> movimentos, String caminhoArquivo) throws IOException {
        boolean arquivoNovo = !new File(caminhoArquivo).exists();
        try (PrintWriter pw = new PrintWriter(new FileWriter(caminhoArquivo, true))) {
            if (arquivoNovo) {
                pw.println(CABECALHO);
            }
            for (Movimento mov : movimentos) {
                pw.println(mov.toCSV());
            }
        }
    }
}