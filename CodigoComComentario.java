import java.util.*;
import java.io.*;

// classe que representa um registro salvo na tabela hash
class Registro {
    String cod; // código único do registro

    Registro(String c) { cod = c; }

    // retorna o código do registro em formato string
    public String toString() { return cod; }
}

// implementação de uma lista encadeada simples
class ListaEncadeada {
    // nó interno da lista (guarda o registro e a ligação pro próximo)
    static class No {
        Registro r;
        No prox;
        No(Registro rr) { r = rr; }
    }

    No inicio; // primeiro nó da lista
    int tam = 0; // quantos registros tem aqui

    // insere novo registro no começo da lista
    void inserir(Registro rr) {
        No n = new No(rr);
        n.prox = inicio;
        inicio = n;
        tam++;
    }

    // procura um código dentro da lista
    boolean buscar(String c) {
        No aux = inicio;
        while(aux != null) {
            if(aux.r.cod.equals(c)) return true;
            aux = aux.prox;
        }
        return false;
    }
}

// classe base pros tipos de tabela hash
abstract class TabelaHash {
    int tam; // tamanho da tabela
    int contCol = 0; // contador de colisões

    TabelaHash(int t) { tam = t; }

    // métodos que vão ser implementados nas classes filhas
    abstract void inserir(Registro r);
    abstract boolean buscar(String c);
}

// tabela hash usando encadeamento separado
class HashEncadeamento extends TabelaHash {
    ListaEncadeada[] tab; // vetor de listas (os buckets)

    HashEncadeamento(int t) {
        super(t);
        tab = new ListaEncadeada[t];
        for(int i=0; i<t; i++) tab[i] = new ListaEncadeada();
    }

    // hash por divisão (simples e direto)
    int hashDiv(String c) { return Math.abs(Integer.parseInt(c) % tam); }

    // hash por multiplicação (usa constante de Knuth)
    int hashMult(String c) {
        double A = 0.618;
        int k = Integer.parseInt(c);
        return (int)(tam * ((k * A) % 1));
    }

    // hash pela soma dos caracteres
    int hashSoma(String c) {
        int s = 0;
        for(char ch : c.toCharArray()) s += ch;
        return s % tam;
    }

    // escolhe qual hash usar (1 = div, 2 = mult, 3 = soma)
    int hashEscolhe(String c, int t) {
        if(t==1) return hashDiv(c);
        if(t==2) return hashMult(c);
        return hashSoma(c);
    }

    // insere o registro usando a função hash escolhida
    void inserir(Registro r, int t) {
        int pos = hashEscolhe(r.cod, t);
        if(tab[pos].inicio != null) contCol++; // se já tinha algo, é colisão
        tab[pos].inserir(r);
    }

    @Override
    void inserir(Registro r) { inserir(r,1); }

    // busca um registro usando a função de hash certa
    boolean buscar(String c, int t) {
        int pos = hashEscolhe(c, t);
        return tab[pos].buscar(c);
    }

    @Override
    boolean buscar(String c) { return buscar(c,1); }

    // retorna as 3 maiores listas da tabela
    List<Integer> maiores() {
        List<Integer> l = new ArrayList<>();
        for(ListaEncadeada x : tab) l.add(x.tam);
        l.sort(Collections.reverseOrder());
        return l.subList(0, Math.min(3, l.size()));
    }
}

// tabela hash usando endereçamento aberto
class HashAberto extends TabelaHash {
    Registro[] tab; // vetor de registros
    boolean[] occ;  // marca se a posição já foi usada
    int tipoHash, tipoProbe; // tipo do hash e tipo da sondagem

