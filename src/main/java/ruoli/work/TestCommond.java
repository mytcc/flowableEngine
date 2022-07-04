package ruoli.work;

import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.runtime.Execution;

import java.io.Serializable;

public class TestCommond implements Command<Execution>, Serializable {

    private String executioinId;

    public TestCommond(String executioinId){
        this.executioinId=executioinId;
    }

    @Override
    public Execution execute(CommandContext commandContext) {
        //ExecutionEntity executionEntity = CommandContextUtil.getExecutionEntityManager().findFirstMultiInstanceRoot()

        //executionEntity.getExecutions()

        return null;
    }
}
