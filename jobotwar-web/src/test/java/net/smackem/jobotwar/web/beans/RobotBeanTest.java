package net.smackem.jobotwar.web.beans;

import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.lang.Compiler;
import org.junit.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                .rgb(0xff0000)
                .freeze();
        final String json = JavalinJson.toJson(bean);
        final RobotBean bean2 = JavalinJson.fromJson(json, RobotBean.class);
        assertThat(bean.dateCreated()).isEqualTo(bean2.dateCreated());
        assertThat(bean2).isEqualTo(bean);
        assertThat(bean2.isFrozen()).isNotEqualTo(bean.isFrozen());
        assertThat(bean2.isFrozen()).isFalse();
    }

    @Test
    public void freeze() {
        final RobotBean bean = new RobotBean("1001")
                .acceleration(2.5)
                .code("illegal code");
        assertThat(bean.isFrozen()).isFalse();
        bean.freeze();
        assertThat(bean.isFrozen()).isTrue();
        assertThatThrownBy(() -> bean.name("hupp")).isInstanceOf(UnsupportedOperationException.class);
    }
}
