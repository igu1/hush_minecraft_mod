package me.ez.hush;

import me.ez.hush.block.SoundMufflerBlock;
import me.ez.hush.block.SoundMufflerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** All Hush registry entries. */
public final class Init {

    private Init() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Main.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Main.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Main.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MOD_ID);

    public static final DeferredBlock<SoundMufflerBlock> SOUND_MUFFLER = BLOCKS.registerBlock(
            "sound_muffler", SoundMufflerBlock::new,
            props -> props.mapColor(MapColor.COLOR_GRAY).strength(1.5f, 6.0f).sound(SoundType.WOOL));

    public static final DeferredItem<net.minecraft.world.item.BlockItem> SOUND_MUFFLER_ITEM =
            ITEMS.registerSimpleBlockItem("sound_muffler", SOUND_MUFFLER);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SoundMufflerBlockEntity>> SOUND_MUFFLER_BE =
            BLOCK_ENTITIES.register("sound_muffler",
                    () -> new BlockEntityType<>(SoundMufflerBlockEntity::new, SOUND_MUFFLER.get()));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register("hush",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hush"))
                    .icon(() -> new ItemStack(SOUND_MUFFLER_ITEM.get()))
                    .displayItems(Init::addTabContents)
                    .build());

    private static void addTabContents(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(SOUND_MUFFLER_ITEM.get());
    }
}
