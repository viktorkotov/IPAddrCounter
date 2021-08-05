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

        byte[] indexes = new byte[1842135835]; // allocate all index values

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
                    count[0] += 1; // new value
                }
            });
        }
        
        System.out.println(count[0]); // print result
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
