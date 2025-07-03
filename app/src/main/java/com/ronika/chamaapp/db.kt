package com.ronika.chamaapp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phone: String
)


@Entity(
    tableName = "contributions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Defines behavior when a User is deleted
        )
    ],
    indices = [Index(value = ["userId"])] // Index for faster queries on userId
)
data class Contribution(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int, // Foreign key referencing User's id
    val amount: Double,
    val date: Long // Consider using Long for timestamp for easier sorting/querying
)




@Dao
interface AppDao {

    // --- User Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long // Returns the new rowId

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>> // Flow for observing changes

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: Int)

    // --- Contribution Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContribution(contribution: Contribution): Long

    @Query("SELECT * FROM contributions WHERE userId = :userId ORDER BY date DESC")
    fun getContributionsForUser(userId: Int): Flow<List<Contribution>>

    @Query("SELECT * FROM contributions ORDER BY date DESC")
    fun getAllContributions(): Flow<List<Contribution>>

    @Query("DELETE FROM contributions WHERE id = :contributionId")
    suspend fun deleteContributionById(contributionId: Int)

    // --- Relationship Query (Optional but often useful) ---

    // Example: Get User with all their contributions
    // This requires a new data class to hold the result
    // data class UserWithContributions(
    //     @Embedded val user: User,
    //     @Relation(
    //         parentColumn = "id",
    //         entityColumn = "userId"
    //     )
    //     val contributions: List<Contribution>
    // )

    // @Transaction // Ensures atomic operation
    // @Query("SELECT * FROM users WHERE id = :userId")
    // fun getUserWithContributions(userId: Int): Flow<UserWithContributions?>
}



// If you decide to store Date as Long, you'll need a TypeConverter
// @TypeConverters(Converters::class)
@Database(entities = [User::class, Contribution::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // Name of your database file
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}