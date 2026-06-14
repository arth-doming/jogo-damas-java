package ui;

import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PainelTabuleiro extends JPanel {

    private static final int TAMANHO_CASA = 70;
    private static final Color COR_CASA_CLARA  = new Color(240, 217, 181);
    private static final Color COR_CASA_ESCURA = new Color(101, 67,  33);
    private static final Color COR_PECA_BRANCA = new Color(255, 255, 240);
    private static final Color COR_PECA_PRETA  = new Color(30,  30,  30);
    private static final Color COR_SELECIONADA = new Color(100, 200, 100, 180);
    private static final Color COR_DESTINO     = new Color(255, 255,   0, 120);

    private Partida partida;
    private int linhaSelec = -1, colunaSelec = -1;
    private List<int[]> destinosPossiveis = new ArrayList<>();
    private MovimentoListener movimentoListener;

    public interface MovimentoListener {
        void onMovimento(int linhaOrig, int colunaOrig, int linhaDesc, int colunaDesc);
    }

    public PainelTabuleiro(Partida partida) {
        this.partida = partida;
        setPreferredSize(new Dimension(8 * TAMANHO_CASA, 8 * TAMANHO_CASA));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tratarClique(e.getX(), e.getY());
            }
        });
    }

    public void setMovimentoListener(MovimentoListener listener) {
        this.movimentoListener = listener;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
        linhaSelec  = -1;
        colunaSelec = -1;
        destinosPossiveis.clear();
        repaint();
    }

    private void tratarClique(int x, int y) {
        if (partida.isEncerrada()) return;

        int col = x / TAMANHO_CASA;
        int lin = y / TAMANHO_CASA;
        if (col < 0 || col > 7 || lin < 0 || lin > 7) return;

        if (linhaSelec == -1) {
            // Primeiro clique: seleciona peça e calcula destinos
            Peca p = partida.getTabuleiro().getPeca(lin, col);
            if (p != null && p.getCor() == partida.getVezDe()) {
                linhaSelec  = lin;
                colunaSelec = col;
                destinosPossiveis = calcularDestinosPossiveis(lin, col, p);
                repaint();
            }
        } else {
            // Segundo clique: tenta mover
            int lo = linhaSelec, co = colunaSelec;
            linhaSelec  = -1;
            colunaSelec = -1;
            destinosPossiveis.clear();

            if (movimentoListener != null) {
                movimentoListener.onMovimento(lo, co, lin, col);
            }
            repaint();
        }
    }

    private List<int[]> calcularDestinosPossiveis(int linha, int coluna, Peca peca) {
        List<int[]> destinos = new ArrayList<>();
        Tabuleiro tab = partida.getTabuleiro();

        if (!peca.isDama()) {
            // Peça normal: verifica as 2 diagonais da direção correta + capturas
            int direcao = (peca.getCor() == Cor.PRETA) ? 1 : -1;

            // Movimentos simples
            int[][] simples = {
                    {linha + direcao, coluna - 1},
                    {linha + direcao, coluna + 1}
            };
            for (int[] dest : simples) {
                if (dentroDoBoardeiro(dest[0], dest[1])
                        && tab.getPeca(dest[0], dest[1]) == null) {
                    destinos.add(dest);
                }
            }

            // Capturas
            int[][] capturas = {
                    {linha + 2 * direcao, coluna - 2},
                    {linha + 2 * direcao, coluna + 2}
            };
            int[][] meios = {
                    {linha + direcao, coluna - 1},
                    {linha + direcao, coluna + 1}
            };
            for (int i = 0; i < capturas.length; i++) {
                int[] dest = capturas[i];
                int[] meio = meios[i];
                if (dentroDoBoardeiro(dest[0], dest[1])
                        && tab.getPeca(dest[0], dest[1]) == null
                        && dentroDoBoardeiro(meio[0], meio[1])) {
                    Peca pecaMeio = tab.getPeca(meio[0], meio[1]);
                    if (pecaMeio != null && pecaMeio.getCor() != peca.getCor()) {
                        destinos.add(dest);
                    }
                }
            }
        } else {
            // Dama: varre todas as diagonais
            int[][] direcoes = {{-1,-1},{-1,1},{1,-1},{1,1}};
            for (int[] dir : direcoes) {
                int l = linha + dir[0];
                int c = coluna + dir[1];
                boolean encontrouAdversaria = false;
                while (dentroDoBoardeiro(l, c)) {
                    Peca p = tab.getPeca(l, c);
                    if (p == null) {
                        destinos.add(new int[]{l, c});
                    } else if (p.getCor() != peca.getCor() && !encontrouAdversaria) {
                        encontrouAdversaria = true;
                    } else {
                        break;
                    }
                    l += dir[0];
                    c += dir[1];
                }
            }
        }

        return destinos;
    }

    private boolean dentroDoBoardeiro(int l, int c) {
        return l >= 0 && l < 8 && c >= 0 && c < 8;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                boolean casaEscura = (l + c) % 2 != 0;
                g2.setColor(casaEscura ? COR_CASA_ESCURA : COR_CASA_CLARA);
                g2.fillRect(c * TAMANHO_CASA, l * TAMANHO_CASA, TAMANHO_CASA, TAMANHO_CASA);

                // Destaque da peça selecionada
                if (l == linhaSelec && c == colunaSelec) {
                    g2.setColor(COR_SELECIONADA);
                    g2.fillRect(c * TAMANHO_CASA, l * TAMANHO_CASA, TAMANHO_CASA, TAMANHO_CASA);
                }

                // Destaque dos destinos possíveis
                for (int[] dest : destinosPossiveis) {
                    if (dest[0] == l && dest[1] == c) {
                        g2.setColor(COR_DESTINO);
                        g2.fillRect(c * TAMANHO_CASA, l * TAMANHO_CASA, TAMANHO_CASA, TAMANHO_CASA);

                        // Circulo no centro da casa
                        g2.setColor(new Color(200, 180, 0, 200));
                        int margem = 25;
                        g2.fillOval(c * TAMANHO_CASA + margem, l * TAMANHO_CASA + margem,
                                TAMANHO_CASA - 2 * margem, TAMANHO_CASA - 2 * margem);
                    }
                }
            }
        }

        for (int l = 0; l < 8; l++) {
            for (int c = 0; c < 8; c++) {
                Peca peca = partida.getTabuleiro().getPeca(l, c);
                if (peca != null) {
                    desenharPeca(g2, peca, l, c);
                }
            }
        }
    }

    private void desenharPeca(Graphics2D g2, Peca peca, int linha, int coluna) {
        int x = coluna * TAMANHO_CASA + 8;
        int y = linha  * TAMANHO_CASA + 8;
        int d = TAMANHO_CASA - 16;

        Color corPeca  = peca.getCor() == Cor.BRANCA ? COR_PECA_BRANCA : COR_PECA_PRETA;
        Color corBorda = peca.getCor() == Cor.BRANCA ? Color.GRAY : new Color(80, 80, 80);

        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillOval(x + 3, y + 3, d, d);

        g2.setColor(corPeca);
        g2.fillOval(x, y, d, d);

        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x, y, d, d);

        if (peca.isDama()) {
            g2.setColor(new Color(255, 215, 0));
            g2.setFont(new Font("Serif", Font.BOLD, 22));
            FontMetrics fm = g2.getFontMetrics();
            String coroa = "♛";
            int tx = x + (d - fm.stringWidth(coroa)) / 2;
            int ty = y + (d + fm.getAscent() - fm.getDescent()) / 2 - 2;
            g2.drawString(coroa, tx, ty);
        }
    }
}