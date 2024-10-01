package usr.lst.userslist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import usr.lst.userslist.db.LoginState
import usr.lst.userslist.db.MyState
import usr.lst.userslist.screens.ListScr
import usr.lst.userslist.screens.LoginScr
import usr.lst.userslist.screens.RegisterScr
import usr.lst.userslist.ui.theme.UsersListTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UsersListTheme {
                val ctx = LocalContext.current
                val scope = rememberCoroutineScope()
                val vm: MainViewModel = viewModel()
                val loginState by vm.loginState.collectAsStateWithLifecycle()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                when (loginState) {
                                    LoginState.START -> Text("Welcome")
                                    LoginState.LOGIN -> Text("Login (Sign In)")
                                    LoginState.LOGGED_IN -> Text("User List")
                                    LoginState.REGISTER -> Text("Register (Sign Up)")
                                }
                            },
                            actions = {
                                if(loginState==LoginState.LOGGED_IN){
                                    IconButton(
                                        {
                                            scope.launch(Dispatchers.IO) {
                                                MyState.logout(ctx)
                                            }
                                        }
                                    ) {
                                        Icon(Icons.AutoMirrored.Default.Logout,"Logout")
                                    }
                                }
                            },
                        )
                    },
                ) { innerPadding ->
                    when (vm.loginState.collectAsStateWithLifecycle().value) {
                        LoginState.START -> Greeting(
                            modifier = Modifier.padding(innerPadding),
                            login = { vm.loginState.value = LoginState.LOGIN },
                            register = { vm.loginState.value = LoginState.REGISTER },
                        )

                        LoginState.LOGIN -> LoginScr(
                            modifier = Modifier.padding(innerPadding),
                            back = { vm.loginState.value = LoginState.START },
                        )

                        LoginState.LOGGED_IN -> ListScr(modifier = Modifier.padding(innerPadding))
                        LoginState.REGISTER -> RegisterScr(
                            modifier = Modifier.padding(innerPadding),
                            back = { vm.loginState.value = LoginState.START },
                            toMain = { vm.loginState.value = LoginState.LOGGED_IN },
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, login: () -> Unit, register: () -> Unit) {
    Box(
        modifier.fillMaxSize(),
        Alignment.Center,
    ){
        Column(
            Modifier.width(IntrinsicSize.Min),
            Arrangement.spacedBy(16.dp),
        ) {
            Button(
                onClick = login,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Login")
            }
            Button(
                onClick = register,
            ) {
                Text("Registration")
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UsersListTheme {
        Greeting(login = {}, register = {})
    }
}