package ruoli.work.goback;

import org.flowable.bpmn.model.FlowNode;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.task.api.history.HistoricTaskInstance;
import ruoli.work.IProcessGoBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 会签内部撤回
 */
public class HqInnerGoback implements IProcessGoBack {
    /**
     * 当前是会签环节
     * @param taskInstance
     * @return
     */
    @Override
    public boolean sceneCheck(HistoricTaskInstance taskInstance) {
        FlowNode flowNode=this.getNode(taskInstance.getProcessDefinitionId(),taskInstance.getTaskDefinitionKey());
        if(!(flowNode.getBehavior() instanceof ParallelMultiInstanceBehavior)){
            // 当前环节不是会签
            return false;
        }
        //当前会签整体任务没有完成
        List<HistoricTaskInstance> taskInstances=processEngine.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(taskInstance.getProcessInstanceId())
                .taskDefinitionKey(taskInstance.getTaskDefinitionKey()).unfinished().list();
        if(taskInstances==null && taskInstances.size()==0){
            return false;
        }
        return true;
    }

    /**
     *
     * @param taskInstance
     * @return
     */
    @Override
    public List<HistoricTaskInstance> canDo(HistoricTaskInstance taskInstance) {
        return new ArrayList<>();
    }

    @Override
    public void doIt(List<HistoricTaskInstance> tasks, HistoricTaskInstance taskInstance,String assignee) {
        processEngine.getRuntimeService().addMultiInstanceExecution(
                taskInstance.getTaskDefinitionKey()
                ,taskInstance.getProcessInstanceId()
                , Collections.singletonMap("assignee", assignee));

    }
}
