import java.util.*;

// classe que representa um registro simples (ex: código de 9 dígitos)
class Registro {
    String cod;
    Registro(String c) { cod = c; }

    @Override
    public String toString() { return cod; }
}

// lista encadeada usada para tratar colisões
class ListaEncadeada {
    static class No {
        Registro r;
        No prox;
        No(Registro rr) { r = rr; }
    }

    No inicio;
    int tam = 0;

    // insere no início da lista
    void inserir(Registro rr) {
        No n = new No(rr);
        n.prox = inicio;
        inicio = n;
        tam++;
    }

    // busca um código na lista
    boolean buscar(String c) {
        No aux = inicio;
        while (aux != null) {
            if (aux.r.cod.equals(c)) return true;
            aux = aux.prox;
        }
        return false;
    }
}

// classe abstrata base (estrutura comum de tabela hash)
abstract class TabelaHash {
    int tam;
    int contCol = 0; // contador de colisões

    TabelaHash(int t) { tam = t; }

    abstract void inserir(Registro r);
    abstract boolean buscar(String c);
}

// tabela hash com encadeamento
class HashEncadeamento extends TabelaHash {
    ListaEncadeada[] tab;

    HashEncadeamento(int t) {
        super(t);
        tab = new ListaEncadeada[t];
        for (int i = 0; i < t; i++) tab[i] = new ListaEncadeada();
    }

    // função hash 1 — divisão
    int hashDiv(String c) {
        return Math.abs(Integer.parseInt(c) % tam);
    }

    // função hash 2 — multiplicação
    int hashMult(String c) {
        double A = 0.618;
        int k = Integer.parseInt(c);
        return (int)(tam * ((k * A) % 1));
    }

    // função hash 3 — soma dos caracteres
    int hashSoma(String c) {
        int s = 0;
        for (char ch : c.toCharArray()) s += ch;
        return s % tam;
    }

    // escolhe a função hash
    int hashEscolhe(String c, int t) {
        if (t == 1) return hashDiv(c);
        if (t == 2) return hashMult(c);
        return hashSoma(c);
    }

    // insere um registro na tabela usando encadeamento
    void inserir(Registro r, int t) {
        int pos = hashEscolhe(r.cod, t);
        if (tab[pos].inicio != null) contCol++;
        tab[pos].inserir(r);
    }

    @Override
    void inserir(Registro r) { inserir(r, 1); }

    // busca um registro na tabela
    boolean buscar(String c, int t) {
        int pos = hashEscolhe(c, t);
        return tab[pos].buscar(c);
    }

    @Override
    boolean buscar(String c) { return buscar(c, 1); }

    // retorna as 3 listas com mais elementos (para análise futura)
    List<Integer> maiores() {
        List<Integer> l = new ArrayList<>();
        for (ListaEncadeada x : tab) l.add(x.tam);
        l.sort(Collections.reverseOrder());
        return l.subList(0, Math.min(3, l.size()));
    }

    // função auxiliar para listar conteúdo (teste visual)
    void listar() {
        for (int i = 0; i < tam; i++) {
            System.out.print("Posição " + i + ": ");
            ListaEncadeada.No aux = tab[i].inicio;
            while (aux != null) {
                System.out.print(aux.r + " -> ");
                aux = aux.prox;
            }
            System.out.println("null");
        }
    }
}

// teste simples
public class Main {
    public static void main(String[] args) {
        HashEncadeamento h = new HashEncadeamento(10);

        h.inserir(new Registro("123"), 1);
        h.inserir(new Registro("456"), 2);
        h.inserir(new Registro("789"), 3);
        h.inserir(new Registro("321"), 1);

        h.listar();
    }
}
