package org.mediaplayer.android.ui;

public class CreationSearchItem {
    public String type;
    public String name;
    public String length;
    public String id;
    public boolean visible;

    public CreationSearchItem(String type, String name, String length, String id) {
        this.type = type;
        this.name = name;
        this.length = length;
        this.id = id;
        this.visible = true;
    }
    public CreationSearchItem copy() {
        return new CreationSearchItem(type, name, length, id);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CreationSearchItem item) {
            return this.id.equals(item.id);
        }
        return super.equals(obj);
    }
}
