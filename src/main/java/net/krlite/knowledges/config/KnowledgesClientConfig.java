package net.krlite.knowledges.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.knowledges.impl.component.CrosshairComponent;
import net.krlite.knowledges.impl.data.info.block.blockinformation.NoteBlockInformationData;

import java.util.Map;
import java.util.TreeMap;

@Config(name = "knowledges")
public class KnowledgesClientConfig extends PartitioningSerializer.GlobalData {
	public General general = new General();

	public Components components = new Components();

	public Data data = new Data();

	@Config(name = "general")
	public static class General implements ConfigData {
		public double mainScalar = 1.0;
		public double crosshairSafeAreaScalar = 1.0;
		public double crosshairPrimaryOpacity = 0.72;
		public double crosshairSecondaryOpacity = 0.5;
		public boolean visibleInDebugHud = false;

		public int mainScalarAsInt() {
			return (int) (mainScalar * 1000);
		}

		public void mainScalarAsInt(int scalar) {
			mainScalar = Theory.clamp(scalar / 1000.0, 0.5, 2.0);
		}

		public int crosshairSafeAreaScalarAsInt() {
			return (int) (crosshairSafeAreaScalar * 1000);
		}

		public void crosshairSafeAreaScalarAsInt(int scalar) {
			crosshairSafeAreaScalar = Theory.clamp(scalar / 1000.0, 0.5, 2.0);
		}

		public int crosshairPrimaryOpacityAsInt() {
			return (int) (crosshairPrimaryOpacity * 1000);
		}

		public void crosshairPrimaryOpacityAsInt(int opacity) {
			crosshairPrimaryOpacity = Theory.clamp(opacity / 1000.0, 0.1, 1.0);
		}

		public int crosshairSecondaryOpacityAsInt() {
			return (int) (crosshairSecondaryOpacity * 1000);
		}

		public void crosshairSecondaryOpacityAsInt(int opacity) {
			crosshairSecondaryOpacity = Theory.clamp(opacity / 1000.0, 0.1, 1.0);
		}
	}

	@Config(name = "components")
	public static class Components implements ConfigData {
		public boolean autoTidiesUp = false;

		public Crosshair crosshair = new Crosshair();
		public InfoBlock infoBlock = new InfoBlock();
		public InfoEntity infoEntity = new InfoEntity();
		public InfoFluid infoFluid = new InfoFluid();

		public static class Crosshair {
			public CrosshairComponent.RingShape ringShape = CrosshairComponent.RingShape.OVAL;
			public boolean cursorRingOutlineEnabled = false;
			public boolean textsRightEnabled = true;
			public boolean textsLeftEnabled = true;
			public boolean subtitlesEnabled = true;
		}

		public static class InfoBlock {
			public boolean showsBlockPoweredStatus = true;
		}

		public static class InfoEntity {
			public boolean showsNumericHealth = false;
		}

		public static class InfoFluid {
			public boolean ignoresWater = false;
			public boolean ignoresLava = false;
			public boolean ignoresOtherFluids = false;
		}

		public Map<String, Boolean> enabled = new TreeMap<>();
	}

	@Config(name = "data")
	public static class Data implements ConfigData {
		public boolean autoTidiesUp = false;

		public NoteBlockInformation noteBlockInformation = new NoteBlockInformation();

		public static class NoteBlockInformation {
			public NoteBlockInformationData.NoteModifier noteModifier = NoteBlockInformationData.NoteModifier.SHARPS;
			public NoteBlockInformationData.MusicalAlphabet musicalAlphabet = NoteBlockInformationData.MusicalAlphabet.ENGLISH;
		}

		public Map<String, Boolean> enabled = new TreeMap<>();
	}
}
