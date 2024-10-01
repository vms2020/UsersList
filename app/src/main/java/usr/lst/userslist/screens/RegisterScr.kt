package usr.lst.userslist.screens

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import usr.lst.userslist.db.MyState
import usr.lst.userslist.db.QuUser
import usr.lst.userslist.db.QuUserDatabase
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "RegisterScr"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScr(modifier: Modifier = Modifier, back: () -> Unit, toMain: () -> Unit) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var birthDay by rememberSaveable { mutableStateOf("") }
    var uriStr by rememberSaveable { mutableStateOf<String?>(null) }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var pwdVisibility by rememberSaveable { mutableStateOf(false) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val picFileName = DocumentFile.fromSingleUri(ctx,uri)?.name
                if(picFileName!=null) {
                    val picFile = File(ctx.filesDir, picFileName)
                    ctx.contentResolver.openInputStream(uri)?.use { iStream ->
                        picFile.outputStream().use { oStream ->
                            iStream.copyTo(oStream)
                        }
                    }
                    uriStr=picFile.toUri().toString()
                }

            }
        }

    BackHandler(onBack = back)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    //onDateSelected(datePickerState.selectedDateMillis)
                    birthDay = Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: 0)
                        .atZone(ZoneId.systemDefault()).toLocalDate().toString()

                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier.fillMaxSize(),
        Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        Alignment.CenterHorizontally,
    ) {

        IconButton(
            {
                galleryLauncher.launch("image/*")
            },
        ) {
            if (uriStr == null) {
                Icon(Icons.Default.Person, "choose photo")
            } else {
                Image(
                    BitmapPainter(
                        BitmapFactory.decodeStream(
                            ctx.contentResolver.openInputStream((uriStr?:"").toUri()))
                            .asImageBitmap(),
                    ),
                    "Photo",
                    Modifier.clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        OutlinedTextField(
            name,
            { name = it },
            label = { Text("Your Name") },
            singleLine = true,
        )

        OutlinedTextField(
            birthDay,
            { birthDay = it },
            label = { Text("Your Birth Day") },
            enabled = false,
            leadingIcon = {
                IconButton(
                    { showDatePicker = true }
                ) {
                    Icon(Icons.Default.CalendarMonth, "Select birth day")
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors()
                .copy(
                    disabledTextColor = OutlinedTextFieldDefaults.colors().unfocusedTextColor,
                    disabledLeadingIconColor = OutlinedTextFieldDefaults.colors().unfocusedLeadingIconColor,
                    disabledIndicatorColor = OutlinedTextFieldDefaults.colors().unfocusedIndicatorColor,
                    disabledLabelColor = OutlinedTextFieldDefaults.colors().unfocusedLabelColor,
                )
        )

        OutlinedTextField(
            password,
            { password = it },
            label = { Text("Your Password") },
            visualTransformation = if (pwdVisibility)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                IconButton({
                    pwdVisibility = !pwdVisibility
                }) {
                    Icon(
                        Icons.Default.RemoveRedEye,
                        contentDescription = "",
                        tint = if (pwdVisibility) Color.Green else Color.LightGray
                    )
                }
            },
        )

        Button(
            onClick = {
                if (name.length > 30 || name.length < 4 || password.length < 4 || password.length > 20 || birthDay.length != 10) {
                    Toast.makeText(ctx, "wrong data", Toast.LENGTH_LONG).show()
                    return@Button
                }
                val u = QuUser(
                    0,
                    name,
                    password,
                    birthDay,
                    LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
                    ),
                    uriStr,
                )
                scope.launch(Dispatchers.IO) {
                    QuUserDatabase.getDataBase(ctx).quUserDao().insertAll(u)
                    MyState.saveLoggedInName(u, ctx)
                }
                //MyState.loggedUser.value=u
            },
            enabled = !(name.length > 30 || name.length < 4 || password.length < 4 || password.length > 20 || birthDay.length != 10),
        ) {
            Text("Register")
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

@Preview(showBackground = true)
@Composable
private fun RegisterScrPreview() {
    RegisterScr(
        back = {},
        toMain = {},
    )
}