package modelo;

import java.io.Serializable;

public enum Cor implements Serializable
{
    BRANCA, PRETA;

    public Cor oposta()
    {
        return this == BRANCA ? PRETA : BRANCA;
    }
}
