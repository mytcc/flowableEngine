package ruoli.work.goback;

import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.InclusiveGateway;
import org.flowable.bpmn.model.ParallelGateway;
import org.flowable.task.api.history.HistoricTaskInstance;
import ruoli.work.IProcessGoBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GatewayGoBack implements IProcessGoBack {
    /**
     * 当前环节 为用户环节，并且已经完成，下一环节为 并行网关、串行网关、包容网关
     * @param taskInstance
     * @return
     */
    @Override
    public boolean sceneCheck(HistoricTaskInstance taskInstance) {
        boolean finshed=this.isFinished(taskInstance.getProcessInstanceId(),taskInstance.getTaskDefinitionKey());
        if(!finshed){
            return false;
        }
        FlowNode nextNode=this.getNextNode(taskInstance.getProcessDefinitionId(),taskInstance.getTaskDefinitionKey());
        //排他网关、并行网关、包容网关
        if(nextNode instanceof ExclusiveGateway || nextNode instanceof ParallelGateway ||  nextNode instanceof InclusiveGateway){
            return true;
        }
       return false;
    }

    @Override
    public List<HistoricTaskInstance> canDo(HistoricTaskInstance taskInstance) {
        FlowNode nextNode=this.getNextNode(taskInstance.getProcessDefinitionId(),taskInstance.getTaskDefinitionKey());
        List<FlowNode> nextUserNode=new ArrayList<>();
        nextNode.getOutgoingFlows().stream().forEach(t->nextUserNode.add((FlowNode)t.getTargetFlowElement()));
        //网关下一步的用户环节 都必须没有处理
        List<HistoricTaskInstance> nextUserTaskList=new ArrayList<>();
        boolean allUnfinished=nextUserNode.stream().allMatch(t->{
             //   !this.isFinished(taskInstance.getProcessInstanceId(),t.getId())
            List<HistoricTaskInstance> tmpList=processEngine.getHistoryService().createHistoricTaskInstanceQuery()
                    .processInstanceId(taskInstance.getProcessInstanceId())
                    .taskDefinitionKey(t.getId()).taskCreatedAfter(taskInstance.getCreateTime())
                    .list();
            nextUserTaskList.addAll(tmpList);
            return tmpList.stream().allMatch(x->x.getEndTime()==null);
        });
        if(allUnfinished){
           return nextUserTaskList;
        }
        return null;

    }

    @Override
    public void doIt(List<HistoricTaskInstance> tasks, HistoricTaskInstance taskInstance, String assignee) {
        List<String> executionIds=new ArrayList<>();
        tasks.stream().forEach(t->executionIds.add(t.getExecutionId()));
        List<String> noRepeatIds=executionIds.stream().distinct().collect(Collectors.toList());
        processEngine.getRuntimeService().createChangeActivityStateBuilder().processInstanceId(taskInstance.getProcessInstanceId())
                .moveExecutionsToSingleActivityId(noRepeatIds,taskInstance.getTaskDefinitionKey())
                .processVariables(Collections.singletonMap("assignee", assignee))
                .changeState();
    }
}
