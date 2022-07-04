package ruoli.work;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.util.*;

/**
 * 会签测试
 */
public class MulitiInstanceTest {
    /**
     * 部署会签流程
     */
    @Test
    public void testDeploy() {
        // 获取流程引擎对象，使用默认的 配置文件 flowable.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 部署流程 获取RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("简单流程.bpmn20.xml")
                .name("会签案例")
                .deploy();
        System.out.println("deploy.getId() = " + deploy.getId());
        System.out.println("deploy.getName() = " + deploy.getName());
        System.out.println("部署开始的时间：" + new Date());
        //TimeUnit.MINUTES.sleep(3);
    }

    /**
     * 启动会签流程
     */
    @Test
    public void startFlow(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRuntimeService().startProcessInstanceByKey("hqch");
    }

    /**
     * 进入会签Task
     */
    @Test
    public void testCompleteTask(){
        // 获取流程引擎对象，使用默认的 配置文件 flowable.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("hqch")
                .taskAssignee("user1")
                .singleResult();
        // 添加流程变量
        Map<String,Object> variables = new HashMap<>();
        // 设置多人会签的数据
        variables.put("persons", Arrays.asList("张三","李四","王五","赵六"));
        // 完成任务
        taskService.complete(task.getId(),variables);
    }

    /**
     * 会签Task单个处理
     */
    @Test
    public void testCompleteTask2(){
        // 获取流程引擎对象，使用默认的 配置文件 flowable.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("hqch")
                .taskAssignee("user5")
                .singleResult();
        // 添加流程变量
        Map<String,Object> variables = new HashMap<>();
        // 设置多人会签的数据
        variables.put("flag", 1);
        // 完成任务
        taskService.complete(task.getId(),variables); //17514
    }

    /**
     * 会签完成后回退
     */
    @Test
    public void testRejectHqTask(){
        // 获取流程引擎对象，使用默认的 配置文件 flowable.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        processEngine.getRuntimeService().createChangeActivityStateBuilder().processInstanceId("190001")
                .moveActivityIdTo("userTask-1","userTask-5")
                .processVariable("assignee", "张三")
                .changeState();
    }


    /**
     * 会签中单个回退
     */
    @Test
    public void testRejectHqTask2(){

        // Map<String,Object> variables = new HashMap<>();


        // 获取流程引擎对象，使用默认的 配置文件 flowable.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //processEngine.getRuntimeService().addMultiInstanceExecution("hqTask-1","112501",Collections.singletonMap("assignee", "赵六"));

        processEngine.getManagementService().executeCommand(new TestCommond("142510"));

    }



}
