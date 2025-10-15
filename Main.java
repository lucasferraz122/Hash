package hash;

import java.util.*;
import java.io.*;

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

    public static void main(String[] args){
        int[] tams = {1000,10000,100000};
        int[] dsets = {100000,1000000,10000000};
        long s = 12345;

        salvaCSV("tipo,tam_tab,tam_dados,hash,modo,tempo_ins,colisoes,tempo_busca,achados,maiores,gaps");

        for(int tam : tams){
            System.out.println("\nTamanho da tabela: "+tam);
            salvaCSV("# -------- Tabela tamanho "+tam+" --------");

            for(int d : dsets){
                System.out.println("\nConjunto de "+d+" elementos");
                salvaCSV("# --- Conjunto de "+d+" elementos ---");

                List<Registro> regs = geraDados(d,s);

                for(int h=1;h<=3;h++){
                    System.out.println("Hash Encadeamento func "+h);
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
                    System.out.println("  ins:"+tIns+"ms col:"+cCol+" busca:"+tBus+" ach:"+ach+" top:"+mai);

                    for(int p=1;p<=3;p++){
                        System.out.println("Hash Aberto func "+h+" probe "+p);
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
                        System.out.println("  ins:"+tIns+"ms col:"+cCol+" busca:"+tBus+" ach:"+ach);
                    }
                }
                salvaCSV("");
            }
            salvaCSV("");
        }

        System.out.println("\nFIM DOS TESTES, olha res.csv");
    }
}
