package ui;

import modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PainelTabuleiro extends JPanel {

    private static final int TAMANHO_CASA = 70;
    private static final Color COR_CASA_CLARA  = new Color(240, 217, 181);
    private static final Color COR_CASA_ESCURA = new Color(101, 67,  33);
    private static final Color COR_PECA_BRANCA = new Color(255, 255, 240);
    private static final Color COR_PECA_PRETA  = new Color(30,  30,  30);
    private static final Color COR_SELECIONADA = new Color(100, 200, 100, 180);

    private Partida partida;
    private int linhaSelec = -1, colunaSelec = -1;
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
        linhaSelec = -1;
        colunaSelec = -1;
        repaint();
    }

    private void tratarClique(int x, int y) {
        if (partida.isEncerrada()) return;

        int col = x / TAMANHO_CASA;
        int lin = y / TAMANHO_CASA;
        if (col < 0 || col > 7 || lin < 0 || lin > 7) return;

        if (linhaSelec == -1) {
            Peca p = partida.getTabuleiro().getPeca(lin, col);
            if (p != null && p.getCor() == partida.getVezDe()) {
                linhaSelec = lin;
                colunaSelec = col;
                repaint();
            }
        } else {
            int lo = linhaSelec, co = colunaSelec;
            linhaSelec = -1;
            colunaSelec = -1;

            if (movimentoListener != null) {
                movimentoListener.onMovimento(lo, co, lin, col);
            }
            repaint();
        }
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

                if (l == linhaSelec && c == colunaSelec) {
                    g2.setColor(COR_SELECIONADA);
                    g2.fillRect(c * TAMANHO_CASA, l * TAMANHO_CASA, TAMANHO_CASA, TAMANHO_CASA);
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