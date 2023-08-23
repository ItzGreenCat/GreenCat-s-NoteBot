package me.greencat.notebot;

import java.util.AbstractMap;

public class Converter {
    public static AbstractMap.SimpleEntry<Type,Integer> converter(int i){
        int temp = i - 60;
        int temp2 = temp + 6;
        if(temp2 >= 0 && temp2 <= 24){
            return new AbstractMap.SimpleEntry<>(Type.PIANO,temp2);
        } else if(temp2 >= 0) {
            int temp6 = temp2 - 24;
            return new AbstractMap.SimpleEntry<>(Type.CLOCK,temp6);
        }
        int temp4 = 6 * 2 + temp2;
        if(temp4 < 0){
            int temp5 = 6 * 2 + temp4;
            return new AbstractMap.SimpleEntry<>(Type.BASS,temp5);
        } else {
            return new AbstractMap.SimpleEntry<>(Type.GUITAR,temp4);
        }

    }
    public enum Type{
        BASS,GUITAR,PIANO,CLOCK
    }
}
