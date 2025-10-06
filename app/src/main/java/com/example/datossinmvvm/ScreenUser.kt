package com.example.datossinmvvm
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.room.Room
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import kotlinx.coroutines.flow.first

@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = crearDatabase(context)
    val dao = db.userDao()
    val coroutineScope = rememberCoroutineScope()

    var userIdText by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var dataUser by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(50.dp))

        TextField(
            value = userIdText,
            onValueChange = { userIdText = it },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name: ") },
            singleLine = true
        )

        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name:") },
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        // Botón para agregar usuario
        Button(
            onClick = {
                val user = User(0, firstName, lastName)
                coroutineScope.launch {
                    agregarUsuario(user, dao)
                }
                firstName = ""
                lastName = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Usuario", fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))

        // Botón para listar usuarios
        Button(
            onClick = {
                coroutineScope.launch {
                    val users = dao.getAll().first()
                    dataUser = users.joinToString("\n") { user ->
                        "${user.uid} - ${user.firstName} - ${user.lastName}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Listar Usuarios", fontSize = 16.sp)
        }




        Spacer(Modifier.height(24.dp))

        // Texto que muestra la lista
        Text(
            text = dataUser,
            fontSize = 20.sp
        )
    }
}


@Composable
fun crearDatabase(context: Context): UserDatabase {
    return Room.databaseBuilder(
        context,
        UserDatabase::class.java,
        "user_db"
    ).build()
}

suspend fun agregarUsuario(user: User, dao: UserDao) {
    try {
        dao.insert(user)
    } catch (e: Exception) {
        Log.e("User", "Error insert: ${e.message}")
    }
}



