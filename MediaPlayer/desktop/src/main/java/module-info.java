module org.mediaplayer.desktop {
    requires javafx.controls;
    requires javafx.media;
	requires org.eclipse.jetty.client;
	requires org.eclipse.jetty.websocket.client;
	requires org.eclipse.jetty.websocket.api;
    requires org.mediaplayer.core;

    exports org.mediaplayer.desktop;
    exports Tools.Desktop.Connection;
}