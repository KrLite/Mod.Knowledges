package net.krlite.knowledges;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.config.disabled.DisabledDataConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataManager extends Knowledges.Manager<Data<?, ?>> {
    DataManager() {
        super(new DisabledDataConfig());
    }

    @Override
    protected String localizationPrefix() {
        return "knowledge_data";
    }

    public Map<Knowledge, List<Data<?, ?>>> asClassifiedMap() {
        return Map.copyOf(asList().stream()
                .filter(data -> data.targetKnowledge().isPresent())
                .collect(Collectors.groupingBy(data -> data.targetKnowledge().get())));
    }
}