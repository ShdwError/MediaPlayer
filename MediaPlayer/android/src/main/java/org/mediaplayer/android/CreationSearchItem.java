package org.mediaplayer.android;

public class CreationSearchItem {
    String type, name, length, id;
    boolean visible;

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
