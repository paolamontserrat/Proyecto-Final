package com.example.notasymedia.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.notasymedia.data.entity.MultimediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MultimediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(multimedia: MultimediaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLista(multimediaList: List<MultimediaEntity>)

    @Delete
    suspend fun eliminar(multimedia: MultimediaEntity)

    @Query("DELETE FROM multimedia_table WHERE notaId = :notaId")
    suspend fun eliminarPorNotaId(notaId: Int)

    @Query("SELECT * FROM multimedia_table WHERE notaId = :notaId")
    fun obtenerPorNotaId(notaId: Int): Flow<List<MultimediaEntity>>
    
    @Query("SELECT * FROM multimedia_table WHERE notaId = :notaId")
    suspend fun obtenerListaPorNotaId(notaId: Int): List<MultimediaEntity>
}
