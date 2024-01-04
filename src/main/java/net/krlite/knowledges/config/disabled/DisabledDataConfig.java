package net.krlite.knowledges.config.disabled;

import net.krlite.knowledges.Knowledges;
import net.minecraft.util.Identifier;

public class DisabledDataConfig extends SimpleDisabledConfig<Data<?, ?>> {
    public DisabledDataConfig() {
        super("disabled_data");
    }

    public boolean get(Data<?, ?> data) {
        return Knowledges.DATA.identifier(data)
                .map(Identifier::toString)
                .filter(super::get)
                .isPresent();
    }

    public void set(Data<?, ?> data, boolean flag) {
        Knowledges.DATA.identifier(data)
                .map(Identifier::toString)
                .ifPresent(key -> super.set(key, flag));
    }
}