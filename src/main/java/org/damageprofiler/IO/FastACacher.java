package org.damageprofiler.IO;

import htsjdk.samtools.reference.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;

/**
 * Created by peltzer on 25/06/15.
 */
public class FastACacher {
    private final Logger LOG;
    private final ReferenceSequenceFile refSeq;
    private HashMap<String,byte[]> data = new HashMap<>();

    public FastACacher(File f, Logger LOG) {
        refSeq = ReferenceSequenceFileFactory.getReferenceSequenceFile(f);
        this.LOG = LOG;


        while(true){
            ReferenceSequence rs = refSeq.nextSequence();
            if(rs == null){
                break;
            } else {
                data.put(getKeyName(rs.getName()), rs.getBases());
            }
        }
    }

    public HashMap<String, byte[]> getData() {
        return data;
    }

    public ReferenceSequence getSubSequence(String chr, int start, int end){
        ReferenceSequence subsequenceAt = refSeq.getSubsequenceAt(chr, start, end);
        return subsequenceAt;
    }

    public String getKeyName(String reference_name){
        String name=reference_name;
        String[] reference_name_splitted = reference_name.split("ref");

        if(reference_name_splitted.length>1){
            name = reference_name_splitted[1].substring(1, reference_name_splitted[1].length()-1);

        }
        return name;
    }
}



