module ModelA {
    // Logging dependencies
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires sysout.over.slf4j;
    requires jcommander;
    opens tech.anapad.modela to jcommander;

    // Google Guava
    requires com.google.common;

    // JavaFX
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    exports tech.anapad.modela to javafx.graphics;

    // Resources
    opens logback;
    opens image.icon;
    opens image.anapad;
}
