package net.krlite.knowledges.config;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.data.info.block.blockinformation.NoteBlockInformationData;
import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.annotation.Table;
import net.krlite.pierced.config.Pierced;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KnowledgesConfig extends Pierced {
	private static final @Silent File file = Knowledges.CONFIG_PATH.resolve("config.toml").toFile();

	private static final @Silent KnowledgesConfig self = new KnowledgesConfig();

	KnowledgesConfig() {
		super(KnowledgesConfig.class, file);
	}

	public static void loadSelf() {
		self.load();
	}

	public static void saveSelf() {
		self.save();
	}

	public static class WithDefault<V> {
		private final V defaultValue;
		private final Supplier<V> supplier;
		private final Consumer<V> consumer;

		public WithDefault(V defaultValue, Supplier<V> supplier, Consumer<V> consumer) {
			this.defaultValue = defaultValue;
			this.supplier = supplier;
			this.consumer = consumer;
		}

		public V defaultValue() {
			return defaultValue;
		}

		public V get() {
			return supplier.get();
		}

		public void set(V value) {
			consumer.accept(value);
		}
	}

	public static class Range<V extends Comparable<V>> extends WithDefault<V> {
		private final V minValue, maxValue;

		public Range(V defaultValue, V minValue, V maxValue, Supplier<V> supplier, Consumer<V> consumer) {
			super(defaultValue, supplier, consumer);
			if (minValue.compareTo(maxValue) > 0) {
				this.minValue = maxValue;
				this.maxValue = minValue;
			} else {
				this.minValue = minValue;
				this.maxValue = maxValue;
			}
		}

		public V min() {
			return minValue;
		}

		public V max() {
			return maxValue;
		}

		@Override
		public V get() {
			V value = super.get();
			if (value.compareTo(min()) < 0) return min();
			if (value.compareTo(max()) > 0) return max();
			return value;
		}

		@Override
		public void set(V value) {
			if (value.compareTo(min()) < 0) super.set(min());
			if (value.compareTo(max()) > 0) super.set(max());
			super.set(value);
		}
	}

	public static class BooleanToggle extends WithDefault<Boolean> {
		public BooleanToggle(Boolean defaultValue, Supplier<Boolean> supplier, Consumer<Boolean> consumer) {
			super(defaultValue, supplier, consumer);
		}
	}

	public static class EnumSelector<E extends Enum<E>> extends WithDefault<E> {
		private final Class<E> enumClass;

		public EnumSelector(Class<E> enumClass, E defaultValue, Supplier<E> supplier, Consumer<E> consumer) {
			super(defaultValue, supplier, consumer);
			this.enumClass = enumClass;
		}

		public Class<E> enumClass() {
			return enumClass;
		}
	}

	public static class Global {
		public static final Range<Double> MAIN_SCALAR = new Range<>(
				1.0, 0.0, 3.0,
				() -> KnowledgesConfig.globalMainScalar,
				value -> KnowledgesConfig.globalMainScalar = value
		);
		public static final Range<Double> CROSSHAIR_SAFE_AREA_SCALAR = new Range<>(
				1.0, 0.0, 3.0,
				() -> KnowledgesConfig.globalCrosshairSafeAreaScalar,
				value -> KnowledgesConfig.globalCrosshairSafeAreaScalar = value
		);
	}

	public static class Component {
		public static class Crosshair {
			public static final BooleanToggle CURSOR_RING_OUTLINE = new BooleanToggle(
					true,
					() -> KnowledgesConfig.componentCrosshairCusrorRingOutline,
					value -> KnowledgesConfig.componentCrosshairCusrorRingOutline = value
			);
			public static final BooleanToggle TEXTS_RIGHT = new BooleanToggle(
					true,
					() -> KnowledgesConfig.componentCrosshairTextsRight,
					value -> KnowledgesConfig.componentCrosshairTextsRight = value
			);
			public static final BooleanToggle TEXTS_LEFT = new BooleanToggle(
					true,
					() -> KnowledgesConfig.componentCrosshairTextsLeft,
					value -> KnowledgesConfig.componentCrosshairTextsLeft = value
			);
			public static final BooleanToggle SUBTITLES = new BooleanToggle(
					true,
					() -> KnowledgesConfig.componentCrosshairSubtitles,
					value -> KnowledgesConfig.componentCrosshairSubtitles = value
			);
		}

		public static class InfoBlock {
			public static final BooleanToggle SHOW_POWERED_STATUS = new BooleanToggle(
					true,
					() -> KnowledgesConfig.componentInfoBlockShowPoweredStatus,
					value -> KnowledgesConfig.componentInfoBlockShowPoweredStatus = value
			);
		}

		public static class InfoFluid {
			public static final BooleanToggle IGNORES_WATER = new BooleanToggle(
					false,
					() -> KnowledgesConfig.componentInfoFluidIgnoresWater,
					value -> KnowledgesConfig.componentInfoFluidIgnoresWater = value
			);
			public static final BooleanToggle IGNORES_LAVA = new BooleanToggle(
					false,
					() -> KnowledgesConfig.componentInfoFluidIgnoresLava,
					value -> KnowledgesConfig.componentInfoFluidIgnoresLava = value
			);
			public static final BooleanToggle IGNORES_OTHER_FLUIDS = new BooleanToggle(
					false,
					() -> KnowledgesConfig.componentInfoFluidIgnoresOtherFluids,
					value -> KnowledgesConfig.componentInfoFluidIgnoresOtherFluids = value
			);
		}
	}

	public static class Data {
		public static class NoteBlockInformation {
			public static final EnumSelector<NoteBlockInformationData.NoteModifier> NOTE_MODIFIER = new EnumSelector<>(
					NoteBlockInformationData.NoteModifier.class,
					NoteBlockInformationData.NoteModifier.SHARPS,
					() -> KnowledgesConfig.dataNoteBlockInformationNoteModifier,
					value -> KnowledgesConfig.dataNoteBlockInformationNoteModifier = value
			);
			public static final EnumSelector<NoteBlockInformationData.MusicalAlphabet> MUSICAL_ALPHABET = new EnumSelector<>(
					NoteBlockInformationData.MusicalAlphabet.class,
					NoteBlockInformationData.MusicalAlphabet.ENGLISH,
					() -> KnowledgesConfig.dataNoteBlockInformationMusicalAlphabet,
					value -> KnowledgesConfig.dataNoteBlockInformationMusicalAlphabet = value
			);
		}
	}

	static double globalMainScalar = Global.MAIN_SCALAR.defaultValue();
	static double globalCrosshairSafeAreaScalar = Global.CROSSHAIR_SAFE_AREA_SCALAR.defaultValue();

	@Table("component.crosshair") static boolean componentCrosshairCusrorRingOutline = Component.Crosshair.CURSOR_RING_OUTLINE.defaultValue();
	@Table("component.crosshair") static boolean componentCrosshairTextsRight = Component.Crosshair.TEXTS_RIGHT.defaultValue();
	@Table("component.crosshair") static boolean componentCrosshairTextsLeft = Component.Crosshair.TEXTS_LEFT.defaultValue();
	@Table("component.crosshair") static boolean componentCrosshairSubtitles = Component.Crosshair.SUBTITLES.defaultValue();

	@Table("component.info.block") static boolean componentInfoBlockShowPoweredStatus = Component.InfoBlock.SHOW_POWERED_STATUS.defaultValue();

	@Table("component.info.fluid") static boolean componentInfoFluidIgnoresWater = Component.InfoFluid.IGNORES_WATER.defaultValue();
	@Table("component.info.fluid") static boolean componentInfoFluidIgnoresLava = Component.InfoFluid.IGNORES_LAVA.defaultValue();
	@Table("component.info.fluid") static boolean componentInfoFluidIgnoresOtherFluids = Component.InfoFluid.IGNORES_OTHER_FLUIDS.defaultValue();

	@Table("data.info.block.block_information.note_block") static NoteBlockInformationData.NoteModifier dataNoteBlockInformationNoteModifier = Data.NoteBlockInformation.NOTE_MODIFIER.defaultValue();
	@Table("data.info.block.block_information.note_block") static NoteBlockInformationData.MusicalAlphabet dataNoteBlockInformationMusicalAlphabet = Data.NoteBlockInformation.MUSICAL_ALPHABET.defaultValue();
}
