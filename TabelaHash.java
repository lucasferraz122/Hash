package hash;

abstract class TabelaHash {
    int tam;
    int contCol = 0;
    TabelaHash(int t) { tam = t; }
    abstract void inserir(Registro r);
    abstract boolean buscar(String c);
}
