package me.ez.hush;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Common configuration for Hush. */
public class Config {

    /** Every valid {@link SoundSource} name, used to validate the category list. */
    private static final Set<String> SOURCE_NAMES = Arrays.stream(SoundSource.values())
            .map(SoundSource::getName)
            .collect(Collectors.toUnmodifiableSet());

    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ENABLED;

    public static final ModConfigSpec.BooleanValue MUFFLER_ENABLED;
    public static final ModConfigSpec.IntValue MUFFLER_RADIUS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MUTED_CATEGORIES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Master switch for Hush").translation("hush.configuration.general").push("general");
        ENABLED = builder.comment("Enable Hush").translation("hush.configuration.general.enabled")
                .define("enabled", true);
        builder.pop();

        builder.comment("Sound Muffler").translation("hush.configuration.muffler").push("muffler");
        MUFFLER_ENABLED = builder.comment("Enable Sound Muffler silence checks")
                .translation("hush.configuration.muffler.enabled").define("enabled", true);
        MUFFLER_RADIUS = builder.comment("Radius (in blocks) in which a Sound Muffler silences sounds")
                .translation("hush.configuration.muffler.radius").defineInRange("radius", 12, 1, 64);
        MUTED_CATEGORIES = builder.comment("Sound categories a muffler silences. Valid names: " + SOURCE_NAMES)
                .translation("hush.configuration.muffler.muted_categories")
                .defineList("muted_categories",
                        List.of("block", "hostile", "neutral", "ambient", "player", "weather"),
                        o -> o instanceof String s && SOURCE_NAMES.contains(s.toLowerCase(Locale.ROOT)));
        builder.pop();

        SPEC = builder.build();
    }

    private Config() {}

    /** @return true when a muffler should silence sounds in the given category. */
    public static boolean isMuted(SoundSource source) {
        if (!ENABLED.get() || !MUFFLER_ENABLED.get()) {
            return false;
        }
        String name = source.getName();
        for (String muted : MUTED_CATEGORIES.get()) {
            if (muted.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
