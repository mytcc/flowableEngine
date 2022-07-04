package ruoli.work.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class TaskCompleteListener implements JavaDelegate {
    /**
     * 触发发送邮件的操作
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {
        System.out.println("总的会签任务数量：" + delegateExecution.getVariable("nrOfInstances")
                + "当前获取的会签任务数量：" + delegateExecution.getVariable("nrOfActiveInstances")
                + " - " + "已经完成的会签任务数量：" + delegateExecution.getVariable("nrOfCompletedInstances"));
    }
}