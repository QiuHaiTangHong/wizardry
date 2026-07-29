package top.begonia.wizardry.client.layout.util;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.handbook.HandbookData;
import top.begonia.wizardry.client.data.definition.handbook.part.SectionData;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;
import top.begonia.wizardry.client.layout.container.handbook.SectionElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PageTurner {
    private Context context;
    private int currentPage;
    @NotNull
    private final HandbookData handbookData;
    private final List<SectionElement> displaySection = new ArrayList<>();
    private final Map<String, SectionElement> sectionElementMap = new LinkedHashMap<>();

    public PageTurner() {
        this.handbookData = WizardryClientDataManager.getInstance().getData(Identifier.fromNamespaceAndPath(Wizardry.MODID, "handbook"), HandbookData.class).orElse(HandbookData.DEFAULT);
    }

    public List<SectionElement> getDisplaySection() {
        return this.displaySection;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void prev() {

    }

    public void next() {

    }

    private @NonNull Map<String, SectionData> getStringSectionDataMap() {
        Map<String, SectionData> noEmptySectionDataList = new LinkedHashMap<>();
        Map<String, Map<String, String>> catalogueEntry = new LinkedHashMap<>();
        this.handbookData.sections().forEach((sectionName, sectionData) -> {
            if (sectionData.text().isPresent()) {
                noEmptySectionDataList.put(sectionName, sectionData);
            }
            if (sectionData.subSections().isPresent()) {
                Map<String, SectionData> subSectionData = sectionData.subSections().get();
                subSectionData.forEach((key, value) -> {
                    noEmptySectionDataList.put(key, value);
                    if (value.includeInContents().isPresent()) {
                        catalogueEntry
                                .computeIfAbsent(value.includeInContents().get(), _ -> new LinkedHashMap<>())
                                .put(key, value.title().orElse(""));
                    }
                });
            }
            if (sectionData.contents().isPresent()) {
                noEmptySectionDataList.put(sectionName, sectionData);
            }
            if (sectionData.includeInContents().isPresent()) {
                catalogueEntry
                        .computeIfAbsent(sectionData.includeInContents().get(), _ -> new LinkedHashMap<>())
                        .put(sectionName, sectionData.title().orElse(""));
            }
        });
        this.context.setCatalogueEntry(catalogueEntry);
        return noEmptySectionDataList;
    }

    public void format(Context context) {
        this.context = context;
        this.context.setColours(handbookData.colours());
        this.context.setImages(handbookData.images());
        this.context.setRecipes(handbookData.recipes());
        Map<String, SectionData> noEmptySectionDataList = getStringSectionDataMap();
        for (Map.Entry<String, SectionData> entry : noEmptySectionDataList.entrySet()) {
            String key = entry.getKey();
            SectionData sectionData = entry.getValue();
            SectionElement sectionElement = new SectionElement(sectionData);
            sectionElement.format(this.context);
            this.sectionElementMap.put(key, sectionElement);
        }
        Wizardry.LOGGER.info("aaaa");
    }
}
