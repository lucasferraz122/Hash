import java.util.*;
import java.io.*;

// classe que representa um registro armazenado na tabela hash
class Registro {
    String cod; // código único do registro

    Registro(String c) { cod = c; }

    // retorna o código do registro como string
    public String toString() { return cod; }
}

// implementação de uma lista encadeada simples
class ListaEncadeada {
    // nó interno da lista
    static class No {
        Registro r;
        No prox;
        No(Registro rr) { r = rr; }
    }

    No inicio; // referência para o primeiro nó
    int tam = 0; // tamanho da lista (quantos registros tem nela)

    // insere um novo registro no início da lista
    void inserir(Registro rr) {
        No n = new No(rr);
        n.prox = inicio;
        inicio = n;
        tam++;
    }

    // busca um código dentro da lista
    boolean buscar(String c) {
        No aux = inicio;
        while(aux != null) {
            if(aux.r.cod.equals(c)) return true;
            aux = aux.prox;
        }
        return false;
    }
}

// classe abstrata base para as implementações de tabela hash
abstract class TabelaHash {
    int tam; // tamanho da tabela
    int contCol = 0; // contador de colisões

    TabelaHash(int t) { tam = t; }

    // métodos abstratos a serem implementados nas subclasses
    abstract void inserir(Registro r);
    abstract boolean buscar(String c);
}

// implementação de tabela hash com encadeamento separado
class HashEncadeamento extends TabelaHash {
    ListaEncadeada[] tab; // vetor de listas encadeadas (buckets)

    HashEncadeamento(int t) {
        super(t);
        tab = new ListaEncadeada[t];
        for(int i=0; i<t; i++) tab[i] = new ListaEncadeada();
    }

    // função de hash por divisão
    int hashDiv(String c) { return Math.abs(Integer.parseInt(c) % tam); }

    // função de hash por multiplicação
    int hashMult(String c) {
        double A = 0.618; // constante de Knuth
        int k = Integer.parseInt(c);
        return (int)(tam * ((k * A) % 1));
    }

    // função de hash por soma dos caracteres
    int hashSoma(String c) {
        int s = 0;
        for(char ch : c.toCharArray()) s += ch;
        return s % tam;
    }

    // escolhe a função de hash conforme o tipo (1 = divisão, 2 = multiplicação, 3 = soma)
    int hashEscolhe(String c, int t) {
        if(t==1) return hashDiv(c);
        if(t==2) return hashMult(c);
        return hashSoma(c);
    }

    // insere um registro aplicando o método de hash selecionado
    void inserir(Registro r, int t) {
        int pos = hashEscolhe(r.cod, t);
        if(tab[pos].inicio != null) contCol++; // conta colisão se a posição já estava ocupada
        tab[pos].inserir(r);
    }

    @Override
    void inserir(Registro r) { inserir(r,1); }

    // busca um registro aplicando o hash selecionado
    boolean buscar(String c, int t) {
        int pos = hashEscolhe(c, t);
        return tab[pos].buscar(c);
    }

    @Override
    boolean buscar(String c) { return buscar(c,1); }

    // retorna as três maiores listas (em tamanho) da tabela
    List<Integer> maiores() {
        List<Integer> l = new ArrayList<>();
        for(ListaEncadeada x : tab) l.add(x.tam);
        l.sort(Collections.reverseOrder());
        return l.subList(0, Math.min(3, l.size()));
    }
}

// implementação de tabela hash com endereçamento aberto
class HashAberto extends TabelaHash {
    Registro[] tab; // vetor de registros
    boolean[] occ;  // vetor que indica se a posição já foi ocupada
    int tipoHash, tipoProbe; // tipo de função hash e tipo de sondagem

    HashAberto(int t, int h, int p) {
        super(t);
        tab = new Registro[t];
        occ = new boolean[t];
        tipoHash = h;  // 1 = divisão, 2 = multiplicação, 3 = soma
        tipoProbe = p; // 1 = linear, 2 = quadrática, 3 = dupla
    }

    // funções hash básicas (iguais às do encadeamento)
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

    // escolhe a função base de hash
    int hBase(String c) {
        if(tipoHash==1) return hDiv(c);
        if(tipoHash==2) return hMult(c);
        return hSoma(c);
    }

    // função de sondagem dupla (double hashing)
    int hDuplo(String c, int i) {
        int h1 = hBase(c);
        int h2 = 1 + (Integer.parseInt(c) % (tam-1));
        return (h1 + i*h2) % tam;
    }

