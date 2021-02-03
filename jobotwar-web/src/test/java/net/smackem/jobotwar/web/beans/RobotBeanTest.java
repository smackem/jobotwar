package net.smackem.jobotwar.web.beans;

import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.lang.Compiler;
import org.junit.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class RobotBeanTest {

    @Test
    public void serialize() {
        final RobotBean bean = new RobotBean("1001")
                .dateModified(OffsetDateTime.now())
                .acceleration(2.5)
                .code("illegal code")
                .language(Compiler.Language.V2)
                .name("robot-1")
                .dateCreated(OffsetDateTime.now().minusSeconds(1))
                .rgb(0xff0000);
        final String json = JavalinJson.toJson(bean);
        final RobotBean bean2 = JavalinJson.fromJson(json, RobotBean.class);
        assertThat(bean.dateCreated()).isEqualTo(bean2.dateCreated());
        assertThat(bean2).isEqualTo(bean);
    }
}