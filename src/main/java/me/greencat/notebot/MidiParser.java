package me.greencat.notebot;

import me.greencat.notebot.bean.MidiMusic;
import me.greencat.notebot.bean.Note;
import net.minecraft.client.MinecraftClient;


import javax.sound.midi.*;
import java.io.File;

public class MidiParser {
    public static MidiMusic parse(String midiFileName){
        try {
            Sequence sequence = MidiSystem.getSequence(new File(MinecraftClient.getInstance().runDirectory, midiFileName));
            Track[] tracks = sequence.getTracks();
            MidiMusic music = new MidiMusic(sequence.getResolution());
            for(Track track : tracks){
                for(int i = 0;i < track.size();i++){
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();
                    if(message instanceof ShortMessage){
                        ShortMessage sMessage = (ShortMessage) message;
                        if(sMessage.getCommand() == ShortMessage.NOTE_ON){
                            int key = sMessage.getData1();
                            long currentTick = event.getTick();
                            music.addNote(new Note(currentTick,key));
                        }
                    }
                }
            }
            MessageUtil.print("加载完成");
            MessageUtil.print(String.format("共有%s个轨道",tracks.length));
            MessageUtil.print(String.format("共有%d个同Tick内容",music.size()));
            return music;
        } catch(Exception e){
            MessageUtil.print("无法加载Midi");
            e.printStackTrace();
        }
        return null;
    }
}
