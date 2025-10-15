package hash;

import java.util.*;

class HashEncadeamento extends TabelaHash {
    ListaEncadeada[] tab;
    HashEncadeamento(int t) {
        super(t);
        tab = new ListaEncadeada[t];
        for(int i=0; i<t; i++) tab[i] = new ListaEncadeada();
    }

    int hashDiv(String c) { return Math.abs(Integer.parseInt(c) % tam); }

    int hashMult(String c) {
        double A = 0.618;
        int k = Integer.parseInt(c);
        return (int)(tam * ((k * A) % 1));
    }

    int hashSoma(String c) {
        int s = 0;
        for(char ch : c.toCharArray()) s += ch;
        return s % tam;
    }

    int hashEscolhe(String c, int t) {
        if(t==1) return hashDiv(c);
        if(t==2) return hashMult(c);
        return hashSoma(c);
    }

    void inserir(Registro r, int t) {
        int pos = hashEscolhe(r.cod, t);
        if(tab[pos].inicio != null) contCol++;
        tab[pos].inserir(r);
    }

    @Override
    void inserir(Registro r) { inserir(r,1); }

    boolean buscar(String c, int t) {
        int pos = hashEscolhe(c, t);
        return tab[pos].buscar(c);
    }

    @Override
    boolean buscar(String c) { return buscar(c,1); }

    List<Integer> maiores() {
        List<Integer> l = new ArrayList<>();
        for(ListaEncadeada x : tab) l.add(x.tam);
        l.sort(Collections.reverseOrder());
        return l.subList(0, Math.min(3, l.size()));
    }
}
