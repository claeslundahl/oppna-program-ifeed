package se.vgregion.test;

import com.liferay.portal.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.ifeed.service.ifeed.ObjectRepo;
import se.vgregion.ifeed.shared.DynamicTableDef;
import se.vgregion.ifeed.shared.DynamicTableSortingDef;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.VgrDepartment;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * Created by clalu4 on 2016-03-23.
 */
@Service
public class JpaIntegrationTest {

  @Autowired
  ObjectRepo repo;

  //@Transactional
  public static void main(String[] arg) throws IOException, InterruptedException {
    System.out.println(new File(".").getAbsolutePath());
    String p = "spring/jpa-connector-direct.xml";
    ApplicationContext context = new ClassPathXmlApplicationContext(p);
    EntityManagerFactory emf = (EntityManagerFactory) context.getBean("entityManagerFactory");
    EntityManager em = emf.createEntityManager();

    JpaIntegrationTest jit = context.getBean(JpaIntegrationTest.class);
    System.out.println(jit);
    em.getTransaction().begin();
    jit.main();
    em.getTransaction().commit();
    TimeUnit.SECONDS.sleep(5);
  }

  /*@Transactional
  public void callMain() {
    try {
      main();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }*/

  @Transactional
  public void main() throws InterruptedException {

    try {

      //SessionFactory sessionFactory = em.unwrap(SessionFactory.class);

      //ObjectRepo repo = context.getBean(ObjectRepo.class);
      // System.out.println(repo);
    /*for (String s : context.getBeanDefinitionNames()) {
      System.out.println(s);
    }*/

      IFeed feed = new IFeed();
      feed.setDescription(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));

      //feed.setId(-1l);
      //List<IFeed> allFeeds = repo.findAll(IFeed.class);System.out.println("Hittade " + allFeeds.size() + " stycken.");
      //System.out.println("Hittade " + repo.findAll(DynamicTableSortingDef.class).size() + " stycken.");
      VgrDepartment department = new VgrDepartment();
      //department.setId(-1l);
      //repo.persist(department);
      repo.persist(feed);
      DynamicTableDef dt = new DynamicTableDef();
      dt.setName(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
      DynamicTableSortingDef es = new DynamicTableSortingDef();
      es.setDirection("asc");
      es.setName("dc.title.fejk");
      es.setIndex(1);
      es.setTableDef(dt);
      dt.getExtraSorting().add(es);
      feed.getDynamicTableDefs().add(dt);
      //repo.merge(feed);
      repo.persist(dt);
      //System.out.println(repo.findByPrimaryKey(IFeed.class, -1l));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
