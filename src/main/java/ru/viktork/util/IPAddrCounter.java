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

        byte[] indexes = new byte[536870912]; // allocate all index values

        //IP parts 127.27.2.3 -> 127 = values[0], 27 = values[1], 2 = values[2], 3 = values[3]
        int[] values = new int[4]; 
        
        // array for
        // 0 - unique IP's
        // 1 - double IP's
        // 2 - all IP's
        long[] count = new long[3];
        
        try (Stream<String> lines = Files.lines(new File(args[0]).toPath());) {
            lines.forEach( line -> {
                Arrays.fill(values, 0); // flash old values
                
                getIPValues(line, values); // fill IP values
                
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
        double unicPercent = (count[0]*100d) / count[2];
        double notUnicPercent = (count[1]*100d) / count[2];
        
        System.out.println(
            "Всего IP проверено " + count[2] + " из них: " +
            "\n    1.уникальных:      " + count[0] + " (" + String.format("%.0f", unicPercent) + "%)" +
            "\n    2.дублирующих:     " + count[1] + " (" + String.format("%.0f", notUnicPercent) + "%)" +
            "\nИспользовано: \n    1.памяти:          " + (usedMem / (1024 * 1024)) + " MiB" +
            "\n    2.времени:         " + String.format("%.2f", (seconds/60)) + " мин (" + String.format("%.2f", seconds) + " с)"
        );
    }

    private static void getIPValues(String line, int[] result) {
        StringBuilder str = new StringBuilder();
        int count = 0;
        for( char ch: line.toCharArray() ) {
            if(ch == '.') {
                result[count++] = Integer.parseInt(str.toString());
                str.setLength(0);
            }
            else {
                str.append(ch);
            }
        }
        result[count] = Integer.parseInt(str.toString());
    }
}