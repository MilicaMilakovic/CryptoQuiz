module com.example.cryptoquiz {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires bcpkix.jdk15on;
    requires bcprov.jdk15on;

    opens net.etfbl.krz.cryptoquiz to javafx.fxml;
    exports net.etfbl.krz.cryptoquiz;
}