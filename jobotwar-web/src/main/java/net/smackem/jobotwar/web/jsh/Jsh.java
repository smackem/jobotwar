package net.smackem.jobotwar.web.jsh;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.plugin.json.JavalinJackson;
import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.persist.PersistableRobot;
import net.smackem.jobotwar.persist.PersistableRobots;
import net.smackem.jobotwar.web.RestClient;
import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.beans.MatchRobot;
import net.smackem.jobotwar.web.beans.RobotBean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess") // allow public access for jshell
public class Jsh {
    public static RestClient http = new RestClient("http://localhost:8666/");

    public static void connect(String baseUri) {
        http = new RestClient(baseUri.endsWith("/") ? baseUri : baseUri + "/");
    }
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

    public static void insertRandomMatches(int robotSetCount, int matchCountPerRobotSet) throws Exception {
        final Collection<RobotBean> robots = Arrays.asList(getRobots());
        for (int i = 0; i < robotSetCount; i++) {
            System.out.printf("%04d ", i);
            final List<String> ids = randomRobotSet(robots);
            final Stream<MatchBean> matches = IntStream.range(0, matchCountPerRobotSet)
                    .mapToObj(ignored -> new MatchBean("")
                            .boardWidth(800)
                            .boardHeight(600)
                            .maxDuration(Duration.ofMinutes(5))
                            .addRobots(ids.stream().map(MatchRobot::new).toArray(MatchRobot[]::new)));
            matches.parallel().forEach(m -> {
                System.out.print(".");
                try {
                    http.post("match", m);
                } catch (Throwable t) {
                    System.out.println(t.getMessage());
                }
            });
            System.out.println();
        }
    }

    private static List<String> randomRobotSet(Collection<RobotBean> robots) {
        final int count = ThreadLocalRandom.current().nextInt(2, 10);
        final List<RobotBean> robotSet = new ArrayList<>(robots);
        final List<String> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final RobotBean robot = robotSet.get(ThreadLocalRandom.current().nextInt(robotSet.size()));
            robotSet.remove(robot);
            ids.add(robot.id());
        }
        return ids;
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
