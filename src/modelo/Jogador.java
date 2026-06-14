package modelo;

import java.io.Serializable;

public abstract class Jogador implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nome;
    private final Cor cor;
    private int pecasCapturadas;

    public Jogador(String nome, Cor cor) {
        this.nome = nome;
        this.cor  = cor;
        this.pecasCapturadas = 0;
    }

    public String getNome()         { return nome; }
    public Cor getCor()             { return cor; }
    public int getPecasCapturadas() { return pecasCapturadas; }

    public void incrementarCaptura() { pecasCapturadas++; }

    public abstract String getTipo();

    @Override
    public String toString() {
        return getTipo() + " " + nome + " (" + cor + ")";
    }
}