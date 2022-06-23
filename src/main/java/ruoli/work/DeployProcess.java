package ruoli.work;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;

/**
 * 部署流程至数据库
 */
public class DeployProcess {
     public static void main(String[] args) {
          // 配置数据库相关信息 获取 ProcessEngineConfiguration
          ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                  .setJdbcUrl("jdbc:mysql://localhost:3306/flowable?serverTimezone=UTC&nullCatalogMeansCurrent=true")
                  .setJdbcUsername("root")
                  .setJdbcPassword("123456")
                  .setJdbcDriver("com.mysql.cj.jdbc.Driver")
                  .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
          // 获取流程引擎对象
          ProcessEngine processEngine = cfg.buildProcessEngine();
          // 部署流程 获取RepositoryService对象
          RepositoryService repositoryService = processEngine.getRepositoryService();
          Deployment deployment = repositoryService.createDeployment()// 创建Deployment对象
                  .addClasspathResource("holiday-request.bpmn20.xml") // 添加流程部署文件
                  .name("请求流程") // 设置部署流程的名称
                  .deploy(); // 执行部署操作
          System.out.println("deployment.getId() = " + deployment.getId());
          System.out.println("deployment.getName() = " + deployment.getName());
     }
}
