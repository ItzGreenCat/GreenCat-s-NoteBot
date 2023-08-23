package me.greencat.notebot.bean;

import me.greencat.notebot.MessageUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MidiMusic {
    private final int resolution;
    private final HashMap<Long,NoteList> noteMap = new HashMap<>();
    public MidiMusic(int resolution){
        this.resolution = resolution;
    }
    public void addNote(Note note){
        if(!noteMap.containsKey(note.currentTick)){
            noteMap.put(note.currentTick,new NoteList(note.currentTick));
        }
        noteMap.get(note.currentTick).notes.add(note);
    }
    public List<Note> getNote(long tick){
        for(Map.Entry<Long,NoteList> entry : noteMap.entrySet()){
            double delaySec = new BigDecimal(timeConverter(resolution,entry.getKey())).divide(new BigDecimal(1000), RoundingMode.HALF_UP).doubleValue();
            if(((int) (delaySec / 25)) == tick){
                return entry.getValue().notes;
            }
        }
        return null;
    }
    public long getLastTick(){
        long latest = 0L;
        for(Map.Entry<Long,NoteList> entry : noteMap.entrySet()){
            double delaySec = new BigDecimal(timeConverter(resolution,entry.getKey())).divide(new BigDecimal(1000), RoundingMode.HALF_UP).doubleValue();
            if(((int) (delaySec / 25)) > latest){
                latest = ((int) (delaySec / 25));
            }
        }
        return latest;
    }
    public int size(){
        return noteMap.size();
    }
    public long timeConverter(double resolution,long currentTick){
        double scalingResolution = 480 / resolution;
        return (long) (currentTick * resolution * scalingResolution * scalingResolution);
    }
}
