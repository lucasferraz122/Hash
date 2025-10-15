package hash;

class HashAberto extends TabelaHash {
    Registro[] tab;
    boolean[] occ;
    int tipoHash, tipoProbe;

    HashAberto(int t, int h, int p) {
        super(t);
        tab = new Registro[t];
        occ = new boolean[t];
        tipoHash = h;
        tipoProbe = p;
    }

    int hDiv(String c) { return Math.abs(Integer.parseInt(c) % tam); }
    int hMult(String c) {
        double A = 0.618;
        int k = Integer.parseInt(c);
        return (int)(tam * ((k*A) % 1));
    }
    int hSoma(String c) {
        int s=0;
        for(char ch : c.toCharArray()) s += ch;
        return s % tam;
    }

    int hBase(String c) {
        if(tipoHash==1) return hDiv(c);
        if(tipoHash==2) return hMult(c);
        return hSoma(c);
    }

    int hDuplo(String c, int i) {
        int h1 = hBase(c);
        int h2 = 1 + (Integer.parseInt(c) % (tam-1));
        return (h1 + i*h2) % tam;
    }

    int hLin(String c, int i) { return (hBase(c) + i) % tam; }
    int hQuad(String c, int i) { return (hBase(c) + i*i) % tam; }

    int probe(String c, int i) {
        if(tipoProbe==1) return hLin(c,i);
        if(tipoProbe==2) return hQuad(c,i);
        return hDuplo(c,i);
    }

    @Override
    void inserir(Registro r) {
        for(int i=0; i<tam; i++) {
            int pos = probe(r.cod, i);
            if(!occ[pos]) { tab[pos]=r; occ[pos]=true; return; }
            else contCol++;
        }
    }

    @Override
    boolean buscar(String c) {
        for(int i=0;i<tam;i++) {
            int pos = probe(c,i);
            if(occ[pos] && tab[pos].cod.equals(c)) return true;
            if(!occ[pos]) return false;
        }
        return false;
    }
}
