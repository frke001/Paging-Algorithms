package org.unibl.etf.simulation;

import org.unibl.etf.virtual_memory.VirtualMemoryManagement;
import java.util.*;


public class Simulation {
    private static final int MIN_BROJ_OKVIRA = 2;
    public static void main(String args[]){

        Scanner skener = new Scanner(System.in);
        int frameNumber,referenceNumber,el,br = 0;
        String input,referenceInput;
        ArrayList<Integer> references = new ArrayList<>();
        System.out.println();
        System.out.println(String.format("%50s","~MEMORY MANAGEMENT~"));
        do {
            System.out.print("\nUnesite broj okvira: ");
            frameNumber = skener.nextInt();
        }while(frameNumber < MIN_BROJ_OKVIRA);
        System.out.print("\nUnesite broj referenci: ");
        referenceNumber = skener.nextInt();
        String ref[];
        int iteration = 0;
        do {
            if(iteration != 0)
                System.out.print("\nUnesite reference: ");
            iteration++;
            referenceInput = skener.nextLine();
            ref = referenceInput.split(",");

        }while(ref.length != referenceNumber);
        for(var element : ref)
            references.add(Integer.parseInt(element));
        VirtualMemoryManagement simulator = new VirtualMemoryManagement(frameNumber,referenceNumber,references);
        System.out.println();
        System.out.println("[1]FIFO");
        System.out.println("[2]LRU");
        System.out.println("[3]Second Chance");
        System.out.println("[4]LFU");
        System.out.println("[5]Optimal");
        System.out.println("[6]Kraj");
        do{
            System.out.print("\nIzaberite algoritam: ");
            input = skener.nextLine();
            if(input.equals("FIFO"))
                simulator.FIFO();
            else if(input.equals("LRU"))
                simulator.LRU();
            else if(input.equals("Second Chance"))
                simulator.SecondChance();
            else if(input.equals("LFU"))
                simulator.LFU();
            else if(input.equals("Optimal"))
                simulator.Optimal();
        }while (!input.equals("Kraj"));
    }
}
