package net.smackem.jobotwar.gui.edit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.persist.PersistableRobot;

class EditRobotViewModel implements PersistableRobot {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty sourceCode = new SimpleStringProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final ObjectProperty<Image> image = new SimpleObjectProperty<>();
    private final ObjectProperty<Compiler.Language> language = new SimpleObjectProperty<>(Compiler.Language.V1);

    public StringProperty nameProperty() {
        return this.name;
    }

    public StringProperty sourceCodeProperty() {
        return this.sourceCode;
    }

    public ObjectProperty<Compiler.Language> languageProperty() {
        return this.language;
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
    public String getSourceCodeLanguage() {
        return this.language.get().name();
    }

    @Override
    public void setSourceCodeLanguage(String value) {
        this.language.set(Enum.valueOf(Compiler.Language.class, value));
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
