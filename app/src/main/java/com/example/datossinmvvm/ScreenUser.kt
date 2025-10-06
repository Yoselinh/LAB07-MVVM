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
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenUser() {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context.applicationContext,
            UserDatabase::class.java,
            "user_db"
        ).fallbackToDestructiveMigration().build()
    }
    val dao = db.userDao()

    val coroutineScope = rememberCoroutineScope()

    var id by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var career by remember { mutableStateOf("") }
    var mostrarLista by remember { mutableStateOf(false) }

    val users by dao.getAll().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CRUD Usuarios") },
                actions = {
                    // Agregar usuario
                    IconButton(onClick = {
                        val ageInt = age.toIntOrNull() ?: 0
                        val user = User(0, firstName, lastName, ageInt, career)
                        coroutineScope.launch { dao.insert(user) }
                        firstName = ""
                        lastName = ""
                        age = ""
                        career = ""
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Usuario")
                    }
                    // Listar usuarios
                    IconButton(onClick = { mostrarLista = !mostrarLista }) {
                        Icon(Icons.Default.List, contentDescription = "Listar Usuarios")
                    }
                    // Eliminar último usuario
                    IconButton(onClick = {
                        coroutineScope.launch {
                            val lastUser = dao.getLastUser()
                            if (lastUser != null) dao.delete(lastUser)
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Último Usuario")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID (solo lectura)") },
                readOnly = true,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Edad") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = career,
                onValueChange = { career = it },
                label = { Text("Carrera") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            if (mostrarLista) {
                Text(
                    text = users.joinToString("\n") {
                        "${it.uid} - ${it.firstName} ${it.lastName}, ${it.age} años, ${it.career}"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
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

suspend fun EliminarUltimoUsuario(dao: UserDao) {
    try {
        val lastUser = dao.getLastUser()
        if (lastUser != null) {
            dao.delete(lastUser)
        }
    } catch (e: Exception) {
        Log.e("User", "Error al eliminar: ${e.message}")
    }
}


