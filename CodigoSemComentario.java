import java.util.*;
import java.io.*;

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

abstract class TabelaHash {
    int tam;
    int contCol = 0;
    TabelaHash(int t) { tam = t; }
    abstract void inserir(Registro r);
    abstract boolean buscar(String c);
}

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

public class Main {
    static String genCod(Random r) { return String.format("%09d", r.nextInt(1000000000)); }

    static List<Registro> geraDados(int q, long s) {
        Random r = new Random(s);
        Set<String> set = new HashSet<>();
        while(set.size() < q) set.add(genCod(r));
        List<Registro> l = new ArrayList<>();
        for(String c : set) l.add(new Registro(c));
        return l;
    }

    static void salvaCSV(String c) {
        try(FileWriter fw = new FileWriter("res.csv", true)) { fw.write(c+"\n"); }
        catch(Exception e) {}
    }

    public static void main(String[] args) {
        int[] tams = {1000,10000,100000};
        int[] dsets = {100000,1000000,10000000};
        long s = 12345;
        salvaCSV("tipo,tam_tab,tam_dados,hash,modo,tempo_ins,colisoes,tempo_busca,achados,maiores,gaps");

        for(int tam : tams) {
            for(int d : dsets) {
                List<Registro> regs = geraDados(d,s);
                for(int h=1; h<=3; h++) {
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
                    salvaCSV("enc,"+tam+","+d+","+h+",nulo,"+tIns+","+cCol+","+tBus+","+ach+","+mai+",--");

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
                        salvaCSV("ab,"+tam+","+d+","+h+","+p+","+tIns+","+cCol+","+tBus+","+ach+",--,--");
                    }
                }
            }
        }
        System.out.println("terminou, checa res.csv");
    }
}
