package pub.zgq.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pub.zgq.community.service.AlphaService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author 孑然
 */
@SpringBootTest
public class TransactionTest {

    @Autowired
    private AlphaService alphaService;

    @Test
    public void test() {
        alphaService.save1();
    }
    @Test
    public void test1() {
        alphaService.save1();
    }


}
