package de.thws;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

import com.google.gson.Gson;

public class Start {
    public static void main(String[] args) throws IOException {

        FileMapper fileMapper = new FileMapper("sounds/I - C.mid", 1, 5);
        //System.out.println(fileMapper.toJson());

        FileMapper fm2= new FileMapper(new File("mapper.json"));

    }
}