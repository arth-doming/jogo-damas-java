package modelo;

import excecoes.MovimentoInvalidoException;
import excecoes.PosicaoOcupadaException;

public interface Movimentavel
{
    void validarMovimento(int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino, Tabuleiro tabuleiro) throws MovimentoInvalidoException, PosicaoOcupadaException;
}
