package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Movimento implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String nomeJogador;
    private final Cor cor;
    private final int linhaOrigem, colunaOrigem;
    private final int linhaDestino, colunaDestino;
    private final boolean captura;
    private final boolean promocaoDama;
    private final LocalDateTime momento;

    public Movimento(
            String nomeJogador, Cor cor,
            int linhaOrigem, int colunaOrigem,
            int linhaDestino, int colunaDestino,
            boolean captura, boolean promocaoDama)
    {
        this.nomeJogador = nomeJogador;
        this.cor = cor;
        this.linhaOrigem = linhaOrigem;
        this.colunaOrigem = colunaOrigem;
        this.colunaDestino = colunaDestino;
        this.linhaDestino = linhaDestino;
        this.captura = captura;
        this.promocaoDama = promocaoDama;
        this.momento = LocalDateTime.now();
    }

    public String getNomeJogador() { return nomeJogador; }
    public Cor getCor() { return cor; }
    public int getLinhaOrigem() { return linhaOrigem; }
    public int getColunaOrigem() { return colunaOrigem; }
    public int getLinhaDestino() { return linhaDestino; }
    public int getColunaDestino() { return colunaDestino; }
    public boolean isCaptura() { return captura; }
    public boolean isPromocaoDama() { return promocaoDama; }
    public LocalDateTime getMomento() { return momento; }

    public String toCSV() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.join(",",
                momento.format(fmt),
                nomeJogador,
                cor.name(),
                linhaOrigem  + "-" + colunaOrigem,
                linhaDestino + "-" + colunaDestino,
                captura      ? "CAPTURA"    : "MOVIMENTO",
                promocaoDama ? "VIROU_DAMA" : ""
        );
    }

    @Override

    public String toString() {
        return nomeJogador + " (" + cor + "): (" + linhaOrigem + "," + colunaOrigem
                + ") -> (" + linhaDestino + "," + colunaDestino + ")"
                + (captura      ? " [CAPTURA]"     : "")
                + (promocaoDama ? " [VIROU DAMA]"  : "");
    }
}
