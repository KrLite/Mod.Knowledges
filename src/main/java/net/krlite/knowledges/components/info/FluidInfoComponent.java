package net.krlite.knowledges.components.info;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.components.InfoComponent;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.krlite.knowledges.core.Target;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class FluidInfoComponent extends InfoComponent<FluidInfoComponent.FluidInfoTarget> {
    public enum FluidInfoTarget implements Target {
        TEST;
    }

    @Override
    public Class<FluidInfoTarget> targets() {
        return FluidInfoTarget.class;
    }

    @Override
    public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
        super.render(context, client, player, world);

        Info.crosshairFluidState().ifPresent(fluidState -> {
            BlockState blockState = fluidState.getBlockState();
            MutableText fluidName = blockState.getBlock().getName();

            String
                    namespace = Registries.FLUID.getId(fluidState.getFluid()).getNamespace(),
                    path = Registries.FLUID.getId(fluidState.getFluid()).getPath();

            Animations.Ring.ovalColor(Palette.Minecraft.WHITE);

            Animations.Ring.ringRadians(Math.PI * 2);
            Animations.Ring.ringColor(Palette.Minecraft.WHITE);

            // Titles
            titles:
            {
                Animations.Texts.titleRight(fluidName);
                Animations.Texts.titleLeft(Util.getModName(namespace));
            }

            // Right Above
            if (client.options.advancedItemTooltips) subtitleRightAbove:{
                Animations.Texts.subtitleRightAbove(Text.literal(path));
            }
            else {
                Animations.Texts.subtitleRightAbove(Text.empty());
            }

            // Right Below
            subtitleRightBelow:
            {
                Animations.Texts.subtitleRightBelow(Text.empty());
            }

            // Left Above
            if (client.options.advancedItemTooltips) subtitleLeftAbove:{
                Animations.Texts.subtitleLeftAbove(Text.literal(namespace));
            }
            else {
                Animations.Texts.subtitleLeftAbove(Text.empty());
            }

            // Left Below
            subtitleLeftBelow:
            {
                int level = fluidState.getLevel();
                Animations.Texts.subtitleLeftBelow(Text.translatable(localizationKey("level"), level));
            }
        });
    }

    @Override
    public @NotNull String partialPath() {
        return "fluid";
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }

    @Override
    public boolean requestsConfigPage() {
        return true;
    }

    @Override
    public Function<ConfigEntryBuilder, List<AbstractFieldBuilder<?, ?, ?>>> buildConfigEntries() {
        return entryBuilder -> List.of(
                entryBuilder.startBooleanToggle(
                                localize("config", "ignores_water"),
                                Knowledges.CONFIG.infoFluidIgnoresWater()
                        )
                        .setDefaultValue(KnowledgesConfig.Default.INFO_FLUID_IGNORES_WATER)
                        .setSaveConsumer(Knowledges.CONFIG::infoFluidIgnoresWater)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.ENABLED_DISABLED_SUPPLIER),

                entryBuilder.startBooleanToggle(
                                localize("config", "ignores_lava"),
                                Knowledges.CONFIG.infoFluidIgnoresLava()
                        )
                        .setDefaultValue(KnowledgesConfig.Default.INFO_FLUID_IGNORES_LAVA)
                        .setSaveConsumer(Knowledges.CONFIG::infoFluidIgnoresLava)
                        .setYesNoTextSupplier(KnowledgesConfigScreen.ENABLED_DISABLED_SUPPLIER)
        );
    }
}
