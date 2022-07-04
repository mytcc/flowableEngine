package ruoli.work;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.List;

public interface IProcessGoBack {
     /**
      * 确实是否符合当前 场景
      * @param taskInstance
      * @return
      */
     boolean sceneCheck(HistoricTaskInstance taskInstance);

     /**
      * 是否 符合 撤回规则，
      * @param taskInstance
      * @return 符合规则，则返回需要撤回的任务实例清单，不符合返回 null
      */
     List<HistoricTaskInstance> canDo(HistoricTaskInstance taskInstance);

     /**
      * 执行撤回
      * @param tasks 需要撤回的 任务实例集合
      * @param taskInstance 当前操作任务实例（需要撤回到此环节）
      * @param variables 流程变量
      */
     void doIt(List<HistoricTaskInstance> tasks, HistoricTaskInstance taskInstance, String assignee);


     ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();

     /**
      * 根据 processDefinitionId、taksDefinitionKey 获取当前节点 信息
      * @param processDefinitionId
      * @param taksDefinitionKey
      * @return
      */
     default FlowNode getNode(String processDefinitionId,String taksDefinitionKey){
          //获取bpm对象
          BpmnModel bpmnModel =processEngine.getRepositoryService().getBpmnModel(processDefinitionId);
          //传节点定义key 获取当前节点
          FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(taksDefinitionKey);
          return flowNode;
     }

     /**
      * 根据 processDefinitionId、taksDefinitionKey 获取下一环节节点信息
      * @param processDefinitionId
      * @param taksDefinitionKey
      * @return
      */
     default FlowNode getNextNode(String processDefinitionId,String taksDefinitionKey){
          FlowNode flowNode=this.getNode(processDefinitionId,taksDefinitionKey);

          List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();
          SequenceFlow sequenceFlow=outgoingFlows.get(0);
          FlowNode nextFlowNode=(FlowNode) sequenceFlow.getTargetFlowElement();
          return nextFlowNode;
     }

     /**
      * 获取 流程中 指定环节 是否全部已经完成
      * @param processInstanceId
      * @param taskDefinitionKey
      * @return
      */
     default boolean isFinished(String processInstanceId,String taskDefinitionKey){
          List<HistoricTaskInstance> currentTaskList=processEngine.getHistoryService()
                  .createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).
                  taskDefinitionKey(taskDefinitionKey).list();
          if(currentTaskList!=null && currentTaskList.size()>0){
               return true;
          }
          boolean finished=currentTaskList.stream().allMatch(t->t.getEndTime()!=null);
          return finished;
     }

}
