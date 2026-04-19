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
}
