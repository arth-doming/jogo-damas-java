package persistencia;

import modelo.Partida;

import java.io.*;

public class GerenciadorPersistencia {

    private GerenciadorPersistencia() { }

    public static void salvarPartida(Partida partida, String caminhoArquivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(caminhoArquivo))) {
            oos.writeObject(partida);
        }
    }

    public static Partida carregarPartida(String caminhoArquivo)
            throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(caminhoArquivo))) {
            return (Partida) ois.readObject();
        }
    }
}