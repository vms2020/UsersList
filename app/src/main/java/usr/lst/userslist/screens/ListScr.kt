package usr.lst.userslist.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import usr.lst.userslist.db.MyState
import usr.lst.userslist.db.QuUserDatabase

@Composable
fun ListScr(modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    val userList by QuUserDatabase.getDataBase(ctx).quUserDao().getAll()
        .collectAsStateWithLifecycle(
            listOf()
        )
    val me by MyState.loggedUser.collectAsStateWithLifecycle()

    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ElevatedCard(
                Modifier.fillMaxWidth(),
            ) {
                Text(
                    "Your name: " + me?.name ?: "",
                    Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                )
                Text(
                    "Your birth day: " + me?.birthDay ?: "",
                    Modifier.padding(start = 8.dp, end = 8.dp)
                )
                Text(
                    "You regitered at: " + me?.registryDateTime ?: "",
                    Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                )
            }
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(16.dp))
        }
        items(
            items = userList,
        ) { u ->
            Card(
                Modifier.fillMaxWidth(),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 8.dp),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically,
                ) {
                    Text(
                        "name: " + u.name,
                    )
                    if ((me?.registryDateTime ?: "") < u.registryDateTime) {
                        IconButton(
                            {
                                scope.launch(Dispatchers.IO) {
                                    QuUserDatabase.getDataBase(ctx).quUserDao().deleteByName(u.name)
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.Delete, "Delete")
                        }
                    }
                }

                Text(
                    "birthday: " + u.birthDay,
                    Modifier.padding(start = 8.dp, end = 8.dp)
                )
                Text(
                    "registered at: " + u.registryDateTime,
                    Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                )
            }

        }
    }
}