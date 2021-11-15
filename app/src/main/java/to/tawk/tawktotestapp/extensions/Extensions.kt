package to.tawk.tawktotestapp.extensions

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import kotlin.math.pow


// Retry Alogorithm, use for retrofit api Single value
// Our function Zips error emissions with a range to limit them to {maxRetryCount},
// and emits a 0L with a power of 2 based delay.
// If emitted error is an UnknownHostException no more retries are done and
// error is thrown, otherwise if the limit of retries is reached error is
// thrown too, in both cases to ensure it reaches our subscribe block

fun <T> Single<T>.retryExponential(maxRetryCount :Int = 3) : Single<T> =
    retryWhen { errors: Flowable<Throwable> ->
        errors.zipWith(
            Flowable.range(1, maxRetryCount + 1),
            { error: Throwable, retryCount: Int ->
                if(error is UnknownHostException || retryCount > maxRetryCount) throw error
                else retryCount
            }
        ).flatMap { retryCount: Int ->
            Flowable.timer( 2.toDouble().pow(retryCount).toLong(), TimeUnit.SECONDS )
        }
    }

