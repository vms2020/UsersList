package usr.lst.userslist.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

enum class LoginState {
    START, LOGIN, REGISTER, LOGGED_IN
}

object MyState {
    val loginState = MutableStateFlow(LoginState.START)
    val loggedUser = MutableStateFlow<QuUser?>(null)
    suspend fun saveLoggedInName(u: QuUser, ctx: Context){
        ctx.dataStore.edit {
            it[nameKey]=u.name
        }
        loginState.value=LoginState.LOGGED_IN
        loggedUser.value=u
    }
    suspend fun logout(ctx: Context){
        ctx.dataStore.edit {
            it.remove(nameKey)
        }
        loginState.value=LoginState.START
        loggedUser.value=null
    }

}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prefdatastore")
val nameKey = stringPreferencesKey("name")
