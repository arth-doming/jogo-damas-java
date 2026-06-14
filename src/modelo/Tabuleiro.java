package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tabuleiro implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Peca[][] grade;

    public Tabuleiro() {
        grade = new Peca[8][8];
    }

    public Peca getPeca(int linha, int coluna) {
        return grade[linha][coluna];
    }

    public void colocarPeca(Peca peca, int linha, int coluna) {
        grade[linha][coluna] = peca;
        peca.setLinha(linha);
        peca.setColuna(coluna);
    }

    public void removerPeca(int linha, int coluna) {
        grade[linha][coluna] = null;
    }

    public void moverPeca(int linhaOrig, int colunaOrig, int linhaDesc, int colunaDesc) {
        Peca peca = grade[linhaOrig][colunaOrig];
        grade[linhaOrig][colunaOrig] = null;
        grade[linhaDesc][colunaDesc] = peca;
        peca.setLinha(linhaDesc);
        peca.setColuna(colunaDesc);
    }

    public List<Peca> getPecasDaCor(Cor cor) {
        List<Peca> lista = new ArrayList<>();
        for (int l = 0; l < 8; l++)
            for (int c = 0; c < 8; c++)
                if (grade[l][c] != null && grade[l][c].getCor() == cor)
                    lista.add(grade[l][c]);
        return lista;
    }

    public int contarPecas(Cor cor) {
        return getPecasDaCor(cor).size();
    }

    public void configuracaoInicial() {
        for (int l = 0; l < 3; l++)
            for (int c = 0; c < 8; c++)
                if ((l + c) % 2 != 0)
                    colocarPeca(new Peca(Cor.PRETA, l, c), l, c);

        for (int l = 5; l < 8; l++)
            for (int c = 0; c < 8; c++)
                if ((l + c) % 2 != 0)
                    colocarPeca(new Peca(Cor.BRANCA, l, c), l, c);
    }
}