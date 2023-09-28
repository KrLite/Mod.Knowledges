package net.krlite.knowledges.config;

import net.fabricmc.loader.api.FabricLoader;
import net.krlite.equator.math.algebra.Theory;
import net.krlite.equator.visual.animation.interpolated.InterpolatedDouble;
import net.krlite.knowledges.Knowledges;
import net.krlite.pierced.annotation.Silent;
import net.krlite.pierced.config.Pierced;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class KnowledgesConfig extends Pierced {
	private static final @Silent File file = FabricLoader.getInstance().getConfigDir().resolve(Knowledges.ID + ".toml").toFile();

	public KnowledgesConfig() {
		super(KnowledgesConfig.class, file);
		load();
	}

	private double crosshairSafeAreaSizeScalar = 1;

	public double crosshairSafeAreaSizeScalar() {
		return crosshairSafeAreaSizeScalar;
	}

	public void crosshairSafeAreaSizeScalar(double scalar) {
		crosshairSafeAreaSizeScalar = Theory.clamp(scalar, 0, 2);
		save();
	}

	private double scalar = 1;

	public double scalar() {
		return scalar;
	}

	public void scalar(double scalar) {
		this.scalar = Theory.clamp(scalar, 0, 2);
		save();
	}

	private boolean	infoTitleEnabled = true;

	public boolean infoTitleEnabled() {
		return infoTitleEnabled;
	}

	public void infoTitleEnabled(boolean flag) {
		infoTitleEnabled = flag;
	}

	private boolean	infoSubtitleEnabled = true;

	public boolean infoSubtitleEnabled() {
		return infoSubtitleEnabled;
	}

	public void infoSubtitleEnabled(boolean flag) {
		infoSubtitleEnabled = flag;
	}
}
