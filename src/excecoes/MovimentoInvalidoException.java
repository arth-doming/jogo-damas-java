package excecoes;

public class MovimentoInvalidoException extends Exception
{
    private final int linhaOrigem, colunaOrigem, linhaDestino, colunaDestino;

    public MovimentoInvalidoException(String mensagem, int linhaOrigem, int colunaOrigem, int linhaDestino, int colunaDestino)
    {
        super(mensagem);
        this.linhaOrigem = linhaOrigem;
        this.colunaOrigem = colunaOrigem;
        this.linhaDestino = linhaDestino;
        this.colunaDestino = colunaDestino;

    }
    public int getLinhaOrigem() {
        return linhaOrigem;
    }
    public int getColunaOrigem() {
        return colunaOrigem;
    }
    public int getLinhaDestino() {
        return linhaDestino;
    }
    public int getColunaDestino() {
        return colunaDestino;
    }

    @Override

    public String toString()
    {
        return "MovimentoInvalidoException: " + getMessage() + " [(" + linhaOrigem + "," + colunaOrigem + ") -> (" + linhaDestino + "," + colunaDestino + ")]";
    }

}
