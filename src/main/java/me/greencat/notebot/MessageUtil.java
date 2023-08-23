package me.greencat.notebot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MessageUtil {
    public static void print(String message){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("[").formatted(Formatting.GOLD).append(Text.literal("GreenCat's NoteBot").formatted(Formatting.WHITE)).append(Text.literal("] -> ").formatted(Formatting.GOLD)).append(Text.literal(message + ".").formatted(Formatting.WHITE)));
        }
    }
}
