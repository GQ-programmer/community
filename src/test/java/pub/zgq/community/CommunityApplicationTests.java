//package pub.zgq.community;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationContext;
//import pub.zgq.community.controller.AlphaController;
//import pub.zgq.community.dao.AlphaDao;
//import pub.zgq.community.service.AlphaService;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@SpringBootTest
//class CommunityApplicationTests {
//
//    @Autowired
//    ApplicationContext applicationContext;
//
//    @Test
//    void contextLoads() {
//        System.out.println(applicationContext);
//
//        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
//        System.out.println(alphaDao.select());
//
//        alphaDao  = applicationContext.getBean("alphaHibernate",AlphaDao.class);
//        System.out.println(alphaDao.select());
//    }
//
//    @Test
//    public void testBeanManagement(){
//        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
//        System.out.println(alphaService);
//
//        alphaService = applicationContext.getBean(AlphaService.class);
//        System.out.println(alphaService);
//    }
//
//    @Test
//    public void testBeanConfig(){
//        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
//        System.out.println(simpleDateFormat.format(new Date()));
//    }
//
//    @Autowired
//    private AlphaService alphaService;
//
//    @Autowired
//    @Qualifier("alphaHibernate")
//    private AlphaDao alphaDao;
//
//    @Autowired
//    private SimpleDateFormat simpleDateFormat;
//
//    @Test
//    public void testDI(){
//        System.out.println(alphaService);
//        System.out.println(alphaDao);
//        System.out.println(simpleDateFormat);
//    }
//
//}