    HashAberto(int t, int h, int p) {
        super(t);
        tab = new Registro[t];
        occ = new boolean[t];
        tipoHash = h;  // 1 = div, 2 = mult, 3 = soma
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

    // escolhe qual hash base vai ser usado
    int hBase(String c) {
        if(tipoHash==1) return hDiv(c);
        if(tipoHash==2) return hMult(c);
        return hSoma(c);
    }

    // sondagem dupla (double hashing)
    int hDuplo(String c, int i) {
        int h1 = hBase(c);
        int h2 = 1 + (Integer.parseInt(c) % (tam-1));
        return (h1 + i*h2) % tam;
    }

    // sondagem linear
    int hLin(String c, int i) { return (hBase(c) + i) % tam; }

    // sondagem quadrática
    int hQuad(String c, int i) { return (hBase(c) + i*i) % tam; }

    // escolhe qual tipo de sondagem usar
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
            if(!occ[pos]) { // achou um espaço livre
                tab[pos]=r;
                occ[pos]=true;
                return;
            } else contCol++; // deu colisão
        }
    }

    // busca um código dentro da tabela
    @Override
    boolean buscar(String c) {
        for(int i=0;i<tam;i++) {
            int pos = probe(c,i);
            if(occ[pos] && tab[pos].cod.equals(c)) return true; // achou
            if(!occ[pos]) return false; // posição livre = não existe
        }
        return false;
    }
}

public class Main {
    // gera código aleatório de 9 dígitos
    static String genCod(Random r) {
        return String.format("%09d", r.nextInt(1000000000));
    }

    // gera lista de registros únicos com base em um seed
    static List<Registro> geraDados(int q, long s) {
        Random r = new Random(s);
        Set<String> set = new HashSet<>();
        while(set.size() < q) set.add(genCod(r));
        List<Registro> l = new ArrayList<>();
        for(String c : set) l.add(new Registro(c));
        return l;
    }

    // salva os resultados num arquivo csv
    static void salvaCSV(String c) {
        try(FileWriter fw = new FileWriter("res.csv", true)) {
            fw.write(c+"\n");
        } catch(Exception e) {}
    }

    // função principal — roda os testes e mede desempenho
    public static void main(String[] args) {
        int[] tams = {1000,10000,100000}; // tamanhos das tabelas
        int[] dsets = {100000,1000000,10000000}; // tamanhos dos dados
        long s = 12345;

        salvaCSV("tipo,tam_tab,tam_dados,hash,modo,tempo_ins,colisoes,tempo_busca,achados,maiores,gaps");

        for(int tam : tams) {
            for(int d : dsets) {
                System.out.println("\n-------------------------------------");
                System.out.println("tamanho da tabela: " + tam + " | qtd de dados: " + d);
                System.out.println("-------------------------------------");

                List<Registro> regs = geraDados(d,s);

                // testa com os 3 tipos de hash
                for(int h=1; h<=3; h++) {

                    // hash com encadeamento
                    HashEncadeamento enc = new HashEncadeamento(tam);

                    long ini = System.nanoTime();
                    for(Registro r : regs) enc.inserir(r,h);
                    long fim = System.nanoTime();
                    double tIns = (fim-ini)/1e6;
                    int cCol = enc.contCol;

                    ini = System.nanoTime();
                    int ach = 0;
                    for(Registro r : regs) if(enc.buscar(r.cod,h)) ach++;
                    fim = System.nanoTime();
                    double tBus = (fim-ini)/1e6;
                    List<Integer> mai = enc.maiores();

                    // printa resultados do encadeamento
                    System.out.println("\nencadeamento - hash " + h);
                    System.out.println("tempo de inserção: " + tIns + " ms");
                    System.out.println("colisões: " + cCol);
                    System.out.println("tempo de busca: " + tBus + " ms");
                    System.out.println("achados: " + ach);
                    System.out.println("maiores listas: " + mai);

                    salvaCSV("enc,"+tam+","+d+","+h+",nulo,"+tIns+","+cCol+","+tBus+","+ach+","+mai+",--");

                    // hash com endereçamento aberto
                    for(int p=1; p<=3; p++) {
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

                        // printa resultados do aberto
                        System.out.println("\naberto - hash " + h + " | probe " + p);
                        System.out.println("tempo de inserção: " + tIns + " ms");
                        System.out.println("colisões: " + cCol);
                        System.out.println("tempo de busca: " + tBus + " ms");
                        System.out.println("achados: " + ach);

                        salvaCSV("ab,"+tam+","+d+","+h+","+p+","+tIns+","+cCol+","+tBus+","+ach+",--,--");
                    }
                }
            }
        }

        System.out.println("\nterminou! confere o res.csv");
    }
}
