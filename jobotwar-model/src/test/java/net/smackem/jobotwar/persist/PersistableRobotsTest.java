package net.smackem.jobotwar.persist;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistableRobotsTest {

    @Test
    public void loadV0() throws IOException {
        final String json = "{\"name\":\"robot1\", \"source\":\"100 -> SPEEDX\"}";
        final InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        final PersistableRobot loaded = PersistableRobots.load(DummyPersistableRobot::new, is);
        assertThat(loaded.getBaseName()).isEqualTo("robot1");
        assertThat(loaded.getSourceCode()).isEqualTo("100 -> SPEEDX");
    }

    @Test
    public void loadV1() throws IOException {
        final String json = "" +
                "{\"meta\": { \"version\": 1 },\n" +
                "\"name\":\"robot1\", \"source\": {\n" +
                "\"code\": \"100 -> SPEEDX\", \"language\": \"V1\"\n" +
                "}}";
        final InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        final PersistableRobot loaded = PersistableRobots.load(DummyPersistableRobot::new, is);
        assertThat(loaded.getBaseName()).isEqualTo("robot1");
        assertThat(loaded.getSourceCode()).isEqualTo("100 -> SPEEDX");
    }

    @Test
    public void testSaveAndLoad() throws IOException {
        final PersistableRobot r = new DummyPersistableRobot();
        r.setBaseName("robot1");
        r.setSourceCode("100->SPEEDX");
        r.setSourceCodeLanguage("V1");
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        PersistableRobots.save(r, os);

        final InputStream is = new ByteArrayInputStream(os.toByteArray());
        final PersistableRobot loaded = PersistableRobots.load(DummyPersistableRobot::new, is);
        assertThat(loaded.getBaseName()).isEqualTo(r.getBaseName());
        assertThat(loaded.getSourceCode()).isEqualTo(r.getSourceCode());
        assertThat(loaded.getSourceCodeLanguage()).isEqualTo(r.getSourceCodeLanguage());
    }

    private static class DummyPersistableRobot implements PersistableRobot {
        private String sourceCode;
        private String baseName;
        private String language;

        @Override
        public String getSourceCode() {
            return this.sourceCode;
        }

        @Override
        public void setSourceCode(String value) {
            this.sourceCode = value;
        }

        @Override
        public String getSourceCodeLanguage() {
            return this.language;
        }

        @Override
        public void setSourceCodeLanguage(String value) {
            this.language = value;
        }

        @Override
        public String getBaseName() {
            return this.baseName;
        }

        @Override
        public void setBaseName(String value) {
            this.baseName = value;
        }
    }
}