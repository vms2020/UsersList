package usr.lst.userslist

import androidx.lifecycle.ViewModel
import usr.lst.userslist.db.LoginState
import usr.lst.userslist.db.MyState

class GreetingViewModel: ViewModel() {
    val loginState = MyState.loginState
}