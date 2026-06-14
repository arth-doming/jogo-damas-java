package modelo;

public class JogadorHumano extends Jogador
{
    private static final long serialVersionUID = 1L;

    public JogadorHumano(String nome, Cor cor){
        super(nome, cor);
    }

    @Override

    public String getTipo(){
        return "Humano";
    }
}
