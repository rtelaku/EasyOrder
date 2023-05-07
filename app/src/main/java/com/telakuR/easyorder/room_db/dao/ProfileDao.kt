package com.telakuR.easyorder.room_db.dao

import androidx.room.*
import com.telakuR.easyorder.room_db.enitites.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query("SELECT * FROM user_profile_table")
    fun getProfile(): Flow<Profile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProfile(profile: Profile)

    @Transaction
    suspend fun deleteAndInsertProfile(profile: Profile) {
        deleteProfile()
        insertProfile(profile = profile)
    }

    @Query("UPDATE user_profile_table SET companyId = :companyId")
    suspend fun setCompanyId(companyId: String)

    @Query("DELETE FROM user_profile_table")
    suspend fun deleteProfile()
}