    // sondagem linear
    int hLin(String c, int i) { return (hBase(c) + i) % tam; }

    // sondagem quadrática
    int hQuad(String c, int i) { return (hBase(c) + i*i) % tam; }

    // escolhe o tipo de sondagem de acordo com o modo
    int probe(String c, int i) {
        if(tipoProbe==1) return hLin(c,i);
        if(tipoProbe==2) return hQuad(c,i);
        return hDuplo(c,i);
    }

    // insere um registro usando o método de sondagem
    @Override
    void inserir(Registro r) {
        for(int i=0; i<tam; i++) {
            int pos = probe(r.cod, i);
            if(!occ[pos]) { // se posição está livre, insere
                tab[pos]=r;
                occ[pos]=true;
                return;
            } else contCol++; // caso contrário, conta colisão
        }
    }

    // busca um código na tabela
    @Override
    boolean buscar(String c) {
        for(int i=0;i<tam;i++) {
            int pos = probe(c,i);
            if(occ[pos] && tab[pos].cod.equals(c)) return true; // encontrou
            if(!occ[pos]) return false; // posição vazia = não encontrado
        }
        return false;
    }
}

public class Main {
    // gera um código aleatório com 9 dígitos
    static String genCod(Random r) {
        return String.format("%09d", r.nextInt(1000000000));
    }

    // gera uma lista de registros únicos com base em um seed
    static List<Registro> geraDados(int q, long s) {
        Random r = new Random(s);
        Set<String> set = new HashSet<>();
        while(set.size() < q) set.add(genCod(r));
        List<Registro> l = new ArrayList<>();
        for(String c : set) l.add(new Registro(c));
        return l;
    }

    // salva os resultados em um arquivo CSV
    static void salvaCSV(String c) {
        try(FileWriter fw = new FileWriter("res.csv", true)) {
            fw.write(c+"\n");
        } catch(Exception e) {}
    }

    // função principal — executa os testes de desempenho
    public static void main(String[] args) {
        // tamanhos das tabelas e conjuntos de dados
        int[] tams = {1000,10000,100000};
        int[] dsets = {100000,1000000,10000000};
        long s = 12345;

        // cabeçalho do CSV de resultados
        salvaCSV("tipo,tam_tab,tam_dados,hash,modo,tempo_ins,colisoes,tempo_busca,achados,maiores,gaps");

        // loop principal de testes
        for(int tam : tams) {
            for(int d : dsets) {
                List<Registro> regs = geraDados(d,s);

                // teste para cada tipo de função hash (1=div, 2=mult, 3=soma)
                for(int h=1; h<=3; h++) {

                    // Hash com encadeamento
                    HashEncadeamento enc = new HashEncadeamento(tam);

                    long ini = System.nanoTime();
                    for(Registro r : regs) enc.inserir(r,h);
                    long fim = System.nanoTime();
                    double tIns = (fim-ini)/1e6; // tempo em ms
                    int cCol = enc.contCol;

                    ini = System.nanoTime();
                    int ach = 0;
                    for(Registro r : regs) if(enc.buscar(r.cod,h)) ach++;
                    fim = System.nanoTime();
                    double tBus = (fim-ini)/1e6;
                    List<Integer> mai = enc.maiores();

                    // salva resultados do encadeamento
                    salvaCSV("enc,"+tam+","+d+","+h+",nulo,"+tIns+","+cCol+","+tBus+","+ach+","+mai+",--");

                    //  Hash com endereçamento aberto
                    for(int p=1; p<=3; p++) {
                        // p = 1 (linear), 2 (quadrática), 3 (dupla)
                        HashAberto ab = new HashAberto(tam,h,p);

                        ini = System.nanoTime();
                        for(Registro r : regs) ab.inserir(r);
                        fim = System.nanoTime();
                        tIns = (fim-ini)/1e6;
                        cCol = ab.contCol;

                        ini = System.nanoTime();
                        ach = 0;
                        for(Registro r : regs) if(ab.buscar(r.cod)) ach++;
                        fim = System.nanoTime();
                        tBus = (fim-ini)/1e6;

                        //salva resultados do endereçamento aberto
                        salvaCSV("ab,"+tam+","+d+","+h+","+p+","+tIns+","+cCol+","+tBus+","+ach+",--,--");
                    }
                }
            }
        }

        System.out.println("terminou, checa res.csv");
    }
}
