package com.chavgun;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));




    public static void writeMessage(String s){
        System.out.println(s);
    }

    public static String readString(){
        String out = null;
        do {
            try {
                out = reader.readLine();
            } catch (IOException e) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }while(out==null);
        return out;
    }

    public static int readInt(){
        while (true){
            try {
                return Integer.parseInt(readString());
            }catch (NumberFormatException e){
                System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            }
        }

    }


}
