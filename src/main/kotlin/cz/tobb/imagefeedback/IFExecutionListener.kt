package cz.tobb.imagefeedback

import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment


class IFExecutionListener : ExecutionListener {

    companion object {
        var IFComponent : IFComponent? = null
    }

    override fun processTerminated(executorId: String, env: ExecutionEnvironment, handler: ProcessHandler, exitCode: Int) {
        IFComponent?.update(if(exitCode == 0) FeedBackImageType.SUCCESS else FeedBackImageType.FAIL, exitCode)
    }

    override fun processStarting(executorId: String, env: ExecutionEnvironment) {
        super.processStarting(executorId, env)
        IFComponent?.update(FeedBackImageType.PROCESSING, -1)
    }
}