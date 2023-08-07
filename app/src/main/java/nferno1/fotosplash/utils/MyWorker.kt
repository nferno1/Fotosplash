package nferno1.fotosplash.utils

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import nferno1.fotosplash.utils.Constants.KEY_WORK_INPUT
import nferno1.fotosplash.utils.Constants.KEY_WORK_RESULT
import nferno1.fotosplash.utils.Constants.WORK_TAG
import java.util.concurrent.TimeUnit


class MyWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        val result = work()
        return Result.success(result)
    }

    private fun work(): Data {
        val url = inputData.getString(KEY_WORK_INPUT)
        return workDataOf(KEY_WORK_RESULT to url)
    }

    companion object {
        fun createWorkRequest(urlRaw: String): OneTimeWorkRequest {
            val networkConstraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            return OneTimeWorkRequestBuilder<MyWorker>()
                .setInputData(workDataOf(KEY_WORK_INPUT to urlRaw))
                .setInitialDelay(1, TimeUnit.SECONDS)
                .setConstraints(networkConstraint)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.SECONDS)
                .addTag(WORK_TAG)
                .build()
        }

    }
}