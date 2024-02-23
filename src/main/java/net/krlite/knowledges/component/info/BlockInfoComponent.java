package net.krlite.knowledges.component.info;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.krlite.equator.visual.color.Palette;
import net.krlite.equator.visual.color.base.ColorStandard;
import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.component.AbstractInfoComponent;
import net.krlite.knowledges.config.KnowledgesConfig;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.krlite.knowledges.core.data.DataInvoker;
import net.krlite.knowledges.core.data.DataProtocol;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BlockInfoComponent extends AbstractInfoComponent {
	public interface MineableToolInvoker extends DataInvoker<BlockInfoComponent, MineableToolInvoker.Protocol> {
		MineableToolInvoker INSTANCE = new MineableToolInvoker() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> blockState -> protocols.stream()
						.map(protocol -> protocol.mineableTool(blockState))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty());
			}
		};

		interface Protocol extends DataProtocol<BlockInfoComponent> {
			Optional<MutableText> mineableTool(BlockState blockState);

			@Override
			default DataInvoker<BlockInfoComponent, ?> dataInvoker() {
				return MineableToolInvoker.INSTANCE;
			}
		}

		@Override
		default @NotNull Class<BlockInfoComponent> targetKnowledgeClass() {
			return BlockInfoComponent.class;
		}
	}

	public interface BlockInformationInvoker extends DataInvoker<BlockInfoComponent, BlockInformationInvoker.Protocol> {
		BlockInformationInvoker INSTANCE = new BlockInformationInvoker() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> (blockState, player) -> protocols.stream()
						.map(protocol -> protocol.blockInformation(blockState, player))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty());
			}
		};

		interface Protocol extends DataProtocol<BlockInfoComponent> {
			Optional<MutableText> blockInformation(BlockState blockState, PlayerEntity player);

			@Override
			default DataInvoker<BlockInfoComponent, ?> dataInvoker() {
				return BlockInformationInvoker.INSTANCE;
			}
		}

		@Override
		default @NotNull Class<BlockInfoComponent> targetKnowledgeClass() {
			return BlockInfoComponent.class;
		}
	}

	@Override
	public void render(@NotNull DrawContext context, @NotNull MinecraftClient client, @NotNull PlayerEntity player, @NotNull ClientWorld world) {
		Info.crosshairBlockState().ifPresent(blockState -> {
			MutableText blockName = blockState.getBlock().getName();

			float hardness = Info.crosshairBlockPos()
					.map(blockPos -> blockState.getHardness(world, blockPos))
					.orElse(0F);
			boolean harvestable = hardness >= 0 && player.canHarvest(blockState);

			String
					namespace = Util.namespace(blockState.getBlock().asItem().getDefaultStack()),
					path = Registries.BLOCK.getId(blockState.getBlock()).getPath();

			boolean resetBlockBreakingProgress = !Animation.Contextual.cancelledBlockBreaking() && Animation.Contextual.rawBlockBreakingProgress() < Animation.Ring.blockBreakingProgress();
			Animation.Ring.blockBreakingProgress(Animation.Contextual.rawBlockBreakingProgress(), resetBlockBreakingProgress);
			Animation.Ring.ringArc(Math.PI * 2 * Animation.Contextual.rawBlockBreakingProgress(), resetBlockBreakingProgress);

			Animation.Ring.ovalColor(harvestable ? Palette.Minecraft.WHITE : Palette.Minecraft.RED);
			Animation.Ring.ringColor(Palette.Minecraft.YELLOW.mix(Palette.Minecraft.GREEN, Animation.Ring.blockBreakingProgress(), ColorStandard.MixMode.PIGMENT));



			// Titles
			titles: {
				Animation.Text.titleRight(blockName);
				Animation.Text.titleLeft(Util.modName(namespace));
			}

			// Right Above
			if (client.options.advancedItemTooltips) subtitleRightAbove: {
				Animation.Text.subtitleRightAbove(net.minecraft.text.Text.literal(path));
			} else {
				Animation.Text.subtitleRightAbove(net.minecraft.text.Text.empty());
			}

			// Right Below
			subtitleRightBelow: {
				MutableText tool, miningLevel = null;
				boolean foundSemanticMiningLevel = false;

				tool: {
					Optional<MutableText> data = MineableToolInvoker.INSTANCE.invoker().mineableTool(blockState);

					if (hardness < 0) {
						// Unbreakable
						tool = localize("tool", "unbreakable");
					} else if (data.isPresent()) {
						tool = data.get();
					} else {
						Animation.Text.subtitleRightBelow(net.minecraft.text.Text.empty());
						break subtitleRightBelow;
					}
				}

				miningLevel: {
					int requiredLevel = MiningLevelManager.getRequiredMiningLevel(blockState);
					if (requiredLevel <= 0) break miningLevel;

					String localizationKey = localizationKey("mining_level");
					String localizationKeyWithLevel = localizationKey("mining_level", String.valueOf(requiredLevel));
					MutableText localizationWithLevel = net.minecraft.text.Text.translatable(localizationKeyWithLevel);

					foundSemanticMiningLevel = !localizationWithLevel.getString().equals(localizationKeyWithLevel);
					miningLevel = foundSemanticMiningLevel ? localizationWithLevel : net.minecraft.text.Text.translatable(localizationKey, requiredLevel);
				}

				if (miningLevel == null) {
					Animation.Text.subtitleRightBelow(net.minecraft.text.Text.translatable(localizationKey("tool"), tool));
				} else {
					Animation.Text.subtitleRightBelow(net.minecraft.text.Text.translatable(
							foundSemanticMiningLevel ? localizationKey("tool_and_mining_level_semantic") : localizationKey("tool_and_mining_level"),
							tool, miningLevel
					));
				}
			}

			// Left Above
			if (client.options.advancedItemTooltips) subtitleLeftAbove: {
				Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.literal(namespace));
			} else {
				Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.empty());
			}

			// Left Below
			subtitleLeftBelow: {
				boolean powered = Knowledges.CONFIG.components.infoBlock.showBlockPoweredStatus
						&& Info.crosshairBlockPos().map(world::isReceivingRedstonePower).orElse(false);

				Animation.Text.subtitleLeftBelow(
						BlockInformationInvoker.INSTANCE.invoker().blockInformation(blockState, player)
								.orElse(powered ? localize("powered") : net.minecraft.text.Text.empty())
				);
			}
		});
	}

	@Override
	public @NotNull String partialPath() {
		return "block";
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
								localize("config", "block_powered_status"),
								Knowledges.CONFIG.components.infoBlock.showBlockPoweredStatus
						)
						.setDefaultValue(true)
						.setTooltip(localize("config", "show_powered_status", "tooltip"))
						.setSaveConsumer(value -> Knowledges.CONFIG.components.infoBlock.showBlockPoweredStatus = value)
						.setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN)
		);
	}
}
