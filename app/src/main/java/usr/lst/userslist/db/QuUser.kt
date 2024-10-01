package usr.lst.userslist.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(
    indices = [
        Index(
            value = ["name"],
            unique = true,
        ),
    ]

)
data class QuUser(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val password: String,
    val birthDay: String,
    @ColumnInfo(defaultValue = "1980-01-01 00:00:00")
    val registryDateTime: String,
)

@Dao
interface QuUserDao {
    @Query("SELECT * FROM QuUser")
    fun getAll(): Flow<List<QuUser>>

    @Query("SELECT * FROM QuUser ORDER BY registryDateTime ASC")
    fun getAllSortedByRegistry(): Flow<List<QuUser>>

    @Insert(entity = QuUser::class, onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(vararg product: QuUser)

    @Query("SELECT * FROM QuUser WHERE name = :name limit 1")
    fun fetchQuUser(name: String): QuUser

    @Delete
    suspend fun delete(product: QuUser)

    @Query("DELETE FROM QuUser WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query("UPDATE QuUser SET password = :password WHERE name = :name")
    suspend fun changePassword(name: String, password: String)

    @Query("UPDATE QuUser SET birthDay = :birthDay WHERE name = :name")
    suspend fun changeBirthDay(name: String, birthDay: String)
}

@Database(
    entities = [
        QuUser::class,
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],

    )
abstract class QuUserDatabase : RoomDatabase() {
    abstract fun quUserDao(): QuUserDao

    companion object {
        @Volatile
        private var INSTANCE: QuUserDatabase? = null

        fun getDataBase(context: Context): QuUserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuUserDatabase::class.java,
                    "QuUser_database",
                )//.enableMultiInstanceInvalidation()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
