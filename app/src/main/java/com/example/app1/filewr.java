package com.example.app1;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class filewr {
    public void bufferSave(String msg,String filename) {

        File file = new File(Environment.getExternalStorageDirectory().getPath(),filename);
        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(file, true));
            bfw.write(msg);
            bfw.newLine();
            bfw.flush();
            bfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bufferRead(String filename) {
        File file = new File(Environment.getExternalStorageDirectory().getPath(),filename);
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(file));
            String line = bfr.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = bfr.readLine();
            }
            bfr.close();

            Log.d("buffer", "bufferRead: " + sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    


}
