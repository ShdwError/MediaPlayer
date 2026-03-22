module org.mediaplayer {
    requires javafx.controls;
    requires javafx.media;
    exports org.mediaplayer.desktop;
    opens org.mediaplayer.desktop to javafx.graphics;
}
