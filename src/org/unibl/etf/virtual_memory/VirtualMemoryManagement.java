package org.unibl.etf.virtual_memory;

import org.unibl.etf.addition.Pair;

import javax.swing.*;
import java.util.*;
import java.util.function.UnaryOperator;

public class VirtualMemoryManagement {
    public static String PAGE_FAULT = "PF";
    public static String R_BIT = "R";
    private static int MAX = 1_000_000;
    public int frameNumber;
    public int referenceNumber;
    public ArrayList<Integer> references;


    public VirtualMemoryManagement(){
        super();
    }
    public VirtualMemoryManagement(int frameNumber,int referenceNumber,ArrayList<Integer> references){
        this.frameNumber = frameNumber;
        this.referenceNumber = referenceNumber;
        this.references = new ArrayList<>(referenceNumber);
        this.references.addAll(references);
    }

    public void FIFO(){
        String outputTable[][] = this.getOutputTable();
        LinkedList<Integer> frames = new LinkedList<>();
        int pageFaults = 0;
        for(int i = 0; i < referenceNumber; i++){

            outputTable[0][i] = references.get(i).toString();
            if(!frames.contains(references.get(i))){
                pageFaults++;
                outputTable[1][i] = PAGE_FAULT;
                if(frames.size() == frameNumber){
                    frames.poll();
                }
                frames.offer(references.get(i));
            }
            int j = 2;
            Iterator<Integer> it = frames.descendingIterator();
            while(it.hasNext()){
                Integer number = it.next();
                outputTable[j][i] = number.toString();
                j++;
            }
        }
        this.print("FIFO",outputTable,pageFaults);
    }

    public void LRU(){
        String outputTable[][] = this.getOutputTable();
        LinkedList<Integer> frames = new LinkedList<>();
        int pageFaults = 0;
        for(int i = 0; i < referenceNumber; i++){

            outputTable[0][i] = references.get(i).toString();
            if(!frames.contains(references.get(i))){
                pageFaults++;
                outputTable[1][i] = PAGE_FAULT;
                if(frames.size() == frameNumber){
                    frames.poll();
                }
                frames.offer(references.get(i));
            }else{
                ArrayList<Integer> helpArray = new ArrayList<>();
                while (!frames.isEmpty())
                    helpArray.add(frames.poll());

                helpArray.remove(references.get(i));
                for (var el : helpArray)
                    frames.offer(el);
                frames.offer(references.get(i));
            }
            int j = 2;
            Iterator<Integer> it = frames.descendingIterator();
            while(it.hasNext()){
                Integer number = it.next();
                outputTable[j][i] = number.toString();
                j++;
            }
        }
        this.print("LRU",outputTable,pageFaults);
    }

    public void SecondChance(){
        String outputTable[][] = this.getOutputTable();
        Deque<Pair<Integer,Boolean>> frames = new LinkedList<>();
        Scanner skener = new Scanner(System.in);
        int page;
        do {
            System.out.print("Unesite stranicu koja ima R bit: ");
            page = skener.nextInt();
        }while (!references.contains(page));
        int pageFaults = 0;
        for(int i = 0; i < referenceNumber; i++){
            outputTable[0][i] = references.get(i).toString();
            if(!frames.contains(new Pair<>(references.get(i),true)) &&
                    !frames.contains(new Pair<>(references.get(i),false))){
                pageFaults++;
                outputTable[1][i] = PAGE_FAULT;
                if(frames.size() == frameNumber) {
                    if (frames.peekFirst().second.equals(true)) {
                        Pair<Integer, Boolean> p = frames.pollFirst();
                        frames.offerLast(new Pair<>(page, false));
                        frames.pollFirst();
                        frames.offerLast(new Pair<>(references.get(i), false));
                    }else{
                        frames.poll();
                        if(references.get(i) == page)
                            frames.offerLast(new Pair<>(references.get(i),true));
                        else
                            frames.offerLast(new Pair<>(references.get(i),false));
                    }
                } else {// ako red nije pun
                    if (references.get(i) == page)
                        frames.offerLast(new Pair<>(references.get(i), true));
                    else
                        frames.offerLast(new Pair<>(references.get(i), false));
                }
            }else {
                boolean status = true;
                if(frames.contains(new Pair<>(page,false)) && references.get(i) == page) // ako sadrzi 2 a naidjemo na referencu 2 postavimo R bit
                    status = false;
                if(!status) {
                    ArrayList<Pair<Integer,Boolean>> helpArray = new ArrayList<>();
                    while (!frames.isEmpty())
                        helpArray.add(frames.poll());
                    int index = helpArray.indexOf(new Pair<>(page, false));
                    //helpArray.remove(new Pair<>(page, false));
                    helpArray.set(index,new Pair<>(page, true)); // prepisemo sadrzaj
                    for (var el : helpArray)
                        frames.offerLast(el);
                }
            }
            int j = 2;
            Iterator<Pair<Integer,Boolean>> it = frames.descendingIterator();
            while(it.hasNext()){
                Pair<Integer,Boolean> p = it.next();
                if(p.second.equals(true))
                    outputTable[j++][i] = p.first.toString() + R_BIT;
                else
                    outputTable[j++][i] = p.first.toString();
            }
        }
        this.print("Second Chance",outputTable,pageFaults);
    }
    public void LFU() {
        String outputTable[][] = this.getOutputTable();
        ArrayList<Pair<Integer, Integer>> frames = new ArrayList<>();
        Scanner skener = new Scanner(System.in);
        System.out.print("Unesite inicijalnu vrijednost brojaca: ");
        int brojac = skener.nextInt();
        System.out.print("Unesite vrijednost dekrementa: ");
        int dekrement = skener.nextInt();
        System.out.print("Unesite vrijednost inkrementa: ");
        int inkrement = skener.nextInt();
        int pageFaults = 0;
        for (int i = 0; i < referenceNumber; i++) {
            outputTable[0][i] = references.get(i).toString();
            if (!contains(frames, new Pair<>(references.get(i), 0))) {
                pageFaults++;
                outputTable[1][i] = PAGE_FAULT;
                this.replaceAll(frames, dekrement);
                this.sort(frames);
                if (frames.size() == frameNumber) {
                    frames.remove(0);
                    frames.add(new Pair<>(references.get(i), brojac));
                } else {
                    frames.add(new Pair<>(references.get(i), brojac));
                }
                this.sort(frames);
            } else {
                this.replaceAll(frames, dekrement);
                this.sort(frames);
                Integer second = this.remove(frames,references.get(i));
                frames.add(new Pair<>(references.get(i),second + inkrement + 1));
                this.sort(frames);
            }
            int j = 2;
            for (int k = frames.size() - 1; k >= 0; k--){
                    outputTable[j++][i] = frames.get(k).first.toString(); //+ frames.get(k).second.toString();
            }
        }
        this.print("LFU",outputTable,pageFaults);
    }

