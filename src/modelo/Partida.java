package modelo;

import excecoes.MovimentoInvalidoException;
import excecoes.PosicaoOcupadaException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Partida implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Jogador jogadorBranco;
    private final Jogador jogadorPreto;
    private final Tabuleiro tabuleiro;
    private final List<Movimento> historico;

    private Cor vezDe;
    private String vencedor;
    private boolean encerrada;

    public Partida(Jogador jogadorBranco, Jogador jogadorPreto) {
        this.jogadorBranco = jogadorBranco;
        this.jogadorPreto  = jogadorPreto;
        this.tabuleiro = new Tabuleiro();
        this.historico = new ArrayList<>();
        this.vezDe = Cor.BRANCA;
        this.encerrada = false;
        this.vencedor = null;

        tabuleiro.configuracaoInicial();
    }

    public Jogador getJogadorBranco() { return jogadorBranco; }
    public Jogador getJogadorPreto() { return jogadorPreto; }
    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public List<Movimento> getHistorico() { return historico; }
    public Cor getVezDe() { return vezDe; }
    public boolean isEncerrada() { return encerrada; }
    public String getVencedor() { return vencedor; }

    public Jogador getJogadorAtual() {
        return (vezDe == Cor.BRANCA) ? jogadorBranco : jogadorPreto;
    }

    public Movimento realizarMovimento(int linhaOrig, int colunaOrig, int linhaDesc, int colunaDesc) throws MovimentoInvalidoException, PosicaoOcupadaException {

        Peca peca = tabuleiro.getPeca(linhaOrig, colunaOrig);

        if (peca == null) {
            throw new MovimentoInvalidoException(
                    "Não há peça na posição de origem.",
                    linhaOrig, colunaOrig, linhaDesc, colunaDesc);
        }

        if (peca.getCor() != vezDe) {
            throw new MovimentoInvalidoException(
                    "Esta peça não pertence ao jogador da vez (" + vezDe + ").",
                    linhaOrig, colunaOrig, linhaDesc, colunaDesc);
        }

        peca.validarMovimento(linhaOrig, colunaOrig, linhaDesc, colunaDesc, tabuleiro);

        boolean foiCaptura = false;
        int dLinha  = linhaDesc - linhaOrig;
        int dColuna = colunaDesc - colunaOrig;

        if (Math.abs(dLinha) >= 2) {
            int stepL = dLinha  > 0 ? 1 : -1;
            int stepC = dColuna > 0 ? 1 : -1;
            int l = linhaOrig + stepL;
            int c = colunaOrig + stepC;
            while (l != linhaDesc || c != colunaDesc) {
                Peca meio = tabuleiro.getPeca(l, c);
                if (meio != null && meio.getCor() != peca.getCor()) {
                    tabuleiro.removerPeca(l, c);
                    getJogadorAtual().incrementarCaptura();
                    foiCaptura = true;
                }
                l += stepL;
                c += stepC;
            }
        }

        tabuleiro.moverPeca(linhaOrig, colunaOrig, linhaDesc, colunaDesc);

        boolean viouDama = false;
        if (!peca.isDama()) {
            if ((peca.getCor() == Cor.PRETA  && linhaDesc == 7) ||
                    (peca.getCor() == Cor.BRANCA && linhaDesc == 0)) {
                peca.promoverDama();
                viouDama = true;
            }
        }

        Jogador atual = getJogadorAtual();
        Movimento mov = new Movimento(atual.getNome(), atual.getCor(),
                linhaOrig, colunaOrig, linhaDesc, colunaDesc,
                foiCaptura, viouDama);
        historico.add(mov);

        verificarFimDeJogo();

        if (!encerrada) {
            vezDe = vezDe.oposta();
        }

        return mov;
    }

    private void verificarFimDeJogo() {
        if (tabuleiro.contarPecas(Cor.BRANCA) == 0) {
            encerrada = true;
            vencedor  = jogadorPreto.getNome() + " [" + jogadorPreto.getTipo() + "]";
        } else if (tabuleiro.contarPecas(Cor.PRETA) == 0) {
            encerrada = true;
            vencedor  = jogadorBranco.getNome() + " [" + jogadorBranco.getTipo() + "]";
        }
    }

    public void desistir() {
        encerrada = true;
        Jogador oponente = (vezDe == Cor.BRANCA) ? jogadorPreto : jogadorBranco;
        vencedor = oponente.getNome() + " [" + oponente.getTipo() + "] (desistência)";
    }

    @Override
    public String toString() {
        return "Partida: " + jogadorBranco.getNome() + " (Brancas) vs "
                + jogadorPreto.getNome() + " (Pretas)"
                + (encerrada ? " | Vencedor: " + vencedor : " | Em andamento");
    }
}