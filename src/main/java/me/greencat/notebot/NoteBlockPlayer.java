package me.greencat.notebot;

import me.greencat.notebot.bean.Note;
import me.greencat.notebot.mixin.InteractionManagerAccessor;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class NoteBlockPlayer implements ClientTickEvents.StartTick{
    @Override
    public void onStartTick(MinecraftClient client) {
        if(NoteBotClient.currentMusic == null){
            NoteBotClient.tickCounter = 0;
            return;
        }
        if (MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().world == null) {
            return;
        }
        if(NoteBotClient.playing) {
            List<Note> noteList = NoteBotClient.currentMusic.getNote(NoteBotClient.tickCounter);
            NoteBotClient.tickCounter++;
            if(noteList != null) {
                for (Note note : noteList) {
                    BlockPos centerPos = switch (note.layer) {
                        case PIANO -> MinecraftClient.getInstance().player.getBlockPos().add(0, -2, 3);
                        case BASS -> MinecraftClient.getInstance().player.getBlockPos().add(0, 1, 3);
                        case CLOCK -> MinecraftClient.getInstance().player.getBlockPos().add(0, 1, -3);
                        case GUITAR -> MinecraftClient.getInstance().player.getBlockPos().add(0, -2, -3);
                    };
                    int zOffset = note.currentNote % 5;
                    int xOffset = (note.currentNote - (note.currentNote % 5)) / 5;
                    int realZ = zOffset - 2;
                    int realX = xOffset - 2;
                    BlockPos currentPos = centerPos.add(realX, 0, realZ);
                    MinecraftClient.getInstance().player.networkHandler.getConnection().send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, currentPos, Direction.UP));
                    MinecraftClient.getInstance().player.networkHandler.getConnection().send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, currentPos, Direction.UP));
                    /*int counter = 0;
                    for (int i = -2; i < 3; i++) {
                        for (int j = -2; j < 3; j++) {
                            BlockPos currentPos = centerPos.add(i, 0, j);
                            if (counter == note.currentNote) {
                                MinecraftClient.getInstance().player.networkHandler.getConnection().send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, currentPos, Direction.UP));
                                MinecraftClient.getInstance().player.networkHandler.getConnection().send(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, currentPos, Direction.UP));
                                break;
                            }
                            counter++;
                        }
                    }*/
                }
            }
            if(NoteBotClient.tickCounter > NoteBotClient.currentMusic.getLastTick()){
                NoteBotClient.tickCounter = 0;
                NoteBotClient.playing = false;
            }
        }
    }
}
