module ModelA {
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires sysout.over.slf4j;
    requires jcommander;
    opens tech.anapad.modela to jcommander;

    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;

    exports tech.anapad.modela to javafx.graphics;
}
