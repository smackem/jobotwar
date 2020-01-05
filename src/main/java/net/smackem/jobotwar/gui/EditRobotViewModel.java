package net.smackem.jobotwar.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

public class EditRobotViewModel {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty sourceCode = new SimpleStringProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public StringProperty nameProperty() {
        return this.name;
    }

    public StringProperty sourceCodeProperty() {
        return this.sourceCode;
    }

    public ObjectProperty<Color> colorProperty() {
        return this.color;
    }
}