    public void Optimal() {
        String outputTable[][] = this.getOutputTable();
        ArrayList<Integer> frames = new ArrayList<>();
        int pageFaults = 0;
        for (int i = 0; i < referenceNumber; i++) {
            outputTable[0][i] = references.get(i).toString();
            if (!frames.contains(references.get(i))) {
                pageFaults++;
                outputTable[1][i] = PAGE_FAULT;
                if (frames.size() == frameNumber) {
                    this.removeLFU(frames, i);
                }
                frames.add(references.get(i));
            }
            int j = 2;
            for (var el : frames) {
                outputTable[j++][i] = el.toString();
            }
        }
        this.print("Optimal",outputTable,pageFaults);
    }

    private void print(String algorithm,String[][] outputTable,int pageFaults){
        System.out.println("\n" + algorithm);
        printOutputTabel(outputTable);
        System.out.println("\n\nEfikasnost algoritma: PF = " + pageFaults + " => pf =" + pageFaults +
                " / " + referenceNumber + " = " + efficiency(pageFaults) + "\n");
    }
    private void removeLFU(ArrayList<Integer> frames,int possition){
        ArrayList<Integer> notFound = new ArrayList<>();
        ArrayList<Integer> fromPossition = new ArrayList<>();
        int max = 0;
        for(int i = possition; i < referenceNumber; i++) {
            fromPossition.add(references.get(i));
        }
        for(var element : frames){
            int index = fromPossition.indexOf(element);
            if(index == -1)
                notFound.add(element);
            index += possition;
            if(index > max)
                max = index;
        }
        Integer el;
        if(notFound.size() != 0){
            el = notFound.get(notFound.size() - 1);
            frames.remove(el);
        }else{
            el = references.get(max);
            frames.remove(el);
        }
    }
    private void sort(ArrayList<Pair<Integer,Integer>> list){
        Collections.sort(list, new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                if(o1.second > o2.second)
                    return 1;
                else if(o1.second < o2.second)
                    return -1;
                else
                    return 0;
            }
        });
    }
    private String efficiency(int pageFaults){
        double pageFaultsTemp = (double) pageFaults;
        double referenceNumberTemp = (double) referenceNumber;
        double result = (double)(pageFaultsTemp/referenceNumberTemp)*100;
        return String.format("%.2f",result) + "%";
    }
    private String[][] getOutputTable(){
        String [][] outputTable = new String[frameNumber + 2][];
        for(int i = 0; i < frameNumber + 2; i++) {
            outputTable[i] = new String[referenceNumber];
            for (int j = 0; j < referenceNumber; j++)
                outputTable[i][j] = " ";
        }
        return outputTable;
    }
    private void printOutputTabel(String[][] outputTable){
        for(int i = 0; i < frameNumber + 2; i++) {
            System.out.println();
            for (int j = 0; j < referenceNumber; j++) {
                System.out.print(String.format("%-4s",outputTable[i][j]));
            }
        }
    }
    private boolean contains(ArrayList<Pair<Integer,Integer>> frames,Pair<Integer,Integer> el){
        for (var pair : frames){
            if(pair.first.equals(el.first))
                return true;
        }
        return false;
    }
    private void replaceAll(ArrayList<Pair<Integer,Integer>> frames,int dekrement){
        frames.replaceAll(new UnaryOperator<Pair<Integer, Integer>>() {
            @Override
            public Pair<Integer, Integer> apply(Pair<Integer, Integer> integerIntegerPair) {
                return new Pair<>(integerIntegerPair.first, integerIntegerPair.second - dekrement);
            }
        });
    }
    private Integer remove(ArrayList<Pair<Integer,Integer>> frames,Integer element){
        Integer second = 0;
        for(var el : frames){
            if(el.first.equals(element))
                second = el.second;
        }
        frames.remove(new Pair<>(element,second));
        return second;
    }
}
