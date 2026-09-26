package com.mel182.cancellationexception

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class App: Application() {

    // Use this in case you want a coroutine scope that lives the entire lifetime of the app
    // In here you can see 'Dispatchers.Main + SupervisorJob()' were the Dispatcher is the coroutine dispatcher
    // and the 'SupervisorJob()' make sure that if any coroutine throw a cancellation exception that it doesn't have any impact on the coroutine itself and remains active
    // Usage:
    // Pass it as an argument
    // class DemoRepository(private val applicationScope: CoroutineScope) { Implementation omitted... }
    //
    // Within the target class that use it do the following:
    // applicationScope.launch {
    //   Implementation.....
    // }.join()
    // It will outlives the viewmodelScope and lifecycleScope.
    val applicationScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


}