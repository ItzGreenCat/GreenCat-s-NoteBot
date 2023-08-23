package me.greencat.notebot;

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

import java.util.ArrayList;
import java.util.List;

public class Calibrator implements ClientTickEvents.StartTick{
    public static boolean calibrating = false;
    public static boolean clicking = false;
    public static boolean printed = true;
    public static List<BlockPos> posList = new ArrayList<>();
    public static void calibrate(){
        if(!calibrating){
            calibrating = true;
            BlockPos playerPos;
            if (MinecraftClient.getInstance().player != null) {
                playerPos = MinecraftClient.getInstance().player.getBlockPos();
                BlockPos noteblockGroupCenterPos;
                noteblockGroupCenterPos = playerPos.add(0,1,3);
                calibrateNoteblockGroup(noteblockGroupCenterPos);
                noteblockGroupCenterPos = playerPos.add(0,-2,3);
                calibrateNoteblockGroup(noteblockGroupCenterPos);
                noteblockGroupCenterPos = playerPos.add(0,1,-3);
                calibrateNoteblockGroup(noteblockGroupCenterPos);
                noteblockGroupCenterPos = playerPos.add(0,-2,-3);
                calibrateNoteblockGroup(noteblockGroupCenterPos);
                MessageUtil.print("校准列表长度:" + posList.size());
                clicking = true;
                printed = false;
            }
        }
    }
    public static void calibrateNoteblockGroup(BlockPos noteblockGroupCenterPos){
        int counter = 0;
        for(int i = -2;i < 3;i++){
            for(int j = -2;j < 3;j++){
                BlockPos currentPos = noteblockGroupCenterPos.add(i,0,j);
                if(MinecraftClient.getInstance().world.getBlockState(currentPos).getBlock() == Blocks.NOTE_BLOCK){
                    int currentNote = MinecraftClient.getInstance().world.getBlockState(currentPos).get(NoteBlock.NOTE);
                    int diff = counter - currentNote;
                    if(diff > 0){
                        for(int k = 0;k < diff;k++){
                            posList.add(currentPos);
                        }
                    } else if (diff < 0) {
                        int fixedDiff = 25 - currentNote;
                        int realDiff = fixedDiff + counter;
                        for(int k = 0;k < realDiff;k++){
                            posList.add(currentPos);
                        }
                    }
                    counter++;
                }
            }
        }
    }

    @Override
    public void onStartTick(MinecraftClient client) {
        if (clicking && calibrating && !posList.isEmpty()) {
            BlockPos pos = posList.get(0);
            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                if (MinecraftClient.getInstance().interactionManager != null) {
                    ((InteractionManagerAccessor)MinecraftClient.getInstance().interactionManager).sendSequencedPackerWrapper(MinecraftClient.getInstance().world, (sequence) -> {
                        return new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), Direction.UP, pos, false), sequence);
                    });
                    posList.remove(0);
                }
            }
        } else if(posList.isEmpty()){
            if(!printed) {
                clicking = false;
                calibrating = false;
                MessageUtil.print("校准完成");
                printed = true;
            }
        }
    }
}
