package me.ez.hush.muffler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.ez.hush.Config;
import me.ez.hush.block.SoundMufflerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Tracks the positions of loaded sound mufflers and answers whether a sound at
 * a given point is inside one. Kept cheap on purpose: the sound hook runs for
 * every positional sound, so only loaded, enabled mufflers in the current
 * dimension are considered.
 */
public final class MufflerRegistry {

    private static final Map<ResourceKey<Level>, Set<BlockPos>> MUFFLERS = new HashMap<>();

    private MufflerRegistry() {}

    public static void add(Level level, BlockPos pos) {
        MUFFLERS.computeIfAbsent(level.dimension(), key -> new HashSet<>()).add(pos.immutable());
    }

    public static void remove(Level level, BlockPos pos) {
        Set<BlockPos> positions = MUFFLERS.get(level.dimension());
        if (positions != null) {
            positions.remove(pos);
            if (positions.isEmpty()) {
                MUFFLERS.remove(level.dimension());
            }
        }
    }

    /** Drops every tracked muffler, e.g. when the client level unloads. */
    public static void clearAll() {
        MUFFLERS.clear();
    }

    public static boolean isMuffled(Level level, double x, double y, double z, SoundSource source) {
        if (!Config.isMuted(source)) {
            return false;
        }
        Set<BlockPos> positions = MUFFLERS.get(level.dimension());
        if (positions == null || positions.isEmpty()) {
            return false;
        }

        double radius = Config.MUFFLER_RADIUS.get();
        double radiusSq = radius * radius;
        for (BlockPos pos : positions) {
            if (!level.isLoaded(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof SoundMufflerBlock) || !state.getValue(SoundMufflerBlock.ENABLED)) {
                continue;
            }
            double dx = pos.getX() + 0.5 - x;
            double dy = pos.getY() + 0.5 - y;
            double dz = pos.getZ() + 0.5 - z;
            if (dx * dx + dy * dy + dz * dz <= radiusSq) {
                return true;
            }
        }
        return false;
    }
}
