package ruoli.work.listener;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

public class TaskCompleteEvent implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println(delegateTask.getAssignee());

    }
}
