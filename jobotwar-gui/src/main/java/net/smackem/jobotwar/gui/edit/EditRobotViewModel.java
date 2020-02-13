package net.smackem.jobotwar.gui.edit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import net.smackem.jobotwar.persist.PersistableRobot;

class EditRobotViewModel implements PersistableRobot {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty sourceCode = new SimpleStringProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();

    public StringProperty nameProperty() {
        return this.name;
    }

    public StringProperty sourceCodeProperty() {
        return this.sourceCode;
    }

    public ObjectProperty<Color> colorProperty() {
        return this.color;
    }

    public ObjectProperty<Image> imageProperty() {
        return this.image;
    }

    @Override
    public String getSourceCode() {
        return this.sourceCodeProperty().get();
    }

    @Override
    public void setSourceCode(String value) {
        this.sourceCodeProperty().set(value);
    }

    @Override
    public String getBaseName() {
        return this.nameProperty().get();
    }

    @Override
    public void setBaseName(String value) {
        this.nameProperty().set(value);
    }
}
