package br.com.fiap.spaceconnect.data.model

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import br.com.fiap.spaceconnect.domain.model.FavoriteItem
import br.com.fiap.spaceconnect.domain.model.FavoriteType
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type")      val type: String,
    @ColumnInfo(name = "title")     val title: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "subtitle")  val subtitle: String,
    @ColumnInfo(name = "saved_at")  val savedAt: Long = System.currentTimeMillis()
)

fun FavoriteEntity.toDomain() = FavoriteItem(
    id       = id,
    type     = FavoriteType.valueOf(type),
    title    = title,
    imageUrl = imageUrl,
    subtitle = subtitle,
    savedAt  = savedAt
)

fun FavoriteItem.toEntity() = FavoriteEntity(
    id       = id,
    type     = type.name,
    title    = title,
    imageUrl = imageUrl,
    subtitle = subtitle,
    savedAt  = savedAt
)

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY saved_at DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: String)

    @Delete
    suspend fun delete(favorite: FavoriteEntity)
}

@Database(entities = [FavoriteEntity::class], version = 1, exportSchema = false)
abstract class SpaceConnectDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}