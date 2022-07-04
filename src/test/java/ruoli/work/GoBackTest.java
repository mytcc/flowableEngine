package ruoli.work;

import org.flowable.engine.*;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.Test;
import ruoli.work.goback.CustomGoback;
import ruoli.work.goback.GatewayGoBack;
import ruoli.work.goback.HqInnerGoback;

import java.util.ArrayList;
import java.util.List;

// 通用撤回处理
public class GoBackTest {
    public static String processInstanceId="190001"; //流程实例ID
    public static String taskId="195010"; //当前任务ID

    public static List<IProcessGoBack> processGoBacks=new ArrayList<>();

    static {
        processGoBacks.add(new CustomGoback());
        processGoBacks.add(new HqInnerGoback());
        processGoBacks.add(new GatewayGoBack());
    }

    @Test
    public void testGoback(){
        HistoricTaskInstance taskInstance= historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskId(taskId).singleResult();
        for(IProcessGoBack processGoBack:processGoBacks){
            if(processGoBack.sceneCheck(taskInstance)){
                List<HistoricTaskInstance> taskInstances=processGoBack.canDo(taskInstance);
                if(taskInstances!=null){
                    processGoBack.doIt(taskInstances,taskInstance,"赵六");
                }
            }
        }
    }

    public static ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
    public static RepositoryService repositoryService = processEngine.getRepositoryService();
    public static RuntimeService runtimeService=processEngine.getRuntimeService();
    public static TaskService taskService=processEngine.getTaskService();
    HistoryService historyService=processEngine.getHistoryService();

//    @Test
//    public void testGetNextTaskStatus() throws Exception {
//        taskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskId(taskId).singleResult();
//        if(taskInstance.getEndTime()==null){
//            System.out.println("当前环节 未处理，无法执行撤回");
//            return;
//        }
//        //验证流程是否已结束，已结束的流程不支持撤回
//        HistoricProcessInstance historicProcessInstance=historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).unfinished().singleResult();
//        if(historicProcessInstance==null){
//            System.out.println("流程已结束，无法撤回");
//            return;
//        }
//        //获取流程发布Id信息
//        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();
//        //获取bpm对象
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
//        //传节点定义key 获取当前节点
//        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey());
//        //输出连线
//        List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
//        if(outgoingFlows.size()!=1){
//            throw new Exception("用户任务出栈路由缺失或出现多条，请核对");
//        }
//        SequenceFlow sequenceFlow=outgoingFlows.get(0);
//        FlowElement targetFlowElement=sequenceFlow.getTargetFlowElement();
//
//
//        if (targetFlowElement instanceof UserTask) {
//            UserTask userTask = (UserTask) targetFlowElement;
//            List<HistoricTaskInstance> tasks=canGobackUserTask(userTask);
//            if(tasks!=null){
//                Map<String,Object> variables=new HashMap<>();
//                //variables.put("assignees",Arrays.asList("张三"));
//                variables.put("assignee","张三");
//                doGobackUserTask(tasks,taskInstance.getTaskDefinitionKey(),variables);
//            }else{
//                System.out.println("下一步任务不存在 或 已被处理，不能撤回。");
//                return;
//            }
//        }
//
//
//
//        //下一个节点是 并行网关（包容网关）
//
//        //下一个节点是 排他网关
//
//        //下一个节点是 结束节点
//        if (targetFlowElement instanceof EndEvent) {
//            System.out.println("下一步为结束节点，无法撤回");
//            return;
//        }
//        //下一个节点是子流程
//    }
//
//    private List<HistoricTaskInstance> canGobackUserTask(UserTask userTask){
//        List<HistoricTaskInstance> tasklist =processEngine.getHistoryService().createHistoricTaskInstanceQuery()
//                .processInstanceId(processInstanceId)
//                .taskCreatedAfter(task.getCreateTime())
//                .taskDefinitionKey(userTask.getId())
//                .list();
//        if(tasklist!=null && tasklist.size()>0){
//            for (HistoricTaskInstance histask:tasklist){
//                if(histask.getEndTime()!=null){
//                    System.out.println("流程已处理，无法撤回");
//                    return null;
//                }
//            }
//            return tasklist;
//        }
//        return null;
//    }
//
//    // 撤回到 普通用户任务  或者 整个节点撤回到 会签
//    private void doGobackUserTask(List<HistoricTaskInstance> tasks, String targetActivityId, Map<String,Object> variables){
//        ArrayList<String> actIds=new ArrayList<>();
//        for(HistoricTaskInstance task:tasks){
//            if(!actIds.contains(task.getExecutionId()) && task.getEndTime()==null) {
//                actIds.add(task.getExecutionId());
//            }
//        }
//        processEngine.getRuntimeService().createChangeActivityStateBuilder().processInstanceId(processInstanceId)
//                .moveExecutionsToSingleActivityId(actIds,targetActivityId)
//                .processVariables(variables)
//                .changeState();
//    }
//
//    /**
//     * 当前任务仍然是会签，近单个撤回
//     * @param tasks
//     * @param targetActivityId
//     * @param variables
//     */
//    private void doGobackHqTask(List<HistoricTaskInstance> tasks, String targetActivityId, Map<String,Object> variables){
//
//    }


}
