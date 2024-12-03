package com.example.app1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import android.os.Process;


public class hookcheck {
    public boolean hasReadProcMaps(String paramString) {
        try {
            Object localObject = new HashSet<>();
            BufferedReader localBufferedReader = new BufferedReader(new FileReader("/proc/" + Process.myPid() + "/maps"));
            for (; ; ) {
                String str = localBufferedReader.readLine();
                if (str == null) {
                    break;
                }
                if ((str.endsWith(".so")) || (str.endsWith(".jar"))) {
                    ((Set) localObject).add(str.substring(str.lastIndexOf(" ") + 1));
                }
            }
            localBufferedReader.close();
            localObject = ((Set) localObject).iterator();
            while (((Iterator<?>) localObject).hasNext()) {
                boolean bool = ((String) ((Iterator<?>) localObject).next()).contains(paramString);
                if (bool) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

}
