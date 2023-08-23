package me.greencat.notebot.bean;

import me.greencat.notebot.Converter;

import java.util.AbstractMap;

public class Note {
    public final long currentTick;
    public final int currentNote;
    public final Converter.Type layer;

    public Note(long currentTick,int currentNote){
        this.currentTick = currentTick;
        AbstractMap.SimpleEntry<Converter.Type,Integer> note = Converter.converter(currentNote);
        this.currentNote = note.getValue();
        layer = note.getKey();
    }
}
