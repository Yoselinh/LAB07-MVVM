package com.example.datossinmvvm
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll(): Flow<List<User>>

    @Insert
    suspend fun insert(user: User)
}
