package edu.umw.cpsc.marche.charbel.whereareyouapp;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Charbel on 4/21/2018.
 */

public class MyFileIO {
    public MyFileIO(){

    }

    public void writeToFile(String fileContents, String filename, Context context) {
        try {
            Log.d("In fileIO", "Starting to write to file " + filename);
            FileOutputStream outputStream;
            try {
                outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("In fileIO", "Finished write to file " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllFromFile(List<String> namesArray, String path, Context context) {
        try{
            //Write into array from file, then close scanner
            Log.d("MyFileIO", "Before reading!");
            File file = new File( path );
            Scanner in = new Scanner(file);
            while (in.hasNextLine()){
                namesArray.add(in.nextLine());
            }
            in.close();
            Log.d("MyFileIO", "After reading!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return namesArray;

    }
}
