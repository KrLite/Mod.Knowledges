package net.krlite.knowledges.impl.component.info;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.visual.color.Palette;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.proxy.RenderProxy;
import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.krlite.knowledges.api.representable.Representable;
import net.krlite.knowledges.impl.component.AbstractInfoComponent;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.krlite.knowledges.api.data.DataInvoker;
import net.krlite.knowledges.api.data.DataProtocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EntityInfoComponent extends AbstractInfoComponent {
	public interface EntityInformationInvoker extends DataInvoker<EntityInfoComponent, EntityInfoComponent.EntityInformationInvoker.Protocol> {
		EntityInformationInvoker INSTANCE = new EntityInformationInvoker() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> (representable) -> protocols.stream()
						.map(protocol -> protocol.entityInformation(representable))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty());
			}
		};

		interface Protocol extends DataProtocol<EntityInfoComponent> {
			Optional<MutableText> entityInformation(EntityRepresentable representable);

			@Override
			default DataInvoker<EntityInfoComponent, ?> dataInvoker() {
				return EntityInformationInvoker.INSTANCE;
			}
		}

		@Override
		default @NotNull Class<EntityInfoComponent> targetKnowledgeClass() {
			return EntityInfoComponent.class;
		}
	}

	public interface EntityDescriptionInvoker extends DataInvoker<EntityInfoComponent, EntityInfoComponent.EntityDescriptionInvoker.Protocol> {
		EntityDescriptionInvoker INSTANCE = new EntityDescriptionInvoker() {
			@Override
			public @NotNull Function<List<Protocol>, Protocol> protocolStream() {
				return protocols -> (representable) -> protocols.stream()
						.map(protocol -> protocol.entityDescription(representable))
						.filter(Optional::isPresent)
						.findFirst()
						.orElse(Optional.empty());
			}
		};

		interface Protocol extends DataProtocol<EntityInfoComponent> {
			Optional<MutableText> entityDescription(EntityRepresentable representable);

			@Override
			default DataInvoker<EntityInfoComponent, ?> dataInvoker() {
				return EntityDescriptionInvoker.INSTANCE;
			}
		}

		@Override
		default @NotNull Class<EntityInfoComponent> targetKnowledgeClass() {
			return EntityInfoComponent.class;
		}
	}

	@Override
	public void render(RenderProxy renderProxy, @NotNull Representable<?> representable) {
		if (representable.type() == HitResult.Type.ENTITY && representable instanceof EntityRepresentable entityRepresentable) {
			Entity entity = entityRepresentable.entity();
			PlayerEntity player = entityRepresentable.player();

			MutableText entityName = entity.getDisplayName().copy();

			String
					namespace = Registries.ENTITY_TYPE.getId(entity.getType()).getNamespace(),
					path = Registries.ENTITY_TYPE.getId(entity.getType()).getPath();

			// Titles
			titles: {
				Animation.Text.titleRight(entityName);
				Animation.Text.titleLeft(KnowledgeProxy.getModName(Registries.ENTITY_TYPE.getId(entity.getType()).getNamespace()));
			}

			switch (entity.getType().getSpawnGroup()) {
				case MONSTER -> {
					Animation.Ring.ringColor(Palette.Minecraft.RED);
					Animation.Ring.ovalColor(Palette.Minecraft.RED);
				}
				case WATER_CREATURE, UNDERGROUND_WATER_CREATURE -> {
					Animation.Ring.ringColor(Palette.Minecraft.AQUA);
					Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
				case WATER_AMBIENT -> {
					Animation.Ring.ringColor(Palette.Minecraft.BLUE);
					Animation.Ring.ovalColor(Palette.Minecraft.BLUE);
				}
				case MISC -> {
					Animation.Ring.ringColor(Palette.Minecraft.WHITE);
					Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
				case AMBIENT -> {
					Animation.Ring.ringColor(Palette.Minecraft.YELLOW);
					Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
				default -> {
					Animation.Ring.ringColor(Palette.Minecraft.GREEN);
					Animation.Ring.ovalColor(Palette.Minecraft.WHITE);
				}
			}

			if (entity.isInvulnerable()) {
				Animation.Ring.ovalColor(Palette.Minecraft.LIGHT_PURPLE);
			}

			if (!entity.isInvulnerable() && entity instanceof LivingEntity livingEntity) {
				float health = livingEntity.getHealth(), maxHealth = livingEntity.getMaxHealth();
				boolean notDamaged = health == maxHealth;
				double arc = Math.PI * 2 * (health / maxHealth);

				if (Animation.Contextual.entityWasNotDamaged() && !notDamaged) {
					Animation.Ring.ringArc(Math.min(arc + Math.PI / 3, Math.PI * 2 - Theory.EPSILON), true);
				}

				Animation.Ring.ringArc(arc);
				Animation.Text.numericHealth(health);
				Animation.Contextual.entityWasNotDamaged(notDamaged);
			}

			// Right Above
			if (MinecraftClient.getInstance().options.advancedItemTooltips) subtitleRightAbove: {
				Animation.Text.subtitleRightAbove(net.minecraft.text.Text.literal(path));
			} else {
				Animation.Text.subtitleRightAbove(net.minecraft.text.Text.empty());
			}

			// Right Below
			subtitleRightBelow: {
				Animation.Text.subtitleRightBelow(
						EntityInformationInvoker.INSTANCE.invoker().entityInformation(entityRepresentable).orElse(net.minecraft.text.Text.empty())
				);
			}

			// Left Above
			if (MinecraftClient.getInstance().options.advancedItemTooltips) subtitleLeftAbove: {
				Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.literal(namespace));
			} else {
				Animation.Text.subtitleLeftAbove(net.minecraft.text.Text.empty());
			}

			// Left Below
			subtitleLeftBelow: {
				Animation.Text.subtitleLeftBelow(
						EntityDescriptionInvoker.INSTANCE.invoker().entityDescription(entityRepresentable).orElse(net.minecraft.text.Text.empty())
				);
			}
		} else {
			Animation.Text.clearNumericHealth();
			Animation.Contextual.entityWasNotDamaged(true);
		}
	}

	@Override
	public @NotNull String partialPath() {
		return "entity";
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
								localize("config", "numeric_health"),
								KnowledgesClient.CONFIG.get().components.infoEntity.showsNumericHealth
						)
						.setDefaultValue(KnowledgesClient.DEFAULT_CONFIG.components.infoEntity.showsNumericHealth)
						.setTooltip(localize("config", "numeric_health", "tooltip"))
						.setSaveConsumer(value -> KnowledgesClient.CONFIG.get().components.infoEntity.showsNumericHealth = value)
						.setYesNoTextSupplier(KnowledgesConfigScreen.BooleanSupplier.DISPLAYED_HIDDEN)
		);
	}
}
