package usr.lst.userslist

import android.app.Application
import android.util.Log
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import usr.lst.userslist.db.LoginState
import usr.lst.userslist.db.MyState
import usr.lst.userslist.db.QuUserDatabase
import usr.lst.userslist.db.dataStore
import usr.lst.userslist.db.nameKey

private const val TAG = "MainViewModel"

class MainViewModel(val appli: Application) : AndroidViewModel(appli) {
    val loginState = MyState.loginState
    val loggedUser = MyState.loggedUser

    val dataStoreUserNameFlow = appli.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                Log.e(TAG, "dataStoreFlow: ", exception)
                emit(emptyPreferences())
            }
        }.map { preferences ->
            Log.i(TAG, "nameKey FLOW: ${preferences[nameKey]}")
            preferences[nameKey] ?: ""
        }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    init {
        val n = runBlocking { appli.dataStore.data.first() }[nameKey]
        Log.i(TAG, "init name = $n")
        if (n.isNullOrBlank()) {
            loginState.value = LoginState.START
        } else {
            loginState.value = LoginState.LOGGED_IN
            viewModelScope.launch(Dispatchers.IO) {
                loggedUser.value = QuUserDatabase.getDataBase(appli).quUserDao()
                    .fetchQuUser(n)
            }
        }
    }
}