package me.greencat.notebot.bean;

import java.util.ArrayList;
import java.util.List;

public class NoteList {
    public final List<Note> notes = new ArrayList<>();
    public final long currentTick;
    public NoteList(long tick){
        currentTick = tick;
    }
}
