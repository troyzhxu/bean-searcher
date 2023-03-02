package features;

import com.example.demo.App;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.AbstractHttpTester;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class HelloTest extends AbstractHttpTester {
    @Test
    public void hello() throws IOException {
        assert path("/hello?name=world").get().contains("world");
        assert path("/hello?name=solon").get().contains("solon");
    }
}
