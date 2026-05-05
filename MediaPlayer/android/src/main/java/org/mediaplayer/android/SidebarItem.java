package org.mediaplayer.android;

import java.util.ArrayList;
import java.util.List;

public class SidebarItem {
    public String title;
    public String id;
    public boolean isHeader;
    public boolean isExpanded;
    public List<SidebarItem> items;
    public SidebarItem(String title, String id, boolean isHeader) {
        this.title = title;
        this.id = id;
        this.isHeader = isHeader;
        this.isExpanded = false;
        this.items = new ArrayList<>();
    }
    public int addSorted(SidebarItem toAdd) {
        int size = items.size();
        for(int i = 0; i < size; i++) {
            if(toAdd.title.toLowerCase().compareTo(items.get(i).title.toLowerCase()) <= 0) {
                items.add(i, toAdd);
                return i;
            }
        }
        items.add(toAdd);
        return size;
    }
}
