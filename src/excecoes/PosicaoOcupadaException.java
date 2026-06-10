package excecoes;

public class PosicaoOcupadaException extends Exception
{
    private final int linha, coluna;

    public PosicaoOcupadaException(int linha, int coluna)
    {
        super("A posição (" + linha + ", " + coluna + ") já está ocupada.");
        this.linha = linha;
        this.coluna = coluna;
    }
    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    @Override
    public String toString()
    {
        return  "PosicaoOcupadaException: " + getMessage();
    }
}
