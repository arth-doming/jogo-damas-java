package modelo;

import excecoes.MovimentoInvalidoException;
import excecoes.PosicaoOcupadaException;

import java.io.Serializable;

public class Peca implements Movimentavel, Serializable {

    private static final long serialVersionUID = 1L;

    private Cor cor;
    private boolean dama;
    private int linha;
    private int coluna;

    public Peca(Cor cor, int linha, int coluna) {
        this.cor    = cor;
        this.dama   = false;
        this.linha  = linha;
        this.coluna = coluna;
    }

    public Cor getCor()      { return cor; }
    public boolean isDama()  { return dama; }
    public int getLinha()    { return linha; }
    public int getColuna()   { return coluna; }

    public void setLinha(int linha)   { this.linha  = linha; }
    public void setColuna(int coluna) { this.coluna = coluna; }
    public void promoverDama()        { this.dama   = true; }

    @Override
    public void validarMovimento(int linhaOrigem, int colunaOrigem,
                                 int linhaDestino, int colunaDestino,
                                 Tabuleiro tabuleiro)
            throws MovimentoInvalidoException, PosicaoOcupadaException {

        if (linhaDestino < 0 || linhaDestino > 7 || colunaDestino < 0 || colunaDestino > 7) {
            throw new MovimentoInvalidoException(
                    "Destino fora do tabuleiro.",
                    linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
        }

        if ((linhaDestino + colunaDestino) % 2 == 0) {
            throw new MovimentoInvalidoException(
                    "Peças só podem estar em casas escuras.",
                    linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
        }

        if (tabuleiro.getPeca(linhaDestino, colunaDestino) != null) {
            throw new PosicaoOcupadaException(linhaDestino, colunaDestino);
        }

        int dLinha  = linhaDestino  - linhaOrigem;
        int dColuna = colunaDestino - colunaOrigem;

        if (!dama) {
            int direcao = (cor == Cor.PRETA) ? 1 : -1;

            if (Math.abs(dColuna) == 1 && dLinha == direcao) {
                return;
            }

            if (Math.abs(dColuna) == 2 && dLinha == 2 * direcao) {
                int mLinha  = linhaOrigem + direcao;
                int mColuna = colunaOrigem + (dColuna > 0 ? 1 : -1);
                Peca meio = tabuleiro.getPeca(mLinha, mColuna);
                if (meio == null || meio.getCor() == this.cor) {
                    throw new MovimentoInvalidoException(
                            "Não há peça adversária para capturar.",
                            linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
                }
                return;
            }

            throw new MovimentoInvalidoException(
                    "Movimento inválido para peça normal.",
                    linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);

        } else {
            if (Math.abs(dLinha) != Math.abs(dColuna)) {
                throw new MovimentoInvalidoException(
                        "Dama deve mover-se na diagonal.",
                        linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
            }

            int stepL = dLinha  > 0 ? 1 : -1;
            int stepC = dColuna > 0 ? 1 : -1;
            int l = linhaOrigem  + stepL;
            int c = colunaOrigem + stepC;
            int adversarias = 0;

            while (l != linhaDestino || c != colunaDestino) {
                Peca p = tabuleiro.getPeca(l, c);
                if (p != null) {
                    if (p.getCor() == this.cor) {
                        throw new MovimentoInvalidoException(
                                "Caminho bloqueado por peça própria.",
                                linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
                    }
                    adversarias++;
                    if (adversarias > 1) {
                        throw new MovimentoInvalidoException(
                                "Dama não pode pular mais de uma peça adversária.",
                                linhaOrigem, colunaOrigem, linhaDestino, colunaDestino);
                    }
                }
                l += stepL;
                c += stepC;
            }
        }
    }

    @Override
    public String toString() {
        return (dama ? "DAMA-" : "PECA-") + cor + "(" + linha + "," + coluna + ")";
    }
}