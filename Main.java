class Registro {
    String cod;
    Registro(String c) { cod = c; }
    public String toString() { return cod; }
}

class ListaEncadeada {
    static class No {
        Registro r;
        No prox;
        No(Registro rr) { r = rr; }
    }
    No inicio;
    int tam = 0;
    void inserir(Registro rr) {
        No n = new No(rr);
        n.prox = inicio;
        inicio = n;
        tam++;
    }
    boolean buscar(String c) {
        No aux = inicio;
        while(aux != null) {
            if(aux.r.cod.equals(c)) return true;
            aux = aux.prox;
        }
        return false;
    }
}
