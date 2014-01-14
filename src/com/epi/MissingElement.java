package com.drx.epi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

/**
 * @author translated from c++ by Blazheev Alexander
 */
public class MissingElement {
    // @include
    public static int find_missing_element(InputStream ifs) throws IOException {
        int[] counter = new int[1 << 16];
        ifs.mark(Integer.MAX_VALUE);
        Scanner s = new Scanner(ifs);
        while(s.hasNextInt()) {
            ++counter[s.nextInt() >> 16];
        }

        for(int i = 0; i < counter.length; ++i) {
            // Find one bucket contains less than (1 << 16) elements.
            if(counter[i] < (1 << 16)) {
                BitSet bit_vec = new BitSet(1 << 16);
                ifs.reset();
                s = new Scanner(ifs);
                while(s.hasNext()) {
                    int x = s.nextInt();
                    if(i == (x >> 16)) {
                        bit_vec.set(((1 << 16) - 1) & x);  // gets the lower 16 bits of x.
                    }
                }
                ifs.close();

                for(int j = 0; j < (1 << 16); ++j) {
                    if(!bit_vec.get(j)) {
                        return (i << 16) | j;
                    }
                }
            }
        }
        // @exclude
        throw new RuntimeException("no missing element");
        // @include
    }
    // @exclude

    public static void main(String[] args) {
        int n = 990000;
        Random r = new Random();
        if(args.length == 1) {
            n = Integer.parseInt(args[0]);
        }
        File missingFile = new File("missing.txt");
        HashSet<Integer> hash = new HashSet<Integer>();
        FileOutputStream ofs = null;
        try {
            try {
                ofs = new FileOutputStream(missingFile);
                OutputStreamWriter osw = new OutputStreamWriter(ofs);
                for(int i = 0; i < n; ++i) {
                    int x;
                    do {
                        x = r.nextInt(1000000);
                    } while(!hash.add(x));
                    osw.write(x + "\n");
                }
            } finally {
                if(ofs != null) {
                    ofs.close();
                }
            }

            FileInputStream ifs = null;
            try {
                ifs = new FileInputStream(missingFile);
                BufferedInputStream bis = new BufferedInputStream(ifs);
                int missing = find_missing_element(bis);
                assert (!hash.contains(missing));
                System.out.println(missing);
            } finally {
                if(ifs != null)
                    ifs.close();
            }
        } catch(IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            // Remove file after the execution.
            missingFile.delete();
        }
    }
}
