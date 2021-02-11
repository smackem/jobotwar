package net.smackem.jobotwar.web.jsh;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.plugin.json.JavalinJackson;
import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.persist.PersistableRobot;
import net.smackem.jobotwar.persist.PersistableRobots;
import net.smackem.jobotwar.web.RestClient;
import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.beans.RobotBean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("WeakerAccess") // allow public access for jshell
public class Jsh {
    public static RestClient http = new RestClient("http://localhost:5666/");

    public static RobotBean[] getRobots() throws Exception {
        return JavalinJson.fromJson(http.get("robot").body(), RobotBean[].class);
    }

    public static MatchBean[] getMatches() throws Exception {
        return JavalinJson.fromJson(http.get("match").body(), MatchBean[].class);
    }

    public static void printJson(Object bean) throws JsonProcessingException {
        final String json = JavalinJackson.getObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(bean);
        System.out.println(json);
    }

    public static RobotBean loadRobotBean(Path filePath) throws IOException {
        final RobotBean bean = new RobotBean("");
        try (final InputStream is = Files.newInputStream(filePath)) {
            PersistableRobots.load(() -> new LocalPersistableRobot(bean), is);
        }
        return bean;
    }

    private static record LocalPersistableRobot(RobotBean bean) implements PersistableRobot {

        @Override
        public String getSourceCode() {
            return this.bean.code();
        }

        @Override
        public void setSourceCode(String value) {
            this.bean.code(value);
        }

        @Override
        public String getSourceCodeLanguage() {
            return this.bean.language().name();
        }

        @Override
        public void setSourceCodeLanguage(String value) {
            this.bean.language(Enum.valueOf(Compiler.Language.class, value));
        }

        @Override
        public String getBaseName() {
            return bean.name();
        }

        @Override
        public void setBaseName(String value) {
            bean.name(value);
        }
    }
}
