package de.vw.paso.client.control.tablebase.tableconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;

public class ColumnSelectionResult {

    @Getter
    private Long id;
    @Getter
    private String name;
    private List<ColumnInfo> list;
    @Getter
    private List<String> selectedIds;
    @Getter
    private List<String> selectedText;
    @Getter
    private boolean isDefault;
    @Getter
    private boolean isPublic;

    public ColumnSelectionResult(List<ColumnInfo> list) {
        this.list = list;
        this.selectedIds = new ArrayList<>();
        this.selectedText = new ArrayList<>();

        this.list.forEach(e -> {
            selectedIds.add(e.id());
            selectedText.add(e.name());
        });
    }

    public ColumnSelectionResult(List<ColumnInfo> list, Long id, String name, boolean isDefault, boolean isPublic) {
        this(list);
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
        this.isPublic = isPublic;
    }

    public ColumnSelectionResult(List<String> selectedIds, List<String> selectedText) {
        this.selectedIds = selectedIds;
        this.selectedText = selectedText;

        this.list = IntStream.range(0, Math.min(selectedIds.size(), selectedText.size()))
                .mapToObj(i -> new ColumnInfo(selectedIds.get(i), selectedText.get(i)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean isIdSelected(String id) {
        return selectedIds.contains(id);
    }

    public boolean isTextSelected(String text) {
        return selectedText.contains(text);
    }

    public int getIndexOfId(String id) {
        return selectedIds.indexOf(id);
    }

    public int getIndexOfText(String text) {
        return selectedText.indexOf(text);
    }
}
