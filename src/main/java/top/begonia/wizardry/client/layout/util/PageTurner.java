package top.begonia.wizardry.client.layout.util;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.WizardryDataManager;
import top.begonia.wizardry.client.data.definition.handbook.HandbookData;
import top.begonia.wizardry.client.data.definition.handbook.part.SectionData;
import top.begonia.wizardry.client.layout.container.handbook.SectionElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PageTurner {
    private Context context;
    private int currentPage;
    private int sectionCacheEndIndex;
    private final HandbookData handbookData;
    private final List<SectionElement> displaySection = new ArrayList<>();
    private final List<SectionElement> sectionElementsCache = new ArrayList<>();
    private final List<String> sectionList = new ArrayList<>();
    private final Map<String, SectionData> allSectionData = new LinkedHashMap<>();

    public PageTurner() {
        Identifier path = Identifier.fromNamespaceAndPath(Wizardry.MODID, "handbook");
        handbookData = WizardryDataManager.getData(path, HandbookData.class).orElse(null);
        this.sectionCacheEndIndex = 0;
    }

    public List<SectionElement> getDisplaySection() {
        return this.displaySection;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void prev() {
        SectionElement currentSectionElement = this.displaySection.getFirst();
        SectionElement.PageState pageState = currentSectionElement.prev();
        if (pageState == SectionElement.PageState.HAS_TWO_PAGE) {
            if (this.displaySection.size() == 2) {
                this.displaySection.removeLast();
                SectionElement prevSectionInCache = this.sectionElementsCache.getFirst();
                if (currentSectionElement == prevSectionInCache) {
                    prevSectionElement();
                }
            }
        } else if (pageState == SectionElement.PageState.ONLY_LEFT_PAGE) {
            currentSectionElement.fillViewBackward(currentSectionElement.getCurrentPageIndex(), 2);
            SectionElement prevSectionInCache = this.sectionElementsCache.getFirst();
            if (this.displaySection.size() == 2) {
                this.displaySection.removeLast();
                prevSectionInCache.fillViewForward(prevSectionInCache.getPageCount() - 1, 2);
                this.displaySection.addFirst(prevSectionInCache);
                prevSectionElement();
            } else {
                SectionElement prevSectionElement = this.sectionElementsCache.getFirst();
                prevSectionInCache.fillViewForward(prevSectionInCache.getPageCount() - 1, 2);
                this.displaySection.addFirst(prevSectionElement);
                prevSectionElement();
            }
        } else {
            SectionElement prevSectionInCache = this.sectionElementsCache.getFirst();
            this.displaySection.clear();
            if (prevSectionInCache.getPageCount() >= 2 && currentSectionElement != prevSectionInCache) {
                prevSectionInCache.fillViewBackward(prevSectionInCache.getPageCount() - 1, 2);
                this.displaySection.add(prevSectionInCache);
                prevSectionElement();
            } else if (prevSectionInCache.getPageCount() == 1) {
                prevSectionInCache.fillViewBackward(prevSectionInCache.getPageCount() - 1, 2);
                this.displaySection.add(prevSectionInCache);
                SectionElement temp = prevSectionElement();
                temp.fillViewBackward(temp.getPageCount() - 1, 1);
                this.displaySection.addFirst(temp);
            }
        }
        this.currentPage = Math.max(0, this.currentPage - 1);
    }

    public void next() {
        SectionElement currentSectionElement = this.displaySection.getLast();
        SectionElement.PageState pageState = currentSectionElement.next();
        if (pageState == SectionElement.PageState.HAS_TWO_PAGE) {
            if (this.displaySection.size() == 2) {
                this.displaySection.removeFirst();
                SectionElement nextSectionElement = this.sectionElementsCache.getLast();
                if (currentSectionElement == nextSectionElement) {
                    nextSectionElement();
                }
            }
        } else if (pageState == SectionElement.PageState.ONLY_RIGHT_PAGE) {
            currentSectionElement.fillViewForward(currentSectionElement.getCurrentPageIndex(), 2);
            if (this.displaySection.size() == 2) {
                this.displaySection.removeFirst();
                nextSectionElement();
                this.displaySection.add(this.sectionElementsCache.getLast());
            } else {
                this.displaySection.add(this.sectionElementsCache.getLast());
                nextSectionElement();
            }
        } else {
            SectionElement nextSectionElement = this.sectionElementsCache.getLast();
            this.displaySection.clear();
            if (nextSectionElement.getRemainingPage() >= 2 && currentSectionElement != nextSectionElement) {
                nextSectionElement.fillViewForward(0, 2);
                this.displaySection.add(nextSectionElement);
                nextSectionElement();
            } else if (nextSectionElement.getRemainingPage() == 1) {
                nextSectionElement.fillViewForward(0, 2);
                this.displaySection.add(nextSectionElement);
                this.displaySection.add(nextSectionElement());
            }
        }
        this.currentPage++;
    }

    private @NonNull SectionElement prevSectionElement() {
        this.sectionElementsCache.removeLast();
        this.sectionCacheEndIndex--;
        String sectionElementName = this.sectionList.get(this.sectionCacheEndIndex - 2);
        SectionElement sectionElement = new SectionElement(this.allSectionData.get(sectionElementName));
        sectionElement.format(this.context);
        this.sectionElementsCache.addFirst(sectionElement);
        return sectionElement;
    }

    private @NonNull SectionElement nextSectionElement() {
        this.sectionElementsCache.removeFirst();
        this.sectionCacheEndIndex++;
        String sectionElementName = this.sectionList.get(this.sectionCacheEndIndex);
        SectionElement sectionElement = new SectionElement(this.allSectionData.get(sectionElementName));
        sectionElement.format(this.context);
        this.sectionElementsCache.addLast(sectionElement);
        return sectionElement;
    }

    private @NonNull Map<String, SectionData> getStringSectionDataMap() {
        Map<String, SectionData> sectionDataList = this.handbookData.sections();
        Map<String, SectionData> noEmptySectionDataList = new LinkedHashMap<>();
        Map<String, Map<String, String>> catalogueEntry = new LinkedHashMap<>();
        sectionDataList.forEach((sectionName, sectionData) -> {
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
        if (handbookData != null) {
            this.context.setColours(handbookData.colours());
            this.context.setImages(handbookData.images());
            this.context.setRecipes(handbookData.recipes());
        }
        if (this.handbookData != null) {
            Map<String, SectionData> noEmptySectionDataList = getStringSectionDataMap();
            this.sectionList.addAll(noEmptySectionDataList.keySet());
            this.allSectionData.putAll(noEmptySectionDataList);
            int cacheCount = Math.clamp(noEmptySectionDataList.size(), 0, 2);
            for (int i = 0; i < cacheCount; i++) {
                String sectionName = this.sectionList.get(i);
                SectionElement sectionElement = new SectionElement(noEmptySectionDataList.get(sectionName));
                sectionElement.format(this.context);
                sectionElementsCache.add(sectionElement);
            }
            if (!this.sectionElementsCache.isEmpty()) {
                this.sectionElementsCache.addFirst(this.sectionElementsCache.getFirst());
                this.displaySection.add(this.sectionElementsCache.get(1));
                this.sectionCacheEndIndex = 1;
            }
        }
    }
}
