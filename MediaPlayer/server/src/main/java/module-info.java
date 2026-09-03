module org.mediaplayer.server {
	requires org.mediaplayer.core;
	requires org.eclipse.jetty.server;
	requires org.eclipse.jetty.websocket.api;
	requires org.eclipse.jetty.websocket.server;
	requires org.mediaplayer.desktop;
	
	exports org.mediaplayer.server;
}