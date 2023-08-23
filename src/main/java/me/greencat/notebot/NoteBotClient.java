package me.greencat.notebot;

import com.mojang.brigadier.arguments.StringArgumentType;
import me.greencat.notebot.bean.MidiMusic;
import me.greencat.notebot.bean.Note;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class NoteBotClient implements ClientModInitializer {
    public static MidiMusic currentMusic = null;
    public static long tickCounter = 0;

    public static boolean playing = false;
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("notebot")
                    .then(ClientCommandManager.literal("load")
                        .then(ClientCommandManager.argument("fileName",StringArgumentType.greedyString())
                                .executes((command) -> {
                                    String fileName = StringArgumentType.getString(command,"fileName");
                                    currentMusic = MidiParser.parse(fileName);
                                    tickCounter = 0;
                                    playing = false;
                                    return 0;
                                })))
                    .then(ClientCommandManager.literal("clear")
                            .executes((command) -> {
                                currentMusic = null;
                                tickCounter = 0;
                                playing = false;
                                return 0;
                            }))
                    .then(ClientCommandManager.literal("start")
                            .executes((command) -> {
                                playing = true;
                                return 0;
                            }))
                    .then(ClientCommandManager.literal("pause")
                            .executes((command) -> {
                                playing = false;
                                return 0;
                            }))
                    .then(ClientCommandManager.literal("calibrate")
                            .executes((command) -> {
                                Calibrator.calibrate();
                                return 0;
                            }))
                    .then(ClientCommandManager.literal("help")
                            .executes((command) -> {
                                MessageUtil.print("Z+方向下层:泥土");
                                MessageUtil.print("Z+方向上层:木板");
                                MessageUtil.print("Z-方向下层:羊毛");
                                MessageUtil.print("Z-方向下层:金块");
                                return 0;
                            })));
        });
        ClientTickEvents.START_CLIENT_TICK.register(new NoteBlockPlayer());
        ClientTickEvents.START_CLIENT_TICK.register(new Calibrator());
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            int scaledWidth = drawContext.getScaledWindowWidth();
            int scaledHeight = drawContext.getScaledWindowHeight();
            if (currentMusic != null) {
                List<MutableText> lines = new ArrayList<>();
                lines.add(Text.literal("当前加载的Midi长度:" + currentMusic.size()));
                lines.add(Text.literal("当前播放Tick数量:" + tickCounter));
                List<Note> noteList = currentMusic.getNote(tickCounter);
                if (noteList != null) {
                    for (Note note : noteList) {
                        lines.add(Text.literal("当前音符:" + note.currentNote));
                    }
                }
                for(int i = 0;i < lines.size();i++){
                    drawContext.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer,lines.get(i),scaledWidth / 2,(scaledHeight / 2) + 10 + i * 13, Color.ORANGE.getRGB());
                }
            }
        });
    }
}
