package top.begonia.wizardry.core.api.layout.util;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.handbook.HandbookData;
import top.begonia.wizardry.client.data.definition.handbook.part.SectionData;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;
import top.begonia.wizardry.core.api.layout.container.IContainerElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PageTurner {
    private Context context;
    private int currentPage = -1;
    @NotNull
    private final HandbookData handbookData;
    private final List<IContainerElement> displayPageElements = new ArrayList<>();
    private final Map<String, SectionFormatConverter> sectionMap = new LinkedHashMap<>();
    private final List<IContainerElement> pageElements = new ArrayList<>();

    public PageTurner() {
        this.handbookData = WizardryClientDataManager.getInstance().getData(Identifier.fromNamespaceAndPath(Wizardry.MODID, "handbook"), HandbookData.class).orElse(HandbookData.DEFAULT);
    }

    public List<IContainerElement> getDisplayPageElements() {
        return this.displayPageElements;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void prev() {
        this.currentPage -= this.safeCapture(this.currentPage, -2, this.pageElements, this.displayPageElements);
    }

    public void next() {
        this.currentPage += this.safeCapture(this.currentPage, 2, this.pageElements, this.displayPageElements);
    }

    public boolean isEnd() {
        return this.currentPage == this.pageElements.size() - 1;
    }

    private int safeCapture(int currentPage, int captureSize, @NonNull List<IContainerElement> pageElements, @NonNull List<IContainerElement> displayPageElements) {
        displayPageElements.clear();
        if (pageElements.isEmpty() || captureSize == 0) {
            return 0;
        }
        int size = pageElements.size();
        int absSize = Math.abs(captureSize);
        int startIndex = captureSize >= 0 ? currentPage + 1 : currentPage + captureSize - 1;
        startIndex = Math.clamp(startIndex, 0, size);
        int endIndex = Math.min(startIndex + absSize, size);
        displayPageElements.addAll(pageElements.subList(startIndex, endIndex));
        return displayPageElements.size();
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
            SectionFormatConverter sectionElement = new SectionFormatConverter(sectionData);
            sectionElement.format(this.context);
            this.sectionMap.put(key, sectionElement);
        }
        this.pageElements.add(IContainerElement.EMPTY_ELEMENT);
        for (Map.Entry<String, SectionFormatConverter> entry : this.sectionMap.entrySet()) {
            this.pageElements.addAll(entry.getValue().getPageElements());
        }
        if (this.pageElements.size() % 2 == 0) {
            this.pageElements.add(IContainerElement.EMPTY_ELEMENT);
        } else {
            this.pageElements.add(IContainerElement.EMPTY_ELEMENT);
            this.pageElements.add(IContainerElement.EMPTY_ELEMENT);
        }
        this.currentPage += this.safeCapture(this.currentPage, 2, this.pageElements, this.displayPageElements);
    }
}
