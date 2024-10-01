package usr.lst.userslist.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import usr.lst.userslist.db.LoginState
import usr.lst.userslist.db.MyState
import usr.lst.userslist.db.QuUserDatabase

private const val TAG = "LoginScr"
@Composable
fun LoginScr(modifier: Modifier = Modifier, back: () -> Unit) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var pwdVisibility by rememberSaveable { mutableStateOf(false) }

    BackHandler(onBack = back)

    Column(
        modifier.fillMaxSize(),
        Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            name,
            { name = it },
            label = { Text("Your Name") },
            singleLine = true,
        )

        OutlinedTextField(
            password,
            { password = it },
            label = { Text("Your Password") },
            visualTransformation = if (pwdVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                IconButton({
                    pwdVisibility = !pwdVisibility
                    Log.i(TAG, "LoginScr: pwdVisibility=$pwdVisibility")
                }) {
                    Icon(
                        Icons.Default.RemoveRedEye,
                        contentDescription = "",
                        tint = if(pwdVisibility) Color.Green else Color.LightGray
                    )
                }
            },
        )

        Button(
            {
                scope.launch(Dispatchers.IO) {
                    val u = QuUserDatabase.getDataBase(ctx).quUserDao().fetchQuUser(name)
                    if(u==null){
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(ctx, "no such user: $name", Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }
                    if(u.password!=password){
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(ctx, "wrong password",Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }
                    MyState.saveLoggedInName(u,ctx)
                }
            },
            enabled = !(name.length > 30 || name.length < 4 || password.length < 4 || password.length > 20)
        ) {
            Text("Login")
        }
    }
    Box(
        modifier.fillMaxSize()
    ) {
        TextButton(
            onClick = back,
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) { Text("To Start Screen") }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun LoginScrPreview() {
    LoginScr(back = {})
}