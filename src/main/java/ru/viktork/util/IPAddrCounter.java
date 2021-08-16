package ru.viktork.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author Viktor Kotov
 */
public class IPAddrCounter {

    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.out.println("Usage: java ru.viktork.util.IPAddrCounter <path to file>");
            return;
        }

        long start = System.nanoTime();

        // if(args.length == 0) {
        //     System.out.println("Usage: java ru.viktork.util.IPAddrCounter <path to file>");
        //     return;
        // }

        byte[] indexes = new byte[536870912]; // allocate all index values

        int[] values = new int[4];
        
        long[] count = new long[1];
        try (Stream<String> lines = Files.lines(new File(args[0]).toPath());) {
            lines.forEach( line -> {
                Arrays.fill(values, 0); // flash old values
                
                getValues(line, values); // fill IP values
                
                // calculate index
                long index = values[0] << 8;

                index += values[1];
                index = index << 8;
                
                index += values[2];
                index = index << 8;
                
                index += values[3];
                
                int i = (int)(index / 8); // real index
                // end calculate index

                byte b = indexes[i]; // byte contains value
                int shift = 1 << (index % 8);
                if((b & shift) == 0) {
                    b |= shift;
                    indexes[i] = b;
                    count[0]++;
                }
                else {
                    count[1]++;
                }
                count[2]++;
            });
        }

        long finish = System.nanoTime();
        long elapsed = finish - start;

        Runtime rt = Runtime.getRuntime();
        long usedMem = rt.totalMemory() - rt.freeMemory();
        
        double seconds = (double) elapsed / 1000000000;
        double unic_percent = (count[0]*100)/count[2];
        double not_unic_percent = (count[1]*100)/count[2];
        
        System.out.println(
            "Всего IP проверено " + count[2] + " из них: " +
            "\n    1.уникальных:      " + count[0] + " (" + String.format("%.0f", unic_percent) + "%)" +
            "\n    2.дублирующих:     " + count[1] + " (" + String.format("%.0f", not_unic_percent) + "%)" +
            "\nИспользовано: \n    1.памяти:          " + (usedMem / (1024 * 1024)) + " MiB" +
            "\n    2.времени:         " + String.format("%.2f", (seconds/60)) + " мин (" + String.format("%.2f", seconds) + " с)"
        );
    }

    private static void getValues(String line, int[] result) {
        String str = "";
        int count = 0;
        for( char ch: line.toCharArray() ) {
            if(ch == '.') {
                result[count++] = Integer.parseInt(str);
                str = "";
            }
            else {
                str += ch;
            }
        }
        result[count] = Integer.parseInt(str);
    }
}