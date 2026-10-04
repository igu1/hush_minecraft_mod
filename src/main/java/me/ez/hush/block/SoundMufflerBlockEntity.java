package me.ez.hush.block;

import me.ez.hush.Init;
import me.ez.hush.muffler.MufflerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Marker block entity. Its only job is to keep the client-side
 * {@link MufflerRegistry} in sync with the loaded muffler blocks so the sound
 * hook never has to scan the world.
 */
public class SoundMufflerBlockEntity extends BlockEntity {

    public SoundMufflerBlockEntity(BlockPos pos, BlockState state) {
        super(Init.SOUND_MUFFLER_BE.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Level level = getLevel();
        if (level != null && level.isClientSide()) {
            MufflerRegistry.add(level, getBlockPos());
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unregister();
    }

    @Override
    public void setRemoved() {
        unregister();
        super.setRemoved();
    }

    private void unregister() {
        Level level = getLevel();
        if (level != null && level.isClientSide()) {
            MufflerRegistry.remove(level, getBlockPos());
        }
    }
}
