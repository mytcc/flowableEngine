package ruoli.work;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RuntimeServiceTest {
    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcess(){
        // 获取流程引擎对象，使用默认的 配置文件 flowable.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 启动流程实例通过 RuntimeService 对象
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 构建流程变量
        Map<String,Object> variables = new HashMap<>();
        variables.put("assignee","张三") ;
        // 启动流程实例，第一个参数是流程定义的id
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("simpleProcess", variables);// 启动流程实例
        // 输出相关的流程实例信息
        System.out.println("流程定义的ID：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例的ID：" + processInstance.getId());
        System.out.println("当前活动的ID：" + processInstance.getActivityId());
    }

    /**
     * 查看任务
     */
    @Test
    public void testQueryTask(){
        // 获取流程引擎对象，使用默认的 配置文件 flowable.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        TaskService taskService = processEngine.getTaskService();
//        List<Task> list = taskService.createTaskQuery()
//                .processDefinitionKey("simpleProcess")
//                .taskAssignee("张三")
//                .list();
//        for (Task task : list) {
//            System.out.println("task.getProcessDefinitionId() = " + task.getProcessDefinitionId());
//            System.out.println("task.getId() = " + task.getId());
//            System.out.println("task.getAssignee() = " + task.getAssignee());
//            System.out.println("task.getName() = " + task.getName());
//            System.out.println("task.getProcessInstanceId() = " + task.getProcessInstanceId());
//        }

        ExecutionEntity  executionEntity = (ExecutionEntity) processEngine.getRuntimeService().createProcessInstanceQuery()
                .processInstanceId("112501").includeProcessVariables().singleResult();
        System.out.println(executionEntity);
    }

    /**
     * 完成任务
     */
    @Test
    public void testCompleteTask(){
        // 获取流程引擎对象，使用默认的 配置文件 flowable.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processInstanceId("190001")
                .taskAssignee("赵六")
                .list().get(0);
        // 添加流程变量
        Map<String,Object> variables = new HashMap<>();
        //variables.put("assignee","张三");
        //variables.put("num",15000);
        variables.put("assignees", Arrays.asList("张3","李4"));
        // 完成任务
        taskService.complete(task.getId(),variables);
    }

    /**
     * 串行回退
     */
    @Test
    public void testRejectTask(){
        // 获取流程引擎对象，使用默认的 配置文件 flowable.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRuntimeService().createChangeActivityStateBuilder().processInstanceId("2501")
                //.moveActivityIdTo("sid-2F445501-1FE7-4851-A566-DC8855FF1D67","sid-66CBC6BC-5B9D-431C-9A14-C0015566DF8E")
                .moveExecutionToActivityId("22501","user5")
                .changeState();
    }

    /**
     * 会签回退
     */
    @Test
    public void testRejectHqTask(){
        // 获取流程引擎对象，使用默认的 配置文件 flowable.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRuntimeService().createChangeActivityStateBuilder().processInstanceId("2501")
                .moveActivityIdTo("sid-2F445501-1FE7-4851-A566-DC8855FF1D67","sid-66CBC6BC-5B9D-431C-9A14-C0015566DF8E")
                .changeState();



    }




}
