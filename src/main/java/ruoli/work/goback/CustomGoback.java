package ruoli.work.goback;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.task.api.history.HistoricTaskInstance;
import ruoli.work.IProcessGoBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class CustomGoback implements IProcessGoBack {
    /**
     * 普通撤回，此场景总共需要满足两个条件
     * 1、当前环节 所有人员处理完成（包含会签类型）
     * 2、下一个环节 必须是用户任务环节（包含单人处理、会签）
     * @param taskInstance 当前环节任务实例（用户操作撤回）
     * @return
     */
    @Override
    public boolean sceneCheck(HistoricTaskInstance taskInstance) {
        // 1、查询当前环节 是否所有用户都已经完成 (必须所有都完成)
        this.isFinished(taskInstance.getProcessInstanceId(),taskInstance.getTaskDefinitionKey());
        List<HistoricTaskInstance> currentTaskList=processEngine.getHistoryService()
        .createHistoricTaskInstanceQuery().processInstanceId(taskInstance.getProcessInstanceId()).
                taskDefinitionKey(taskInstance.getTaskDefinitionKey()).list();
        boolean anyNotComplete=currentTaskList.stream().anyMatch(t->t.getEndTime()==null);
        if(anyNotComplete){
            return false;
        }

        // 2、下一个环节 必须是 用户任务环节
        FlowElement nextNode=this.getNextNode(taskInstance.getProcessDefinitionId(),taskInstance.getTaskDefinitionKey());
        if (!(nextNode  instanceof UserTask)) {
            return false;
        }

        return true;
    }

    /**
     * 判断是否可以撤回,满足：下一步处理人全部未处理
     * @param taskInstance
     * @return
     */
    @Override
    public List<HistoricTaskInstance> canDo(HistoricTaskInstance taskInstance) {
        FlowElement nextNode=this.getNextNode(taskInstance.getProcessDefinitionId(),taskInstance.getTaskDefinitionKey());
        UserTask nextUserNode = (UserTask) nextNode;
        // 获取下一个节点的 任务实例
        List<HistoricTaskInstance> taskList =processEngine.getHistoryService().createHistoricTaskInstanceQuery()
                .processInstanceId(taskInstance.getProcessInstanceId())
                .taskCreatedAfter(taskInstance.getCreateTime())
                .taskDefinitionKey(nextUserNode.getId())
                .list();
        boolean anyComplete=taskList.stream().anyMatch(t->t.getEndTime()!=null);
        if(anyComplete){
            return null;
        }
        return taskList;
    }

    @Override
    public void doIt(List<HistoricTaskInstance> tasks, HistoricTaskInstance taskInstance,String assignee) {
        List<String> executionIds=new ArrayList<>();
        tasks.stream().forEach(t->executionIds.add(t.getExecutionId()));
        List<String> noRepeatIds=executionIds.stream().distinct().collect(Collectors.toList());
        processEngine.getRuntimeService().createChangeActivityStateBuilder().processInstanceId(taskInstance.getProcessInstanceId())
                .moveExecutionsToSingleActivityId(noRepeatIds,taskInstance.getTaskDefinitionKey())
                .processVariables(Collections.singletonMap("assignee", assignee))
                .changeState();
    }
}
