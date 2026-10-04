package me.ez.hush.client;

import me.ez.hush.Config;
import me.ez.hush.Main;
import me.ez.hush.muffler.MufflerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * Client-only hook that drops sounds inside a muffler's radius. Uses NeoForge's
 * {@link PlaySoundEvent}, the supported pre-play hook, so no mixin is needed.
 */
@EventBusSubscriber(modid = Main.MOD_ID, value = Dist.CLIENT)
public final class MufflerEvents {

    private MufflerEvents() {}

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        if (!Config.ENABLED.get() || !Config.MUFFLER_ENABLED.get()) {
            return;
        }
        SoundInstance sound = event.getSound();
        // Relative sounds (music, UI, local ambience) have no world position.
        if (sound == null || sound.isRelative()) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (MufflerRegistry.isMuffled(level, sound.getX(), sound.getY(), sound.getZ(), sound.getSource())) {
            event.setSound(null);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level && level.isClientSide()) {
            MufflerRegistry.clearAll();
        }
    }
}
